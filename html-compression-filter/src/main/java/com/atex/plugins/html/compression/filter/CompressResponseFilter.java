package com.atex.plugins.html.compression.filter;

import com.atex.plugins.html.compression.ConfigPolicy;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.googlecode.htmlcompressor.compressor.YuiJavaScriptCompressor;
import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.servlet.RequestPreparator;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class CompressResponseFilter implements Filter {

    private static final String FIRST_TIME = CompressResponseFilter.class.getCanonicalName()+".processed";
    private HtmlCompressor compressor;
    private CmClient cmClient;
    private static final Logger LOGGER = Logger.getLogger(CompressResponseFilter.class.getName());



    private Double warnSize = null;
    private volatile int numErrors = 0;
    static final Pattern PREVIEW_RE = Pattern.compile("^/preview/((?!polopoly_fs)[^/]+/)?\\d+\\.\\d+");

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp,
                         final FilterChain chain) throws IOException, ServletException {

        ServletResponse responseWrapper = resp;

        boolean doCompression = (compressor != null) && (req.getAttribute(FIRST_TIME) == null) && !ignoreRequest((HttpServletRequest) req);

        if (doCompression) {
            responseWrapper = new CharResponseWrapper((HttpServletResponse) resp);
        }
        req.setAttribute(FIRST_TIME,true);

        chain.doFilter(req, responseWrapper);

        if (doCompression) {
            if (!responseWrapper.isCommitted()) {
                if (responseWrapper.getContentType() != null && responseWrapper.getContentType().startsWith("text/html")) {
                    try {
                        String servletResponse = ((CharResponseWrapper) responseWrapper).getCaptureAsString();
                        String compressed = compressor.compress(servletResponse);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        baos.write(compressed.getBytes());
                        double compressedSize = baos.size() / ((double) 1024 * 1024);

                        if (warnSize > 0.0 && compressedSize > warnSize) {
                            LOGGER.log(Level.WARNING, String.format("Page output greater than %s mb limit for "
                                    + "path page %s", warnSize, ((HttpServletRequest) req).getRequestURL().toString()));
                        }
                        resp.getWriter().write(compressed);
                        numErrors = numErrors / 5;
                    } catch (Exception e) {
                        numErrors++;
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        if (numErrors > 200) {
                            LOGGER.warning("Disabling HTML Compression due to number of errors");
                            compressor = null;
                        }
                    }
                } else {
                    // We captured it, so we better release it into the wild...
                    responseWrapper.flushBuffer();
                    resp.getOutputStream().write(((CharResponseWrapper) responseWrapper).getCaptureAsBytes());
                    responseWrapper.getOutputStream().close();
                }
            }
        }
    }

    private boolean ignoreRequest(final HttpServletRequest request) {
        if (RequestPreparator.getIgnoreRequest(request)) {

            // it looks like the "/preview" url is ignored, since we need
            // to enable the html compression for those url anyway

            final String requestURI = request.getRequestURI();
            return !PREVIEW_RE.matcher(requestURI).find();
        }
        return false;
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {

        ConfigPolicy configPolicy = null;
        try {
            Application app = ApplicationServletUtil.getApplication(config.getServletContext());
            cmClient = app.getPreferredApplicationComponent(CmClient.class);
            PolicyCMServer policyCmServer = cmClient.getPolicyCMServer();
            configPolicy = ConfigPolicy.getInstance(policyCmServer);
            warnSize = configPolicy.getWarningSize();

            boolean compressionEnabled = configPolicy.isCompressionEnabled();

            if (compressionEnabled) {
                LOGGER.info ("HTML Compression Enabled, warning size = " + warnSize);
                compressor = new HtmlCompressor();
                compressor.setCompressCss(true);
                compressor.setJavaScriptCompressor(new YuiJavaScriptCompressor());
                compressor.setCompressJavaScript(false);
                compressor.setRemoveIntertagSpaces(true);
                compressor.setRemoveMultiSpaces(true);
            }
        } catch (IllegalApplicationStateException e) {
            throw new ServletException("Failed to get CmClient", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            compressor = null;
        }

    }

    @Override
    public void destroy() {
    }

}

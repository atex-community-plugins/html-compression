package com.atex.plugins.html.compression.filter;

import com.atex.plugins.html.compression.ConfigPolicy;
import com.google.javascript.jscomp.CompilationLevel;
import com.googlecode.htmlcompressor.compressor.ClosureJavaScriptCompressor;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompressResponseFilter implements Filter {

    private HtmlCompressor compressor;
    private CmClient cmClient;
    private static final Logger LOGGER = Logger.getLogger(CompressResponseFilter.class.getName());

    public static final String TEMPLATE = "com.atex.plugins.htmlCompression.ConfigHome";
    public static final ContentId CONTENT_ID = new ExternalContentId(TEMPLATE);

    private Double warnSize = null;

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp,
                         final FilterChain chain) throws IOException, ServletException {

        CharResponseWrapper responseWrapper = new CharResponseWrapper(
                (HttpServletResponse) resp);

        chain.doFilter(req, responseWrapper);

        String servletResponse = new String(responseWrapper.toString());
        String compressed = compressor.compress(servletResponse);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(compressed.getBytes());
        double compressedSize = baos.size() / ((double) 1024 * 1024);

        if (warnSize > 0.0 && compressedSize > warnSize) {
            LOGGER.log(Level.WARNING, String.format("Page output greater than %s mb limit for "
                    + "path page %s", warnSize, ((HttpServletRequest) req).getRequestURL().toString()));
        }
        resp.getWriter().write(compressed);
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {

        ConfigPolicy configPolicy = null;
        try {
            Application app = ApplicationServletUtil.getApplication(config.getServletContext());
            cmClient = app.getPreferredApplicationComponent(CmClient.class);
            PolicyCMServer policyCmServer = cmClient.getPolicyCMServer();
            configPolicy = (ConfigPolicy) policyCmServer.getPolicy(CONTENT_ID);
            warnSize = configPolicy.getWarningSize();
        } catch (IllegalApplicationStateException e) {
            throw new ServletException("Failed to get CmClient", e);
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        boolean compressionEnabled = configPolicy.isCompressionEnabled();

        if (compressionEnabled) {
            compressor = new HtmlCompressor();
            compressor.setCompressCss(true);
            compressor.setJavaScriptCompressor(new ClosureJavaScriptCompressor(CompilationLevel.SIMPLE_OPTIMIZATIONS));
            compressor.setCompressJavaScript(true);
        }
    }

    @Override
    public void destroy() {
    }

}

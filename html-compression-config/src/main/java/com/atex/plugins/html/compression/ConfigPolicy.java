package com.atex.plugins.html.compression;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.app.policy.CheckboxPolicy;
import com.polopoly.cm.app.policy.SingleValued;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.ContentPolicy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.model.DescribesModelType;

import java.util.logging.Level;
import java.util.logging.Logger;

@DescribesModelType
public class ConfigPolicy extends ContentPolicy {

    private static final String ENABLED = "enabled";
    private static final String WARN_SIZE = "warnSize";
    private static final Logger LOGGER = Logger.getLogger(ConfigPolicy.class.getName());

    public static final ContentId CONTENT_ID = new ExternalContentId("plugins.com.atex.plugins.html-compression-config.Config");

    @Override
    protected void initSelf() {
        super.initSelf();
    }

    public boolean isCompressionEnabled() {
        return getCheckboxValue(ENABLED, true);
    }

    private boolean getCheckboxValue(final String name, final boolean defaultValue) {
        try {
            final CheckboxPolicy check = (CheckboxPolicy) getChildPolicy(name);
            if (check != null) {
                return check.getChecked();
            }
        } catch (final CMException e) {
            LOGGER.log(Level.SEVERE, "Cannot get " + name + " value: " + e.getMessage(), e);
        }
        return defaultValue;
    }

    public Double getWarningSize() throws CMException {
        double defaultValue = 0.0;
        final SingleValued child = (SingleValued) getChildPolicy(WARN_SIZE);
        if (child == null) {
            return defaultValue;
        }
        return child.getValue() != null ? Double.parseDouble(child.getValue()) : defaultValue;
    }

    public static ConfigPolicy getInstance(PolicyCMServer policyCmServer) throws CMException {
        return (ConfigPolicy) policyCmServer.getPolicy(CONTENT_ID);
    }
}

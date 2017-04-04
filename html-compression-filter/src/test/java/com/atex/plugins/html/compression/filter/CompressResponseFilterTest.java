package com.atex.plugins.html.compression.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * A simple unit test for {@link CompressResponseFilter}.
 *
 * @author mnova
 */
public class CompressResponseFilterTest {

    @Test
    public void test1() {

        final String[] strings = new String[] {
                "/preview/2.210",
                "/preview/www/2.210#device=window",
                "/preview/polopoly_fs/3.208.1479810100!/video-js.css",
                "/preview/www/2.210/2.245/2.317",
                "/preview/polopoly_fs/3.189.1480673054!/jquery.cookie.js"
        };

        final Boolean[] expecteds = new Boolean[] {
                true,
                true,
                false,
                true,
                false
        };

        Pattern re = CompressResponseFilter.PREVIEW_RE;
        t(re, strings, expecteds);
    }

    private void t(final Pattern re, final String[] strings, final Boolean[] expecteds) {
        for (int i = 0; i < strings.length; i++) {
            Assert.assertEquals(strings[i], expecteds[i], t(re, strings[i]));
        }
    }

    private boolean t(final Pattern re, final String s) {
        final Matcher m = re.matcher(s);
        return m.find();
    }

}
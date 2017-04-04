html-compression
================

This plugin uses a Servlet filter to handle the default response back from rendering velocity templates and compresses the HTML to remnove extraneuous whitespace etc.
It will ignore anything that the CM Servlet is defined as ignoring by using RequestPrepator.getIgnoreRequest on the incoming request.


In order for the html compression to be available you need to add the configuration depdendecncy to your top POM.
```
    <dependency>
        <groupId>com.atex.plugins</groupId>
        <artifactId>html-compression-config</artifactId>
        <version>1.0.1-SNAPSHOT</version>
      <classifier>contentdata</classifier>
    </dependency>
    <dependency>
      <groupId>com.atex.plugins</groupId>
      <artifactId>html-compression-config</artifactId>
      <version>1.0.1-SNAPSHOT</version>
    </dependency>
```

And to webapp-dispatcher add the following dependendancy to ensure that the web-fragment.xml is added to the project.

```
    <dependency>
      <groupId>com.atex.plugins</groupId>
      <artifactId>html-compression-filter</artifactId>
      <version>1.0.1-SNAPSHOT</version>
    </dependency>
```

To disable/enable the plugin or add a warning for output limit go to plugins configurations under the Root Deparment plugins configurations,
after that a container restart is required.

## Polopoly Version
10.16.2

## Code Status
The code in this repository is provided with the following status: **EXAMPLE**

Under the open source initiative, Atex provides source code for plugin with different levels of support. There are three different levels of support used. These are:

- EXAMPLE  
The code is provided as an illustration of a pattern or blueprint for how to use a specific feature. Code provided as is.

- PROJECT  
The code has been identified in an implementation project to be generic enough to be useful also in other projects. This means that it has actually been used in production somewhere, but it comes "as is", with no support attached. The idea is to promote code reuse and to provide a convenient starting point for customization if needed.

- PRODUCT  
The code is provided with full product support, just as the core Polopoly product itself.
If you modify the code (outside of configuraton files), the support is voided.

## Alternative Approach
Add the following to velocity.properties

userdirective=com.polopoly.cm.servlet.velocity.directives.FileDirective, \
  com.polopoly.cm.servlet.velocity.directives.LinkDirective, \
  com.polopoly.cm.servlet.velocity.directives.RenderDirective, \
  com.polopoly.cm.servlet.velocity.directives.LogDirective, \
  com.polopoly.cm.servlet.velocity.directives.ImageDirective, \
  com.polopoly.cm.servlet.velocity.directives.ImageResourceDirective, \
  com.polopoly.cm.servlet.velocity.directives.ArticleHitDirective, \
  com.polopoly.cm.servlet.velocity.directives.RenderWithFallbackDirective, \
  com.googlecode.htmlcompressor.velocity.HtmlCompressorDirective

userdirective.compressHtml.removeIntertagSpaces = true
userdirective.compressHtml.compressCss = true

and then wrap the HTML you want to compress with #renderHTML ()

See https://code.google.com/archive/p/htmlcompressor/ for more details.


## License
Atex Polopoly Source Code License
Version 1.0 February 2012

See file **LICENSE** for details

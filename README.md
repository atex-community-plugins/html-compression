html-compression
===============

In order for the html compression to be available web application you need to modify your web.xml and add the html compression filter that comes with the plugin. The filter definition is available in a file called web-fragment.xml but you can also take it from here.
```
<filter>
    <filter-name>htmlCompression</filter-name>
    <filter-class>com.atex.plugins.html.compression.filter.CompressResponseFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>htmlCompression</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

Also add dependecies :

```
<dependency>
  <groupId>javax</groupId>
  <artifactId>javaee-api</artifactId>
  <version>6.0</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>com.googlecode.htmlcompressor</groupId>
  <artifactId>htmlcompressor</artifactId>
  <version>1.5.2</version>
</dependency>
<dependency>
  <groupId>com.yahoo.platform.yui</groupId>
  <artifactId>yuicompressor</artifactId>
  <version>2.4.7</version>
</dependency>
<dependency>
  <groupId>com.google.javascript</groupId>
  <artifactId>closure-compiler</artifactId>
  <version>v20131014</version>
</dependency>

```

To disable/enable the plugin or add a warning for output limit go to plugins configurations under the Root Deparment plugins configurations,
after that a container restart is required.

## Polopoly Version
10.16.0-fp1

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


## License
Atex Polopoly Source Code License
Version 1.0 February 2012

See file **LICENSE** for details

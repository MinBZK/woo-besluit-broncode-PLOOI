<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:import href="../common/dlogger.xsl"/>

  <xsl:param name="config:development-mode" select="false()" as="xs:boolean"/>
  <xsl:param name="config:debug" select="/*/req:parameters/req:parameter[@name='debug']/req:value = 'true'" as="xs:boolean?"/>
  <xsl:param name="config:version" as="xs:string?"/>
  <xsl:param name="config:cache-buster-qs" as="xs:string?"/>

  <xsl:variable name="context-path" select="/*/req:context-path || /*/req:webapp-path" as="xs:string"/>
  <xsl:variable name="query-params" select="/*/req:parameters/req:parameter" as="element(req:parameter)*"/>
  <xsl:variable name="cb-context-path" select="/*/req:context-path || '/cb-common' || '/' || $cb-version" as="xs:string"/>

  <xsl:template match="/">
    <resp:response>
      <xsl:variable name="status" as="element(status)">
        <!-- HTTP status code and message can be determined in importing stylesheets by implementing named template "status": -->
        <xsl:call-template name="status"/>
      </xsl:variable>
      <xsl:attribute name="status" select="$status/@code"/>
      <xsl:choose>
        <xsl:when test="$status/xs:integer(@code) = (301,302,303)">
          <!-- Send redirect header: -->
          <resp:headers>
            <resp:header name="location">
              <xsl:value-of select="$status/@location"/>
            </resp:header>
          </resp:headers>
        </xsl:when>
        <!--
        <xsl:when test="not($status/xs:integer(@code) = 200)">
          <resp:body>
            <html>
              <body>
                <xsl:value-of select="$status/@message"/>
              </body>
            </html>
          </resp:body>
        </xsl:when>
        -->
        <xsl:otherwise>
          <resp:headers>
            <resp:header name="Content-Type">text/html; charset=utf-8</resp:header>
            <resp:header name="X-Content-Type-Options">nosniff</resp:header>
            <resp:header name="Strict-Transport-Security">max-age=1000; includeSubDomains; preload</resp:header>
            <resp:header name="Content-Security-Policy"> upgrade-insecure-requests</resp:header>
<!--        <resp:header name="Content-Security-Policy">default-src self; frame-ancestors self;</resp:header>
               This header prevents the download of images and other layout related items.

              Using CSP, you can nail down what your site should include and what not. But it is hard and can break your site
              Using the Content-Security-Policy-Report-Only mode browsers only log resources that would be blocked
              to the console instead of blocking them. This reporting mechanism gives you a way to check and adjust your ruleset.
              <resp:header name="Content-Security-Policy-Report-Only"> default-src 'self'; ... report-uri https://reporting URI</resp:header>
           -->
            <resp:header name="Cache-Control">max-age=30, public, immutable</resp:header>
            <resp:header name="Referrer-Policy">same-origin</resp:header>
            <resp:header name="X-Frame-Options">SAMEORIGIN</resp:header>
          </resp:headers>
          <resp:body>
            <html lang="nl">
              <head>
                <title>
                  <!-- HTML page title can be determined in importing stylesheets by implementing named template "title": -->
                  <xsl:call-template name="title"/>
                </title>
                <xsl:text>
</xsl:text>
                <script>document.documentElement.className = 'has-js';</script>
                <xsl:text>
</xsl:text>
                <!-- Extra meta tags can be generated in (multilevel) importing stylesheets by implementing a template matching on "metas": -->
                <xsl:variable name="metas" as="element(metas)">
                  <metas/>
                </xsl:variable>
                <xsl:apply-templates select="$metas" mode="meta"/>
                <link rel="stylesheet" title="default" href="{$cb-context-path}/css/transition.css{$config:cache-buster-qs}"/>
                <link rel="stylesheet" href="{$cb-context-path}/css/print.css{$config:cache-buster-qs}"/>
                <link rel="shortcut icon" href="{$cb-context-path}/images/favicon.ico{$config:cache-buster-qs}" type="image/vnd.microsoft.icon"/>
                <!-- Extra link tags can be generated in (multilevel) importing stylesheets by implementing a template matching on "links": -->
                <xsl:variable name="links" as="element(links)">
                  <links/>
                </xsl:variable>
                <xsl:apply-templates select="$links" mode="link"/>
              </head>
              <body class="preview">
                <xsl:call-template name="header"/>

                <xsl:call-template name="breadcrumb"/>

                <xsl:call-template name="main"/>

                <xsl:call-template name="footer"/>

                <script src="{$cb-context-path}/js/main.js{$config:cache-buster-qs}"></script>
                <!-- Extra script tags can be generated in (multilevel) importing stylesheets by implementing a template matching on "scripts": -->
                <xsl:variable name="scripts" as="element(scripts)*">
                  <scripts/>
                </xsl:variable>
                <xsl:apply-templates select="$scripts" mode="script"/>
              </body>
              <xsl:comment>
                <xsl:value-of select="$config:version"/>
              </xsl:comment>
            </html>
          </resp:body>
        </xsl:otherwise>
      </xsl:choose>
    </resp:response>
  </xsl:template>

  <xsl:template name="status" as="element(status)">
    <!-- Default HTTP status and message: -->
    <status code="200" message=""/>
  </xsl:template>

  <xsl:template match="metas" as="element(meta)*" mode="meta">
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport"     content="width=device-width,initial-scale=1"/>
  </xsl:template>

  <xsl:template match="links" as="element(link)*" mode="link"/>

  <xsl:template match="scripts" as="element(script)*" mode="script"/>

  <xsl:template name="title" as="xs:string">Untitled</xsl:template>

  <xsl:template name="selected-nav-id" as="xs:string">unknown</xsl:template>

  <xsl:template name="header" as="element(cl:header)">
    <cl:header>
      <cl:logo>
        <cl:file>
          <xsl:value-of select="$cb-context-path || '/images/logo.svg'"/>
        </cl:file>
        <cl:alt>logo.svg</cl:alt>
        <cl:label-you-are-here>U bent hier:</cl:label-you-are-here>
        <cl:text>Hello World</cl:text>
        <cl:href>https://www.overheid.nl</cl:href>
      </cl:logo>
      <cl:hamburgerButtonText>Menu</cl:hamburgerButtonText>
      <cl:nav>true</cl:nav>
      <cl:navLinks>
        <cl:link>
          <cl:id>zoeken-1</cl:id>
          <cl:url>#</cl:url>
          <cl:text>Zoeken</cl:text>
          <!--
          <cl:modifier>modifier</cl:modifier>
          <cl:className></cl:className>
          <cl:action></cl:action>
          -->
        </cl:link>
        <cl:link>
          <cl:id>zoeken-2</cl:id>
          <cl:url>#</cl:url>
          <cl:text>Zoeken</cl:text>
          <!--
          <cl:modifier>modifier</cl:modifier>
          <cl:className></cl:className>
          <cl:action></cl:action>
          -->
        </cl:link>
      </cl:navLinks>
      <cl:skipLinks>
        <cl:link>
          <cl:url>#content</cl:url>
          <cl:text>Direct naar content</cl:text>
        </cl:link>
        <cl:link>
          <cl:url>#search</cl:url>
          <cl:text>Direct zoeken</cl:text>
        </cl:link>
      </cl:skipLinks>
      <!--
      <cl:metaLinks>
        <cl:link>
          <cl:url>#</cl:url>
          <cl:text>Metalink 1</cl:text>
        </cl:link>
        <cl:link>
          <cl:url>#</cl:url>
          <cl:text>Metalink 2</cl:text>
        </cl:link>
      </cl:metaLinks>
      -->
      <cl:headerMore>
        <cl:item>
          <cl:title>Aankondigingen over uw buurt</cl:title>
          <cl:description>Zoals bouwplannen en verkeersmaatregelen.</cl:description>
          <cl:link>
            <cl:url>https://beta.overheid.nl/aankondigingen-over-uw-buurt</cl:url>
            <cl:text>Naar aankondigingen over uw buurt</cl:text>
          </cl:link>
        </cl:item>
        <cl:item>
          <cl:title>Dienstverlening</cl:title>
          <cl:description>Zoals belastingen, uitkeringen en subsidies.</cl:description>
          <cl:link>
            <cl:url>https://beta.overheid.nl/dienstverlening</cl:url>
            <cl:text>Naar dienstverlening</cl:text>
          </cl:link>
        </cl:item>
        <cl:item>
          <cl:title>Beleid &amp; regelgeving</cl:title>
          <cl:description>Officiële publicaties van de overheid.</cl:description>
          <cl:link>
            <cl:url>https://beta.overheid.nl/beleid-en-regelgeving</cl:url>
            <cl:text>Naar beleid &amp; regelgeving</cl:text>
          </cl:link>
        </cl:item>
        <cl:item>
          <cl:title>Contactgegevens overheden</cl:title>
          <cl:description>Adressen en contactpersonen van overheidsorganisaties.</cl:description>
          <cl:link>
            <cl:url>https://almanak.overheid.nl/onl</cl:url>
            <cl:text>Naar overheidsorganisaties</cl:text>
          </cl:link>
        </cl:item>
      </cl:headerMore>
    </cl:header>
  </xsl:template>

  <xsl:template name="breadcrumb" as="element(cl:breadcrumb)?">
    <xsl:message terminate="yes">Named template "breadcrumb" must be overriden in importing stylesheet</xsl:message>
  </xsl:template>

  <xsl:template name="main" as="element(main)?">
    <xsl:message terminate="yes">Named template "main" must be overriden in importing stylesheet</xsl:message>
  </xsl:template>

  <xsl:template name="footer" as="element(cl:footer)*">
    <cl:footer>
      <cl:list>
        <cl:ordered-list>false</cl:ordered-list>
        <cl:modifier>list--linked</cl:modifier>
        <cl:items>
          <cl:item><a href="https://www.overheid.nl/over-deze-site">Over deze website</a></cl:item>
          <cl:item><a href="https://www.overheid.nl/contact/reageren-op-wetten">Contact</a></cl:item>
          <cl:item><a href="https://www.overheid.nl/english" lang="en">English</a></cl:item>
          <cl:item><a href="https://www.overheid.nl/help/plooi">Help</a></cl:item>
          <cl:item><a href="https://www.overheid.nl/help/zoeken">Zoeken</a></cl:item>
        </cl:items>
      </cl:list>
      <cl:list>
        <cl:ordered-list>false</cl:ordered-list>
        <cl:modifier>list--linked</cl:modifier>
        <cl:items>
          <cl:item><a href="https://www.overheid.nl/informatie-hergebruiken">Informatie hergebruiken</a></cl:item>
          <cl:item><a href="https://www.overheid.nl/privacy-statement">Privacy en cookies</a></cl:item>
          <cl:item><a href="https://www.overheid.nl/toegankelijkheid">Toegankelijkheid</a></cl:item>
          <cl:item><a href="https://www.overheid.nl/sitemap">Sitemap</a></cl:item>
        </cl:items>
      </cl:list>
      <cl:list>
        <cl:ordered-list>false</cl:ordered-list>
        <cl:modifier>list--linked</cl:modifier>
        <cl:items>
          <cl:item><a href="https://data.overheid.nl/">Open data</a></cl:item>
          <cl:item><a href="https://linkeddata.overheid.nl/">Linked Data Overheid</a></cl:item>
          <cl:item><a href="https://puc.overheid.nl/">PUC Open Data</a></cl:item>
        </cl:items>
      </cl:list>
      <cl:list>
        <cl:ordered-list>false</cl:ordered-list>
        <cl:modifier>list--linked</cl:modifier>
        <cl:items>
          <cl:item><a href="https://mijn.overheid.nl">MijnOverheid.nl</a></cl:item>
          <cl:item><a href="https://www.rijksoverheid.nl">Rijksoverheid.nl</a></cl:item>
          <cl:item><a href="https://ondernemersplein.nl">Ondernemersplein.nl</a></cl:item>
          <cl:item><a href="https://www.werkenbijdeoverheid.nl">Werkenbijdeoverheid.nl</a></cl:item>
        </cl:items>
      </cl:list>
    </cl:footer>
  </xsl:template>

</xsl:stylesheet>
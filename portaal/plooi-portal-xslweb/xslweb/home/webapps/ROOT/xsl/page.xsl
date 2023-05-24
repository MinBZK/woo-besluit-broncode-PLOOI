<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:functx="http://www.functx.com"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:param name="cb-version" as="xs:string?" select="'2.0.0'"/>

  <xsl:param name="config:sru-endpoint" as="xs:string"/>
  <xsl:param name="config:sru-x-connection" as="xs:string"/>
  <xsl:param name="config:valuelist-endpoint" as="xs:string"/>
  <xsl:param name="config:webapp-dir" as="xs:string" required="yes"/>

  <xsl:param name="config:valuelist-label-type" as="xs:string*" required="yes"/>
  <xsl:param name="config:valuelist-label-thema" as="xs:string*" required="yes"/>
  <xsl:param name="config:valuelist-labels-organisation" as="xs:string*" required="yes"/>

  <xsl:import href="../../cb-common/xsl/2.0.0/page.xsl"/>
  <xsl:import href="../../cb-common/xsl/common/sru-search.xsl"/>
  <xsl:import href="../../cb-common/xsl/common/utils.xsl"/>
  <xsl:import href="../../cb-common/xsl/common/functx-1.0.xsl"/>
  <xsl:import href="cache.xsl"/>
  <xsl:import href="index-def.xsl"/>

  <xsl:mode name="header" on-no-match="shallow-copy"/>

  <xsl:variable name="apos" as="xs:string">'</xsl:variable>
  <xsl:variable name="quot" as="xs:string">"</xsl:variable>
  <xsl:variable name="path" select="/*/req:path" as="xs:string"/>
  <xsl:variable name="valuelist-label-type" select="$config:valuelist-label-type" as="xs:string*"/>
  <xsl:variable name="valuelist-label-thema" select="$config:valuelist-label-thema" as="xs:string*"/>
  <xsl:variable name="valuelist-labels-organisation" select="$config:valuelist-labels-organisation" as="xs:string*"/>
  <xsl:variable name="active-session-id" select="/*/req:cookies/req:cookie[req:name = 'actieveSessieID']/req:value" as="xs:string?"/>
  <xsl:decimal-format name="EU" decimal-separator="," grouping-separator="."/>
  <xsl:variable name="decimal-format-pattern" as="xs:string">#.###</xsl:variable>

  <!-- Add extra css links to page: -->
  <xsl:template match="links" as="element(link)*" mode="link">
    <xsl:next-match/>
    <link rel="stylesheet" href="{$context-path}/css/plooi.css{$config:cache-buster-qs}"/>
  </xsl:template>

  <!-- Add extra javascripts to page: -->
  <xsl:template match="scripts" as="element(script)*" mode="script">
    <xsl:next-match/>
    <script src="{$cb-context-path}/js/sessionStorage.js{$config:cache-buster-qs}"/>
    <script src="{$context-path}/js/plooi.js{$config:cache-buster-qs}"/>
  </xsl:template>

  <xsl:template name="header" as="element(cl:header)">
    <xsl:variable name="header" as="element(cl:header)">
      <cl:header>
        <cl:logo>
          <cl:file>
            <xsl:value-of select="$cb-context-path || '/images/logo.svg' || $config:cache-buster-qs"/>
          </cl:file>
          <cl:alt>logo.svg</cl:alt>
          <cl:label-you-are-here>U bent hier:</cl:label-you-are-here>
          <cl:text>Platform open overheidsinformatie</cl:text>
          <cl:href>https://www.overheid.nl</cl:href>
        </cl:logo>
        <cl:hamburgerButtonText>Menu</cl:hamburgerButtonText>
        <cl:collapse-other-sites>true</cl:collapse-other-sites>
        <cl:nav>true</cl:nav>
        <cl:navLinks>
          <xsl:variable name="selected-nav-id" as="xs:string">
            <xsl:call-template name="selected-nav-id"/>
          </xsl:variable>
          <cl:link>
            <cl:id>home</cl:id>
            <cl:url>
              <xsl:value-of select="$context-path || '/'"/>
            </cl:url>
            <cl:text>Home</cl:text>
          </cl:link>
          <cl:link>
            <cl:id>searchform</cl:id>
            <cl:url>
              <xsl:value-of select="$context-path || '/uitgebreid-zoeken'"/>
            </cl:url>
            <cl:text>Uitgebreid zoeken</cl:text>
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
        <xsl:variable name="qs" select="if (/*/req:query-string/text()) then '?' || /*/req:query-string else /*/req:query-string" as="xs:string?"/>
        <cl:headerMore>
          <cl:item>
            <cl:title>Aankondigingen over uw buurt</cl:title>
            <cl:description>Zoals bouwplannen en verkeersmaatregelen.</cl:description>
            <cl:link>
              <cl:url>https://www.overheid.nl/aankondigingen-over-uw-buurt</cl:url>
              <cl:text>Naar aankondigingen over uw buurt</cl:text>
            </cl:link>
          </cl:item>
          <cl:item>
            <cl:title>Dienstverlening</cl:title>
            <cl:description>Zoals belastingen, uitkeringen en subsidies.</cl:description>
            <cl:link>
              <cl:url>https://www.overheid.nl/dienstverlening</cl:url>
              <cl:text>Naar dienstverlening</cl:text>
            </cl:link>
          </cl:item>
          <cl:item>
            <cl:title>Beleid &amp; regelgeving</cl:title>
            <cl:description>Officiële publicaties van de overheid.</cl:description>
            <cl:link>
              <cl:url>https://www.overheid.nl/beleid-en-regelgeving</cl:url>
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
    </xsl:variable>
    <xsl:apply-templates select="$header" mode="header"/>
  </xsl:template>

  <xsl:template name="footer" as="element()*">
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

  <xsl:template match="cl:navLinks/cl:link[cl:id = cl:get-selected-nav-id()]" mode="header">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
      <cl:className>is-active</cl:className>
    </xsl:copy>
  </xsl:template>

  <xsl:function name="local:format-number" as="xs:string?">
    <xsl:param name="value" as="xs:numeric?"/>
    <xsl:value-of select="format-number($value, $decimal-format-pattern, 'EU')"/>
  </xsl:function>

</xsl:stylesheet>

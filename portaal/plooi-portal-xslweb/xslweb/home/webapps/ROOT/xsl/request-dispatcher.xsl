<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:pipeline="http://www.armatiek.com/xslweb/pipeline"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  exclude-result-prefixes="#all"
  version="2.0">

  <xsl:param name="cb-version" as="xs:string?" select="'2.0.0'"/>

  <xsl:variable name="query-params" select="/*/req:parameters/req:parameter" as="element(req:parameter)*"/>

  <xsl:template match="/">
    <pipeline:pipeline>
      <xsl:apply-templates/>
    </pipeline:pipeline>
  </xsl:template>

  <xsl:template match="/req:request[req:method='GET' and req:path = '/']">
    <pipeline:pipeline>
      <xsl:call-template name="xss-filter"/>
      <pipeline:transformer name="decompress-uris" xsl-path="decompress-uris.xsl" log="false"/>
      <pipeline:transformer name="home" xsl-path="home.xsl" log="false"/>
      <xsl:call-template name="cl"/>
    </pipeline:pipeline>
  </xsl:template>

  <xsl:template match="/req:request[req:method='GET' and matches(req:path, '^/(ZoekUitgebreid|uitgebreid-zoeken)')]">
    <pipeline:pipeline>
      <xsl:call-template name="xss-filter"/>
      <pipeline:transformer name="decompress-uris" xsl-path="decompress-uris.xsl" log="false"/>
      <pipeline:transformer name="searchform" xsl-path="searchform.xsl" log="false"/>
      <xsl:call-template name="cl"/>
    </pipeline:pipeline>
  </xsl:template>

  <xsl:template match="/req:request[req:method='GET' and matches(req:path, '^/(ZoekUitgebreidResultaat|zoekresultaten)')]" priority="2.0">
    <xsl:call-template name="search-query-params"/>
    <pipeline:pipeline>
      <xsl:call-template name="xss-filter"/>
      <pipeline:transformer name="decompress-uris" xsl-path="decompress-uris.xsl" log="false"/>
      <pipeline:transformer name="searchresult" xsl-path="searchresult.xsl" log="false"/>
      <xsl:call-template name="cl"/>
    </pipeline:pipeline>
  </xsl:template>

  <xsl:template match="/req:request[req:method='GET' and matches(req:path, '^/Details/([^/]+)/(\d+)/?(\.html)?/?$')]">
    <xsl:call-template name="search-query-params"/>
    <pipeline:pipeline>
      <xsl:call-template name="xss-filter"/>
      <pipeline:transformer name="decompress-uris" xsl-path="decompress-uris.xsl" log="false"/>
      <pipeline:transformer name="doc-resolver" xsl-path="doc-resolver.xsl" log="false"/>
      <pipeline:transformer name="detail" xsl-path="detail.xsl" log="false"/>
      <pipeline:transformer name="nav" xsl-path="nav.xsl" log="false"/>
      <xsl:call-template name="cl"/>
    </pipeline:pipeline>
  </xsl:template>

  <xsl:template match="/req:request[req:method='GET' and starts-with(req:path, '/repository/') and not(contains(req:path, '../'))]">
    <pipeline:pipeline>
      <pipeline:transformer name="repository-file" xsl-path="repository-file.xsl" log="false"/>
      <pipeline:resource-serializer name="resource" log="false"/>
    </pipeline:pipeline>
  </xsl:template>

  <xsl:template match="/req:request[req:method='GET' and req:path = '/uri-check']">
    <pipeline:pipeline>
      <xsl:call-template name="xss-filter"/>
      <pipeline:transformer name="uri-check" xsl-path="uri-check.xsl" log="false"/>
      <xsl:call-template name="cl"/>
    </pipeline:pipeline>
  </xsl:template>

  <!--
  <xsl:template match="/req:request[req:method='GET' and req:path = '/test086']">
    <pipeline:pipeline>
      <xsl:call-template name="xss-filter"/>
      <pipeline:transformer name="test" xsl-path="test.xsl" log="false"/>
      <xsl:call-template name="cl"/>
    </pipeline:pipeline>
  </xsl:template>
  -->

  <xsl:template match="/req:request" priority="-1.0">
    <pipeline:pipeline>
      <xsl:call-template name="xss-filter"/>
      <pipeline:transformer name="error" xsl-path="error.xsl" log="false">
        <pipeline:parameter name="error-code" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
          <pipeline:value>404</pipeline:value>
        </pipeline:parameter>
      </pipeline:transformer>
      <xsl:call-template name="cl"/>
    </pipeline:pipeline>
  </xsl:template>

  <xsl:template name="cl">
    <pipeline:transformer name="components-2-html" xsl-path="../../cb-common/xsl/{$cb-version}/components-2-html/components-2-html.xsl" log="false"/>
    <pipeline:transformer name="compress-uris" xsl-path="compress-uris.xsl" log="false"/>
    <pipeline:transformer name="xss-encode" xsl-path="../../cb-common/xsl/common/xss-encode.xsl" log="false"/>
    <pipeline:transformer name="serializer-html5" xsl-path="serialize-html5.xsl" log="false"/>
  </xsl:template>

  <xsl:template name="xss-filter">
    <pipeline:xss-filter methods="ht"/>
  </xsl:template>

  <xsl:template name="search-query-params">
    <xsl:if test="not(/*/req:parameters/req:parameter[@name = 'text']/req:value/text())">
      <xsl:variable name="text-query-param" as="element(req:parameter)">
        <req:parameter name="text">
          <req:value>*:*</req:value>
        </req:parameter>
      </xsl:variable>
      <xsl:sequence select="req:set-attribute('text-query-param', $text-query-param)"/>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>

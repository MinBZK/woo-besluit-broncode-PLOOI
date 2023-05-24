<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:frbr="https://koop.overheid.nl/namespaces/frbr"
  xmlns:ext="http://zoekservice.overheid.nl/extensions"
  xmlns:file="http://expath.org/ns/file"
  xmlns:functx="http://www.functx.com"
  exclude-result-prefixes="#all"
  version="3.0">
    
  <xsl:include href="../../cb-common/xsl/common/frbr.xsl"/>
  <xsl:import href="../../cb-common/xsl/common/functx-1.0.xsl"/>
  
  <xsl:param name="config:development-mode" as="xs:boolean"/>
  <xsl:param name="config:plooi-repos-endpoint" as="xs:string"/>
  <xsl:param name="config:plooi-repos-path" as="xs:string?"/>
  <xsl:param name="config:plooi-repos-endpoint-internal" as="xs:string"/>
  
  <xsl:variable name="query-params" select="/*/req:parameters/req:parameter" as="element(req:parameter)*"/> 
  <xsl:variable name="path" select="/*/req:path" as="xs:string"/>
  <xsl:variable name="ds" select="file:dir-separator()" as="xs:string"/>
 
  <xsl:template match="/*">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:variable name="path" select="/req:request/req:path" as="xs:string"/>
      <xsl:variable name="regex" as="xs:string">([^/]+)/(\d+)/?(\.html)?/?$</xsl:variable>
      <xsl:variable name="analyze-result" select="analyze-string($path, $regex)" as="element(fn:analyze-string-result)"/>
      <xsl:variable name="work" select="$analyze-result/fn:match/fn:group[@nr='1']" as="xs:string"/>
      <xsl:variable name="expression" select="$analyze-result/fn:match/fn:group[@nr='2']" as="xs:string"/>
      <xsl:variable name="url" select="$config:plooi-repos-endpoint-internal || '/' || $work || '/_owms'" as="xs:string"/>

      <xsl:choose>
        <xsl:when test="$config:plooi-repos-path">
          <xsl:variable name="path" select="$config:plooi-repos-path || translate(ext:hashed-directory($work), '/', $ds) || $ds || $work || $ds || $expression  || $ds || 'xml' || $ds || $work || '_' || $expression || '.xml'" as="xs:string"/>
          <xsl:if test="$config:development-mode">
            <xsl:sequence select="log:log('INFO', 'doc-resolver-path: ' || $path)"/>
          </xsl:if> 
          <xsl:sequence select="frbr:get-document-filesystem($path)/*"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="$config:development-mode">
            <xsl:sequence select="log:log('INFO', 'doc-resolver-url: ' || $url)"/>
          </xsl:if> 
          <xsl:sequence select="frbr:get-document($url, (), (), ())/*"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:sequence select="*"/> <!-- Request XML -->
      <xml-url>
        <xsl:value-of select="$url"/>
      </xml-url>
      <expression>
        <xsl:value-of select="$expression"/>
      </expression>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>

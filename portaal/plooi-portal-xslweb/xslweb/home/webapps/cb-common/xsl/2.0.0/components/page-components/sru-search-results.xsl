<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"  
  xmlns:sru="http://docs.oasis-open.org/ns/search-ws/sruResponse"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response" 
  xmlns:http="http://expath.org/ns/http-client"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template name="cl:sru-search-results" as="element(cl:sru-search-results)">
    <xsl:param name="sru-endpoint" as="xs:string"/>
    <xsl:param name="query-parameters" as="element(req:parameter)*"/>
    <xsl:param name="rows-per-page" as="xs:integer"/>
    <xsl:param name="param-name-page" as="xs:string"/>
    <xsl:param name="index-def" as="element(index-def)"/>
    <xsl:variable name="current-page-param" select="$query-parameters[@name = $param-name-page]/req:value[1]" as="xs:string"/>
    <xsl:variable name="current-page" select="if ($current-page-param) then xs:integer($current-page-param) else 1" as="xs:integer"/>
    <cl:search-results>
      <!--
      <cl:modifier/>
      -->
      <xsl:apply-templates select="/sru:searchRetrieveResponse/sru:records/sru:record"/>  
    </cl:search-results>
  </xsl:template>
  
</xsl:stylesheet>
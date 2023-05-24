<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:webapp="http://www.armatiek.com/xslweb/functions/webapp"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:mode on-no-match="shallow-copy"/>

  <xsl:variable name="param-names" select="('thema', 'informatiesoort', 'organisatie')" as="xs:string+"/>

  <xsl:template match="req:parameters/req:parameter[@name = $param-names]/req:value/text()[not(starts-with(., 'http'))]">
    <xsl:value-of select="webapp:get-attribute(.)"/>
  </xsl:template>

</xsl:stylesheet>
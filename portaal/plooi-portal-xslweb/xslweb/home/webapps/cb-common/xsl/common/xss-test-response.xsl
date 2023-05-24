<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log" 
  xmlns:functx="http://www.functx.com" 
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:import href="functx-1.0.xsl"/>
  
  <xsl:mode on-no-match="shallow-copy"/>
  
  <xsl:variable name="xss-encoded-values" select="req:get-attribute('xss-encoded-values')" as="xs:string*"/>
  
  <xsl:template match="@*|text()">
    <xsl:variable name="text" select="." as="xs:string?"/>
    <xsl:for-each select="$xss-encoded-values">
      <xsl:if test="contains($text, .)">
        <xsl:sequence select="log:log('INFO', 'Possible unencoded value found: ' || .)"/>
      </xsl:if>  
    </xsl:for-each>
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>  
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
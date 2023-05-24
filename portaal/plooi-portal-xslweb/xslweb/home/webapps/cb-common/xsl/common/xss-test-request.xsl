<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:ext="http://zoekservice.overheid.nl/extensions"  
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:mode on-no-match="shallow-copy"/>
  
  <xsl:template match="req:parameters/req:parameter/req:value/text()|req:cookies/req:cookie/req:value/text()">
    <xsl:variable name="encoded-elem" select="ext:xss-encode-for-html-content(.)" as="xs:string?"/>    
    <xsl:variable name="encoded-attr" select="ext:xss-encode-for-html-attribute(.)" as="xs:string?"/>
    <xsl:if test="not(. = $encoded-elem) or not(. = $encoded-attr)">
      <xsl:sequence select="req:set-attribute('xss-encoded-values', (req:get-attribute('xss-encoded-values'), .))"/>
    </xsl:if>
    <xsl:value-of select="."/>
  </xsl:template>
  
</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:link">
    <a href="{cl:action}" class="{cl:class}">
      <xsl:value-of select="cl:text"/>
    </a>  
  </xsl:template>
  
</xsl:stylesheet>
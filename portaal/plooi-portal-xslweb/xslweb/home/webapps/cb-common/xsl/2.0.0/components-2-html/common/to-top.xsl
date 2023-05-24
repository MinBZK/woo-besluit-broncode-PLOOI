<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:to-top">
    <a href="#" role="button" class="to-top" data-decorator="to-top">
      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
        <xsl:attribute name="id" select="cl:id"/>
      </xsl:if>
      <xsl:value-of select="cl:text"/>
    </a>
  </xsl:template>
  
</xsl:stylesheet>
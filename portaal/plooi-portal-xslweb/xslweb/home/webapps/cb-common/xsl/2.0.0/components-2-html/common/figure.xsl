<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:figure">
    <figure class="figure">
      <xsl:apply-templates select="cl:picture"/>
      <xsl:apply-templates select="cl:fig-caption" mode="figure"/>
    </figure>
  </xsl:template>
  
  <xsl:template match="cl:fig-caption" mode="figure">
    <figcaption>
      <xsl:value-of select="cl:text"/>
    </figcaption>
    <xsl:if test="cl:source">
      <span class="figure__source">
        <xsl:value-of select="cl:text"/>
      </span>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
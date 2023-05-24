<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:picture">
    <picture>
      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
        <xsl:attribute name="id" select="cl:id"/>
      </xsl:if>
      <xsl:apply-templates select="cl:sources/cl:source" mode="picture"/>
      <img src="{cl:src}" alt="{cl:alt}"/>  
    </picture>
  </xsl:template>
  
  <xsl:template match="cl:sources/cl:source" mode="picture">
    <source srcset="{cl:srcset}{cl:media}" media="{cl:media}"/>
  </xsl:template>
  
</xsl:stylesheet>
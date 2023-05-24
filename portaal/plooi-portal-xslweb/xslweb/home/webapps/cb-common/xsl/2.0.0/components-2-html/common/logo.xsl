<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:logo">
    <div class="logo {cl:modifier}">
      <a href="{cl:href}">
        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
          <xsl:attribute name="id" select="cl:id"/>
        </xsl:if>
        <img src="{cl:file}" alt="{cl:alt}"/>
      </a>
      <xsl:if test="cl:text">
        <p class="logo__you-are-here"><span class="visually-hidden"><xsl:value-of select="if (cl:label-you-are-here) then cl:label-you-are-here else 'U bent nu hier: '"/></span>
          <xsl:for-each select="cl:text">
            <span>
              <xsl:value-of select="."/>
            </span>
          </xsl:for-each>
        </p>  
      </xsl:if>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
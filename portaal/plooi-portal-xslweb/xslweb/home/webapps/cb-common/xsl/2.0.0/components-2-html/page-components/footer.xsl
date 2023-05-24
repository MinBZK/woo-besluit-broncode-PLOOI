<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:footer">
    <div class="footer row--footer" role="contentinfo">
      <div class="container columns">
        <xsl:for-each select="cl:list"><!-- 1.25 naar 1.35: aangepast -->
          <div>
            <xsl:apply-templates select="."/>
          </div>
        </xsl:for-each>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
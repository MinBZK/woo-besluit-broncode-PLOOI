<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library" exclude-result-prefixes="#all"
  version="3.0">

  <xsl:template match="cl:video"><!-- 1.25 naar 1.35: geheel vervangen -->
    <div class="video">
      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
        <xsl:attribute name="id" select="cl:id"/>
      </xsl:if>
      <iframe
        title="{cl:title}"
        height="{cl:height}"
        src="{cl:video-embed-url}"
        >
        <xsl:if test="cl:allow-fullscreen">
          <xsl:attribute name="allowfullcreen">true</xsl:attribute>
        </xsl:if>
      </iframe>
      <a href="{cl:video-url}"><xsl:value-of select="cl:title"/> bekijken op Youtube</a>
    </div>
  </xsl:template>

</xsl:stylesheet>

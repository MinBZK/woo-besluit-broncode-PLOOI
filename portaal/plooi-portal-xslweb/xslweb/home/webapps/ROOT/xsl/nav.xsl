<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:mode on-no-match="shallow-copy"/>

  <xsl:mode name="nav" on-no-match="shallow-skip"/>

  <xsl:variable name="root" select="/" as="document-node()"/>

  <xsl:template match="cl:nav/cl:items">
    <xsl:copy>
      <xsl:apply-templates select="$root/*/*/html/body/div/div[@id = 'content']" mode="nav"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="section[contains(@class, 'js-scrollSection')]" mode="nav">
    <cl:item>
      <cl:title>
        <xsl:choose>
          <xsl:when test="@id = 'section-omschrijving'">Omschrijving</xsl:when>
          <xsl:when test="@id = 'section-tekst'">Samenvatting</xsl:when>
        </xsl:choose>
      </cl:title>
      <cl:link>
        <xsl:value-of select="'#' || @id"/>
      </cl:link>
      <!--
      <cl:active>true</cl:active>
      -->
      <xsl:variable name="sub-items" as="element(cl:item)*">
        <xsl:apply-templates select="descendant::section[contains(@class, 'js-scrollSection')]" mode="nav"/>
      </xsl:variable>
      <xsl:if test="$sub-items">
        <cl:sub>
          <cl:items>
            <xsl:sequence select="$sub-items"/>
          </cl:items>
        </cl:sub>
      </xsl:if>
    </cl:item>
  </xsl:template>

</xsl:stylesheet>
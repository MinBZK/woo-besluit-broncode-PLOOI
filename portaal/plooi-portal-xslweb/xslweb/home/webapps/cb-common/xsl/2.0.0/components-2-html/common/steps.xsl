<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:i18n="https://koop.overheid.nl/namespaces/i18n"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:utils="https://koop.overheid.nl/namespaces/utils"
  exclude-result-prefixes="#all"
  expand-text="yes"
  version="3.0">
  
  <xsl:template match="cl:steps">
    <xsl:variable name="current-step-text" as="node()" select="cl:current-step-text/node()"/>
    <ol class="steps">
      <xsl:for-each select="cl:step">
        <xsl:variable name="active" as="xs:boolean" select="string(@active) eq 'true'"/>
        
        <li class="steps__step {if ($active) then 'is-active' else ''}"><!-- 1.25 naar 1.35: aangepast -->
          <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
            <xsl:attribute name="id" select="cl:id"/>
          </xsl:if>
          <xsl:if test="$active"><span class="visually-hidden"><xsl:apply-templates select="$current-step-text"/>: </span></xsl:if>
          <xsl:apply-templates select="node()"/>
        </li>
      </xsl:for-each>
    </ol>
  </xsl:template>

</xsl:stylesheet>

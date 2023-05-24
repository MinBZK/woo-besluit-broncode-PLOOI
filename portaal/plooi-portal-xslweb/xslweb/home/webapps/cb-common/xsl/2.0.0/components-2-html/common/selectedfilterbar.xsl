<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:selectedfilterbar">
    <div class="selectedfilterbar {cl:modifier}">
      <xsl:variable name="elem-name" select="if (cl:heading-tag) then cl:heading-tag else 'p'" as="xs:string"/>
      <xsl:element name="{$elem-name}">
        <xsl:attribute name="class">selectedfilterbar__heading</xsl:attribute>
        <xsl:value-of select="cl:heading"/>
      </xsl:element>
      <div class="selectedfilterbar__filters">
        <xsl:apply-templates select="cl:selectedfilter"/>
      </div>
      <xsl:if test="cl:button-reset-action">
        <div class="selectedfilterbar__buttonreset">
          <p>
            <xsl:variable name="link" as="element(cl:link)">
              <cl:link>
                <cl:text>
                  <xsl:value-of select="if (cl:button-reset-text) then cl:button-reset-text else 'Wis alle filters'"/>
                </cl:text>
                <cl:action>
                  <xsl:value-of select="cl:button-reset-action"/>
                </cl:action>
                <cl:class>link link--remove</cl:class>
              </cl:link>  
            </xsl:variable>
            <xsl:apply-templates select="$link"/>
          </p>
        </div>  
      </xsl:if>
    </div>  
  </xsl:template>
  
</xsl:stylesheet>
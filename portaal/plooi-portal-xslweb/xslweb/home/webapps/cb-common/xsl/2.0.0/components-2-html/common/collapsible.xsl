<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:collapsible">
    <div id="{cl:id}" class="collapsible {cl:modifier}" data-decorator="init-collapsible">
      <div class="collapsible__header">
        <a href="#{cl:id}" aria-controls="collapsible-content-{cl:id}" aria-expanded="{cl:aria-expanded}" data-handler="toggle-collapsible"><xsl:apply-templates select="cl:heading/node()" mode="collapsible"/></a>
      </div>
      <div class="collapsible__content" id="collapsible-content-{cl:id}">
        <xsl:apply-templates select="cl:text/node()" mode="collapsible"/>
      </div>
    </div>
  </xsl:template>
  
  <!-- TODO Additional templates voor mode="collapsible"? -->
  <xsl:template match="@* | node()" mode="collapsible">
    <xsl:copy><xsl:apply-templates select="@* | node()" mode="collapsible"/></xsl:copy>
  </xsl:template>
</xsl:stylesheet>
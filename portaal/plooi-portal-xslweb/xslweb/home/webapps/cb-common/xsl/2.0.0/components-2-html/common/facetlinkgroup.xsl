<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:decimal-format name="EU" decimal-separator="," grouping-separator="."/>
  <xsl:variable name="decimal-format-pattern" as="xs:string">#.###</xsl:variable>
  
  <xsl:template match="cl:facetlinkgroup">
    <div class="facetgroup {cl:modifier}" data-decorator="init-facetgroup">
      <ul class="facetgroup__list">
        <xsl:apply-templates select="cl:item" mode="facetlinkgroup"/>
      </ul>
      <xsl:if test="count(cl:item) gt 5">
        <xsl:variable name="modal" as="element(cl:modal)?">
          <xsl:apply-templates select="cl:modal" mode="facetlinkgroup-modal">
            <xsl:with-param name="cl:modal-title" select="cl:modal/cl:modal-title" as="xs:string?"/>
          </xsl:apply-templates>  
        </xsl:variable>
        <xsl:apply-templates select="$modal"/>
      </xsl:if>
    </div>
  </xsl:template>
  
  <xsl:template match="cl:item" mode="facetlinkgroup">
    <li>
      <a href="{cl:href}" class="{if (cl:selected = 'true') then 'is-selected' else ()}">
        <xsl:value-of select="cl:label"/>
      </a> 
      <xsl:if test="cl:amount">
        <span class="amount"> (<xsl:value-of select="if (cl:amount castable as xs:integer) then format-number(cl:amount cast as xs:integer, $decimal-format-pattern, 'EU') else cl:amount"/>)</span>
      </xsl:if>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:modal" mode="facetlinkgroup-modal">
    <xsl:param name="cl:modal-title" as="xs:string"/>
    <xsl:copy>
      <xsl:sequence select="*"/>
      <cl:content>
        <h2>
          <cl:modal-title>
            <xsl:value-of select="cl:modal-title"/>  
          </cl:modal-title>
        </h2>
        <div class="js-facetgroup__listholder"/>
      </cl:content>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
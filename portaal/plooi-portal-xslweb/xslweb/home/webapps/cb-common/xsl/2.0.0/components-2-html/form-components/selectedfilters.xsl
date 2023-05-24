<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:selectedfilters">
    <div class="selectedfilters {cl:modifier}">
      <h3 class="selectedfilters__header">
        <xsl:value-of select="cl:title"/>
      </h3>
      <ul>
        <xsl:apply-templates select="cl:item" mode="selectedfilters"/>
      </ul>
      <xsl:if test="cl:link-edit-action">
        <xsl:variable name="link" as="element(cl:link)">
          <cl:link>
            <cl:text>
              <xsl:value-of select="if (cl:link-edit-text) then cl:link-edit-text else 'Wijzigen'"/>
            </cl:text>
            <cl:action>
              <xsl:value-of select="cl:link-edit-action"/>
            </cl:action>
            <cl:class>link link--edit</cl:class>
          </cl:link>
        </xsl:variable>
        <xsl:apply-templates select="$link"/>
      </xsl:if>
    </div>  
  </xsl:template>
  
  <xsl:template match="cl:item" mode="selectedfilters">
    <li>
      <span class="selectedfilters__filter">
        <xsl:value-of select="cl:label"/>
      </span>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
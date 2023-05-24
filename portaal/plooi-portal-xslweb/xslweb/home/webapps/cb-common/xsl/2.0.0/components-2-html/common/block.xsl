<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
    
  <xsl:template match="cl:block">
    <div class="block-content{if (cl:slim) then ' block-content--slim' else ()}{if (cl:modifier) then ' block-content--' || cl:modifier else ()}{if (cl:highlight) then ' block-content--highlight' else ()}{if (cl:flexed) then ' block-content--flexed' else ()}">
      <xsl:if test="exists(cl:subheading)">
        <p class="block-content__subheading">
          <i>
            <xsl:apply-templates select="cl:subheading/node()" mode="copy"/>
          </i>
        </p>
      </xsl:if>
      <xsl:element name="h{if (cl:htype) then cl:htype else '2'}">
        <xsl:attribute name="class">block-content__heading</xsl:attribute>
        <xsl:apply-templates select="cl:heading/node()" mode="copy"/>
      </xsl:element>
      <xsl:if test="exists(cl:content)">
        <xsl:apply-templates select="cl:content/node()" mode="copy"/>
      </xsl:if>
      <xsl:apply-templates select="cl:vrije-content"/><!-- 1.25 naar 1.35 toegevoegd -->
      <xsl:if test="exists(cl:buttonText)">
        <p>
          <a href="{cl:href}" class="button {if (exists(cl:buttonModifier)) then 'button--' || cl:buttonModifier else ''}">
            <xsl:apply-templates select="cl:buttonText/node()" mode="copy"/>
          </a>
          <!-- render button -->
        </p>
      </xsl:if>
      <xsl:if test="exists(cl:cta)">
        <a href="{cl:cta-href}" class="block-content__cta cta">
          <xsl:if test="exists(cl:cta-id)"><!-- upgrade 1.9.38 - 1.9.40 -->
            <xsl:attribute name="id" select="cl:cta-id"/>
          </xsl:if>
          <xsl:if test="cl:cta-external">
            <xsl:attribute name="rel" select="cl:cta-external"/>
          </xsl:if>
          <xsl:value-of select="cl:cta-label"/>
        </a>
      </xsl:if>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  expand-text="yes"
  version="3.0">
  
  <xsl:template match="cl:pageactions">
    <ul class="pageactions">
      <xsl:apply-templates select="cl:items/cl:item" mode="pageactions"/>
    </ul>
  </xsl:template>
  
  <xsl:template match="cl:items/cl:item" mode="pageactions">
    <li>
      <a href="{cl:link}">
        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
          <xsl:attribute name="id" select="cl:id"/>
        </xsl:if>
        <xsl:if test="cl:action-print">
          <xsl:attribute name="data-decorator">init-printtrigger</xsl:attribute>
        </xsl:if>
        <xsl:if test="cl:modal">
          <xsl:attribute name="data-decorator">init-modal</xsl:attribute>
          <xsl:attribute name="data-handler">open-modal</xsl:attribute>
         <xsl:attribute name="data-modal">modal-{if (cl:modal-id) then cl:modal-id else '1'}</xsl:attribute>
        </xsl:if>
        <xsl:if test="cl:icon">
          <img src="{cl:icon}" alt="" role="presentation"/>
        </xsl:if>
        <xsl:value-of select="cl:text"/>
        <xsl:if test="cl:count">
          <span class="browse__count">
            <xsl:value-of select="cl:count"/>
            <span class="visually-hidden">
              <xsl:value-of select="' ' || cl:count-context"/>
            </span>
          </span>
        </xsl:if>
      </a>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
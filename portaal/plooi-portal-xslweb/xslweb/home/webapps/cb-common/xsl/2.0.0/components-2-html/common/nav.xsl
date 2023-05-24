<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:nav">
    <ul class="nav-sub{if (cl:modifier) then ' nav-sub--' || cl:modifier else ()}">
      <xsl:if test="cl:aria-label">
        <xsl:attribute name="aria-label">
          <xsl:value-of select="cl:aria-label"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="cl:items/cl:item" mode="nav"/>
    </ul>
  </xsl:template>
  
  <xsl:template match="cl:nav/cl:items/cl:item" mode="nav">
    <li class="nav-sub__item{if (cl:active='true') then ' is-active' else ()}">
      <a href="{cl:link}" class="nav-sub__link{if (cl:active='true') then ' is-active' else ()}{if (cl:is-mother='true') then ' is-mother' else ()}"><!-- 1.9.35 -> 1.9.38 aangepast -->
        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
          <xsl:attribute name="id" select="cl:id"/>
        </xsl:if>
        <xsl:if test="cl:sactive"><!-- 1.9.35 -> 1.9.38 aangepast -->
          <xsl:attribute name="aria-current">page</xsl:attribute>
        </xsl:if>
        <xsl:apply-templates select="cl:title"/>
      </a>
      <xsl:apply-templates select="cl:sub" mode="nav"/>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:sub" mode="nav">
    <ul class="nav-sub__sub">
      <xsl:apply-templates select="cl:items/cl:item" mode="nav"/>
    </ul>  
  </xsl:template>
  
  <xsl:template match="cl:sub/cl:items/cl:item" mode="nav">
    <li class="nav-sub__sub-item">
      <a href="{cl:link}" class="nav-sub__sub-link{if (cl:active='true') then ' is-active' else ()}{if (cl:is-mother='true') then ' is-mother' else ()}"><!-- 1.9.35 -> 1.9.38 aangepast -->
        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
          <xsl:attribute name="id" select="cl:id"/>
        </xsl:if>
        <xsl:apply-templates select="cl:title"/>
      </a>
      <xsl:apply-templates select="cl:sub" mode="nav"/>
    </li> 
  </xsl:template>
  
</xsl:stylesheet>
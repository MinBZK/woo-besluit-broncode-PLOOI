<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
   
  <xsl:template match="cl:breadcrumb">
    <div class="row row--page-opener">
      <div class="container">
        <div class="breadcrumb">
          <xsl:apply-templates select="cl:title"/>
          <ol>
            <xsl:apply-templates select="cl:level"/>
          </ol>
        </div>    
      </div>
    </div>
  </xsl:template>
  
  <xsl:template match="cl:breadcrumb/cl:title">
    <p>
      <xsl:apply-templates/>
    </p>
  </xsl:template>
  
  <xsl:template match="cl:breadcrumb/cl:level">
    <li>
      <xsl:if test="@id">
        <xsl:attribute name="id" select="@id"/>
      </xsl:if>
      <xsl:choose>
        <xsl:when test="not(following-sibling::cl:level)">
          <xsl:value-of select="cl:label"/>  
        </xsl:when>
        <xsl:otherwise>
          <a href="{cl:path}">
            <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
              <xsl:attribute name="id" select="cl:id"/>
            </xsl:if>
            <xsl:value-of select="cl:label"/>
          </a>
        </xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
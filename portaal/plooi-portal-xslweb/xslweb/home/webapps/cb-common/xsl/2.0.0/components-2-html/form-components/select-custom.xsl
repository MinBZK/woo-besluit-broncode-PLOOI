<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
 
  <xsl:template match="cl:select-custom"><!-- 1.25 naar 1.35: aangepast -->
    <label for="{cl:id}" class="form__label form__label--{cl:label-modifier} {if (cl:label-visible) then '' else 'visually-hidden'}">
      <xsl:value-of select="cl:label"/>
    </label>
    <xsl:apply-templates select="cl:tooltip" mode="#current"/>
    <div class="select-custom {cl:modifier}">
      <select id="{if (cl:id) then cl:id else generate-id()}" name="{cl:name}">
        <xsl:choose>
          <xsl:when test="cl:options/cl:label"><!-- let op, o ptions met een label is een "optgroupLabel" --> 
            <optgroup label="{cl:options/cl:label}">
              <xsl:apply-templates select="cl:options/cl:option" mode="select-custom"/>
            </optgroup>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="cl:options/cl:option" mode="select-custom"/>
          </xsl:otherwise>
        </xsl:choose>
      </select>
    </div>
  </xsl:template>
 
  <xsl:template match="cl:options/cl:option" mode="select-custom">
    <option value="{cl:value}">
      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
        <xsl:attribute name="id" select="cl:id"/>
      </xsl:if>
      <xsl:if test="cl:linked-to">
        <xsl:attribute name="data-linkedto" select="cl:linked-to"/>
      </xsl:if>
      <xsl:if test="cl:selected = 'true'">
        <xsl:attribute name="selected">selected</xsl:attribute>
      </xsl:if>
      <xsl:value-of select="cl:label"/>
    </option>
  </xsl:template>
  
</xsl:stylesheet>
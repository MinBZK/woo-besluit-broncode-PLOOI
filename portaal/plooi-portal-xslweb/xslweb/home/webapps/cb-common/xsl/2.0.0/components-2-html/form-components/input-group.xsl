<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:input-group">
    <xsl:variable name="code">
      <xsl:if test="cl:label">
        <p class="form__label {if (cl:label-size) then 'form__label--' || cl:label-size else ()}"><!-- 1.25 naar 1.35: aangepast -->
          <xsl:value-of select="cl:label"/>
        </p>
      </xsl:if>
      <div class="input-group {if (cl:size) then 'input-group--' || cl:size else ()} {if (cl:flex = 'true') then 'input-group--flex' else ()}">
        <xsl:if test="cl:radio = 'true'">
          <xsl:apply-templates select="cl:options/cl:option" mode="input-group"/>
        </xsl:if>
      </div>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="cl:radio = 'true'">
        <div role="radiogroup">
          <xsl:sequence select="$code"/>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="$code"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="cl:options/cl:option" mode="input-group">
    <div class="input-group__item">
      <input class="input-group__input" type="radio" id="option-{cl:id}" name="{../../cl:name}" value="{cl:value}" data-linkedto="{cl:linked-to}">
        <xsl:if test="cl:checked = 'true'">
          <xsl:attribute name="checked">checked</xsl:attribute>
        </xsl:if>
        <xsl:if test="cl:disabled = 'true'">
          <xsl:attribute name="disabled">disabled</xsl:attribute>
        </xsl:if>
        <label class="input-group__label" for="option-{cl:id}">
          <xsl:value-of select="cl:text"/>
        </label>
      </input>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
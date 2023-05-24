<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:input-radio">
    <xsl:apply-templates select="cl:group" mode="input-radio"/>
    <xsl:apply-templates select="cl:options/cl:option" mode="input-radio"/>
  </xsl:template>
  
  <xsl:template match="cl:group" mode="input-radio">
    <div class="input-radio-group"/>
  </xsl:template>
  
  <xsl:template match="cl:options/cl:option" mode="input-radio">
    <xsl:variable name="id" select="if (cl:id) then cl:id else generate-id()" as="xs:string"/>
    <div class="input-radio {cl:modifier}"><!-- upgrade 1.9.40 - 1.9.44 -->
      <input name="{../../cl:name}" id="option-{$id}" value="{cl:value}" type="radio" class="radio__input">
        <xsl:if test="cl:checked = 'true'">
          <xsl:attribute name="checked">checked</xsl:attribute>
        </xsl:if>
        <xsl:if test="cl:disabled = 'true'">
          <xsl:attribute name="disabled">disabled</xsl:attribute>
        </xsl:if>
        <xsl:if test="cl:linkedto">
          <xsl:attribute name="data-linkedto" select="cl:linkedto"/>
        </xsl:if>
        <!-- 1.9.27 Data attributes added. -->
        <xsl:for-each select="cl:data">
          <xsl:attribute name="{cl:name}" select="cl:value"/>
        </xsl:for-each>
      </input>
      <label class="radio__label" for="option-{$id}">
        <xsl:value-of select="cl:text"/>
      </label>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
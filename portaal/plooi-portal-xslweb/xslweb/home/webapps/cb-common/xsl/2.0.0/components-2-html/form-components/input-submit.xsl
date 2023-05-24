<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:input-submit">
    <div class="input-inputsubmit {cl:modifier-component}">
      <label for="{cl:id}">
        <xsl:if test="not(cl:show-label = 'true')">
          <xsl:attribute name="class">visually-hidden</xsl:attribute>
        </xsl:if>
        <xsl:value-of select="cl:label"/>
      </label>
      <div class="input-inputsubmit__grid">
        <xsl:element name="{if (cl:input-type = 'texarea') then 'textarea' else 'input'}"><!-- 1.9.35 -> 1.9.37: aangepast -->
          <xsl:attribute name="type" select="if (cl:type) then cl:type else 'text'"/>
          <xsl:attribute name="name" select="cl:name"/>
          <xsl:attribute name="id" select="cl:id"/>
          <xsl:attribute name="value" select="cl:value"/>
          <xsl:attribute name="class" select="'input ' || cl:modifier"/>
          <xsl:attribute name="placeholder" select="cl:place-holder"/>
          <xsl:if test="cl:required">
            <xsl:attribute name="required">required</xsl:attribute>
          </xsl:if>
        </xsl:element>  
        <button class="button button--primary" type="submit">
          <span>
            <xsl:value-of select="cl:button"/><!-- 1.9.37 -> 1.9.38 aangepast -->
          </span>
        </button>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
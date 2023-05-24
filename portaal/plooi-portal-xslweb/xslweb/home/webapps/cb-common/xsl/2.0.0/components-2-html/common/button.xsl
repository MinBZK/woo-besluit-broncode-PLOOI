<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:button">
    <xsl:choose>
      <xsl:when test="cl:anchor = 'true'">
        <a class="button {cl:modifier}" href="{cl:action}" role="button">
          <xsl:if test="cl:id">
            <xsl:attribute name="id" select="cl:id"/>
          </xsl:if>
          <xsl:if test="cl:disabled = 'true'">
            <xsl:attribute name="disabled">disabled</xsl:attribute>
          </xsl:if>
          <xsl:value-of select="cl:text"/>
          <xsl:if test="cl:text2">
            <span class="button__sub">
              <xsl:value-of select="cl:text2"/>
            </span>
          </xsl:if>
        </a>
      </xsl:when>
      <xsl:when test="cl:button = 'true'">
        <button class="button {cl:modifier}" type="{if (cl:type) then cl:type else 'submit'}">
          <xsl:if test="cl:id">
            <xsl:attribute name="id" select="cl:id"/>
          </xsl:if>
          <xsl:if test="cl:disabled = 'true'">
            <xsl:attribute name="disabled">disabled</xsl:attribute>
          </xsl:if>
          <xsl:if test="cl:hidden = 'true'">
            <xsl:attribute name="hidden">hidden</xsl:attribute>
          </xsl:if>
          <xsl:value-of select="cl:text"/>
          <xsl:if test="cl:text2">
            <span class="button__sub">
              <xsl:value-of select="cl:text2"/>
            </span>
          </xsl:if>
        </button>
      </xsl:when>
      <xsl:when test="cl:button-with-divider">
        <button class="button {cl:modifier}" type="{if (cl:type) then cl:type else 'button'}">
          <xsl:if test="cl:id">
            <xsl:attribute name="id" select="cl:id"/>
          </xsl:if>
          <xsl:if test="cl:disabled = 'true'">
            <xsl:attribute name="disabled">disabled</xsl:attribute>
          </xsl:if>
          <span class="divider">
            <xsl:value-of select="cl:text"/>
            <xsl:if test="cl:text2">
              <span class="button__sub">
                <xsl:value-of select="cl:text2"/>
              </span>
            </xsl:if>
          </span>    
        </button>
      </xsl:when>
      <xsl:when test="cl:input-button">
        <input class="button {cl:modifier}" type="{if (cl:type) then cl:type else 'button'}" value="{cl:text}">
          <xsl:if test="cl:id">
            <xsl:attribute name="id" select="cl:id"/>
          </xsl:if>
          <xsl:if test="cl:disabled = 'true'">
            <xsl:attribute name="disabled">disabled</xsl:attribute>
          </xsl:if>
        </input>
      </xsl:when>
      <xsl:when test="cl:input-submit">
        <input class="button {cl:modifier}" type="{if (cl:type) then cl:type else 'submit'}" value="{cl:text}">
          <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
            <xsl:attribute name="id" select="cl:id"/>
          </xsl:if>
          <xsl:if test="cl:disabled = 'true'">
            <xsl:attribute name="disabled">disabled</xsl:attribute>
          </xsl:if>
        </input>
      </xsl:when>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:checkbox">
    <xsl:variable name="divs" as="element(div)*">
      <xsl:for-each select="cl:option">
        <xsl:variable name="id" select="'option-' || xs:string(if (cl:id) then cl:id else generate-id())" as="xs:string"/>
        <div class="input-checkbox">
          <input class="checkbox__input {cl:input-class}" type="checkbox" id="{$id}" name="{cl:name}" value="{cl:value}">
            <xsl:if test="cl:disabled = 'true'">
              <xsl:attribute name="disabled">disabled</xsl:attribute>
            </xsl:if>
            <xsl:if test="cl:checked = 'true'">
              <xsl:attribute name="checked">checked</xsl:attribute>
            </xsl:if>
            <!-- nb. {{{arguments}}} toegevoegd in handlebars in 1.9.37. Dit betekent dat hier mogelijk aanvullende attributen kunnen voorkomen. dit moet dan via cl:* constructs lopen. -->
          </input>
          <label class="checkbox__label" for="{$id}">
            <xsl:apply-templates select="cl:text/node()"/>
          </label> 
          <xsl:if test="cl:link-href"><!-- upgrade 1.9.38 - 1.9.40 -->
            <a href="{cl:link-href}">
              <xsl:value-of select="cl:link-label"/>
            </a>
          </xsl:if>
        </div>
      </xsl:for-each>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="cl:moreless = 'true'">
        <div data-decorator="showmoreless" data-config="{{&quot;amount&quot;:&quot;4&quot;}}">
          <ul class="list list--unstyled">
            <xsl:for-each select="$divs">
              <li>
                <xsl:sequence select="."/>
              </li>  
            </xsl:for-each>
          </ul>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="$divs"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
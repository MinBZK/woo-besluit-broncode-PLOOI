<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:input-text">
    <xsl:variable name="id" select="if (cl:clean-id) then cl:id else 'input-text-' || cl:id" as="xs:string"/>
    <xsl:variable name="output" as="element()+">
      <xsl:variable name="output-2" as="element()+">
        <label for="{$id}" class="form__label {if (cl:label-modifier) then ('form__label--' || cl:label-modifier) else ''}">
          <xsl:if test="not(cl:field-label-visible = 'true')">
            <xsl:attribute name="class">visually-hidden</xsl:attribute>
          </xsl:if>
          <xsl:value-of select="cl:field-label"/> 
          <xsl:if test="cl:required = 'true'">
            <span class="mandatory">Verplicht</span>
          </xsl:if>
        </label>
        <xsl:if test="cl:required = 'true'">
          <span class="mandatory">Verplicht</span>
        </xsl:if>
        <xsl:if test="cl:prefix = 'true'">
          <span class="input-text__prefix {cl:modifier2}">
            <xsl:value-of select="cl:prefix-label"/>
          </span>
        </xsl:if>
        <input 
          type="{if (cl:type) then cl:type else 'text'}"
          id="{$id}"
          name="{if (cl:name) then cl:name else cl:id}"
          class="input input-text {cl:modifier} {cl:class} {if (cl:error) then 'has-error' else ()}"
         
          aria-invalid="{if (cl:error) then 'true' else 'false'}">
          <xsl:if test="cl:field-place-holder"><!-- 1.25 naar 1.35: aangepast -->
            <xsl:attribute name="placeholder">
              <xsl:value-of select="cl:field-place-holder"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="cl:autofocus = 'true'">
            <xsl:attribute name="autofocus">autofocus</xsl:attribute>
          </xsl:if>
          <xsl:if test="cl:disabled = 'true'">
            <xsl:attribute name="disabled">disabled</xsl:attribute>
          </xsl:if>
          <xsl:if test="cl:value">
            <xsl:attribute name="value" select="cl:value"/>
          </xsl:if>
          <xsl:if test="cl:error">
            <xsl:attribute name="aria-describedby">input-text-<xsl:value-of select="cl:id"/>__error</xsl:attribute> 
          </xsl:if>
          <xsl:if test="cl:remove = 'true'">
            <xsl:attribute name="data-decorator">toggle-remove-visibility</xsl:attribute>
          </xsl:if>
          <xsl:if test="cl:required = 'true'">
            <xsl:attribute name="required">required</xsl:attribute>
          </xsl:if>
          <xsl:if test="cl:pattern-type">
            <xsl:attribute name="data-pattern-type" select="cl:pattern-type"/>
          </xsl:if>
          <xsl:if test="cl:title">
            <xsl:attribute name="title" select="cl:title"/>
          </xsl:if>
        </input>
        <xsl:if test="cl:error">
          <p role="alert" id="input-text-{cl:id}__error" class="form__error">
            <xsl:value-of select="cl:error"/>
          </p>  
        </xsl:if>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="cl:prefix = 'true'">
          <div class="input-text__container input-text__container--prefix">
            <xsl:sequence select="$output-2"/>  
          </div>
        </xsl:when>
        <xsl:otherwise>
          <xsl:sequence select="$output-2"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="cl:remove = 'true'">
        <div class="relative">
          <xsl:sequence select="$output"/>
          <a href="{cl:link}" class="input-text__remove invisible" data-handler="empty-input">
            <span class="visually-hidden">
              <xsl:value-of select="if (cl:label-clear) then cl:label-clear else 'Wis waarde'"/>
            </span>
          </a>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="$output"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
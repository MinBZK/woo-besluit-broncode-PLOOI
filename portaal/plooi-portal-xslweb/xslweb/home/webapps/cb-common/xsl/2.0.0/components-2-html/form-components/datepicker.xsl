<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:template match="cl:datepicker[cl:range]">
    <xsl:param name="narrow" as="xs:boolean?"/><!-- defaults to false() -->
    <xsl:choose>
      <xsl:when test="$narrow">
        <div class="columns">
          <div class="column">
            <div class="form__row">
              <xsl:for-each select="cl:range/cl:from">
                <xsl:call-template name="datepicker">
                  <xsl:with-param name="range-relation" select="'input--' || ../cl:to/cl:id"/>
                  <xsl:with-param name="range-edge" select="'from'"/>
                </xsl:call-template>
              </xsl:for-each>
            </div>
            <div class="form__row">
              <xsl:for-each select="cl:range/cl:to">
                <xsl:call-template name="datepicker">
                  <xsl:with-param name="range-relation" select="'input--' || ../cl:from/cl:id"/>
                  <xsl:with-param name="range-edge" select="'to'"/>
                </xsl:call-template>
              </xsl:for-each>
            </div>
          </div>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <div>
          <xsl:for-each select="cl:range/cl:from">
            <xsl:call-template name="datepicker">
              <xsl:with-param name="range-relation" select="'input--' || ../cl:to/cl:id"/>
              <xsl:with-param name="range-edge" select="'from'"/>
            </xsl:call-template>
          </xsl:for-each>
        </div>
        <div>
          <xsl:for-each select="cl:range/cl:to">
            <xsl:call-template name="datepicker">
              <xsl:with-param name="range-relation" select="'input--' || ../cl:from/cl:id"/>
              <xsl:with-param name="range-edge" select="'to'"/>
            </xsl:call-template>
          </xsl:for-each>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="cl:datepicker[not(cl:range)]">
    <xsl:call-template name="datepicker"/>
  </xsl:template>

  <xsl:template name="datepicker">
    <xsl:param name="range-relation" as="xs:string?"/>
    <xsl:param name="range-edge" as="xs:string?"/>
    <xsl:if test="cl:label">
      <label class="form__label{if (cl:label-modifier) then ' form__label--' || cl:label-modifier else ()}" for="input--{cl:id}">
        <xsl:value-of select="cl:label"/>
      </label>
    </xsl:if>
    <xsl:variable name="date-pattern" expand-text="no">
      ^ \d{1,2} - \d{1,2} - \d{4} $
    </xsl:variable>
    <xsl:variable name="value" select="if (matches(cl:value, $date-pattern, 'x')) then cl:value else ()"/>
    <div class="datepicker">
      <input
        id="input--{cl:id}"
        type="date"
        name="{cl:name}"
        value="{$value}"
        data-date="{$value}"
        class="datepicker__input {if (cl:input-class) then cl:input-class else ()}"
        date-format="{if (cl:config-date-format) then cl:config-date-format else 'dd-mm-yy'}"
        data-decorator="init-datepicker"
        data-placeholder="{cl:place-holder}"><!-- 1.25 naar 1.35: aangepast -->
        <xsl:if test="cl:required = 'true'">
          <xsl:attribute name="required">required</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="data-config">
          <xsl:choose>
            <xsl:when test="$range-relation">{ "dateFormat" : "dd-mm-yy", "range" : "true", "rangePosition": "<xsl:value-of select="$range-edge"/>", "rangeRelation":"#<xsl:value-of select="$range-relation"/>" }</xsl:when>
            <xsl:when test="cl:year-range">{ "dateFormat" : "dd-mm-yy", "yearrange": "<xsl:value-of select="cl:year-range"/>" }</xsl:when>
            <xsl:otherwise>{ "dateFormat" : "dd-mm-yy" }</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
      </input>
    </div>
    <xsl:if test="cl:select-today"><!-- 1.25 naar 1.35: toegevoegd -->
      <a href="#" data-handler="select-today" data-for="input--{cl:id}">
        <xsl:if test="cl:today-id"><!-- upgrade 1.9.38 - 1.9.40 -->
          <xsl:attribute name="id" select="cl:today-id"/>
        </xsl:if>
        <xsl:text>Vandaag</xsl:text>
      </a>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
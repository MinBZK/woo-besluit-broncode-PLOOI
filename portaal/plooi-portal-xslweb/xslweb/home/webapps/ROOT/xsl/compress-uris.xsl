<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:functx="http://www.functx.com"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:include href="../../cb-common/xsl/common/functx-1.0.xsl"/>

  <xsl:mode on-no-match="shallow-copy"/>

  <xsl:variable name="param-names" select="('thema', 'informatiesoort', 'organisatie')" as="xs:string+"/>

  <xsl:template match="select[@name = $param-names]//option/@value | input[@name = $param-names]/@value">
    <xsl:attribute name="value" select="functx:substring-after-last(., '/')"/>
  </xsl:template>

  <xsl:template match="a/@href[not(starts-with(., 'http')) and contains(., '?')]">
    <xsl:variable name="path" select="substring-before(., '?')" as="xs:string?"/>
    <xsl:variable name="query-string" select="substring-after(., '?')" as="xs:string?"/>
    <xsl:variable name="params" select="tokenize($query-string, '&amp;')" as="xs:string*"/>
    <xsl:variable name="tokens" as="xs:string*">
      <xsl:for-each select="$params">
        <xsl:variable name="name" select="substring-before(., '=')" as="xs:string?"/>
        <xsl:variable name="value" select="substring-after(., '=')" as="xs:string?"/>
        <xsl:choose>
          <xsl:when test="($name = $param-names) and starts-with($value, 'http')">
            <xsl:value-of select="$name || '=' || encode-for-uri(functx:substring-after-last($value, '%2F'))"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:variable>
    <xsl:attribute name="href" select="$path || '?' || string-join($tokens, '&amp;')"/>
  </xsl:template>

</xsl:stylesheet>
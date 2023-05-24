<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:utils="https://koop.overheid.nl/namespaces/utils"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:template name="get-query-clause" as="xs:string?">
    <xsl:param name="index" as="element(index)"/>
    <xsl:param name="from-values" as="xs:string*"/>
    <xsl:param name="to-values" as="xs:string*"/>
    <xsl:variable name="datum-range" select="$query-params[@name = 'datumrange']/req:value" as="xs:string?"/>
    <xsl:variable name="result" as="xs:string*">
      <xsl:choose>
        <xsl:when test="$index/form-field = 'text'">
          <xsl:variable name="query" select="normalize-space($from-values[1])" as="xs:string?"/>
          <xsl:variable name="scope" select="$query-params[@name = 'ftsscope']/req:value" as="xs:string?"/>
          <xsl:choose>
            <xsl:when test="$scope = 'title'">
              <xsl:value-of select="'(dcterms.title=/relevant &quot;' || $query || '&quot;)'"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="'(plooi.text=/relevant &quot;' || $query || '&quot;)'"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:when test="$index/form-field = 'datumrange'">
          <xsl:variable name="datum-range" select="normalize-space($from-values[1])" as="xs:string?"/>
          <xsl:choose>
            <xsl:when test="$datum-range = 'vandaag'">(dcterms.available.from == "<xsl:value-of select="substring(xs:string(current-date()), 1, 10)"/>")</xsl:when>
            <xsl:when test="$datum-range = 'afgelopen-week'">(dcterms.available.from >= "<xsl:value-of select="substring(xs:string(current-date() - xs:dayTimeDuration('P7D')), 1, 10)"/>")</xsl:when>
            <xsl:when test="$datum-range = 'afgelopen-maand'">(dcterms.available.from >= "<xsl:value-of select="substring(xs:string(current-date() - xs:yearMonthDuration('P1M')), 1, 10)"/>")</xsl:when>
            <xsl:when test="$datum-range = 'afgelopen-jaar'">(dcterms.available.from >= "<xsl:value-of select="substring(xs:string(current-date() - xs:yearMonthDuration('P1Y')), 1, 10)"/>")</xsl:when>
            <xsl:when test="$datum-range = 'custom' and $index/form-field = 'datumBeschikbaarVanaf'">
              <xsl:variable name="date-from" select="$query-params[@name = 'datumBeschikbaarVanaf']/req:value" as="xs:string?"/>
              <xsl:variable name="date-to" select="$query-params[@name = 'datumBeschikbaarTot']/req:value" as="xs:string?"/>
              <xsl:choose>
                <xsl:when test="normalize-space($date-from) and normalize-space($date-to)">(dcterms.available.from >= "<xsl:value-of select="utils:to-iso-date($date-from)"/>" AND (dcterms.available.from &lt;= "<xsl:value-of select="utils:to-iso-date($date-to)"/>")</xsl:when>
                <xsl:when test="normalize-space($date-from)">(dcterms.available.from >= "<xsl:value-of select="utils:to-iso-date($date-from)"/>")</xsl:when>
                <xsl:when test="normalize-space($date-to)">(dcterms.available.from &lt;= "<xsl:value-of select="utils:to-iso-date($date-to)"/>")</xsl:when>
              </xsl:choose>
            </xsl:when>
            <xsl:otherwise>cql.allRecords=1</xsl:otherwise>
          </xsl:choose>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:sequence select="string-join($result, '')"/>
  </xsl:template>

</xsl:stylesheet>
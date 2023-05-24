<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:err="http://www.w3.org/2005/xqt-errors"
  xmlns:search="https://koop.overheid.nl/namespaces/search"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:webapp="http://www.armatiek.com/xslweb/functions/webapp"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:http="http://expath.org/ns/http-client"
  xmlns:functx="http://www.functx.com"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:param name="config:repos-endpoint" as="xs:string"/>

  <xsl:mode name="capitalize-first" on-no-match="shallow-copy"/>
  <xsl:mode name="default-value" on-no-match="shallow-copy"/>
  <xsl:mode name="algemeen" on-no-match="shallow-copy"/>

  <xsl:function name="search:get-number-of-documents" as="xs:integer?">
    <xsl:variable name="cached-number" select="webapp:get-cache-value('number-of-documents', 'number-of-documents')" as="xs:integer?"/>
    <xsl:choose>
      <xsl:when test="$cached-number">
        <xsl:sequence select="$cached-number"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:try>
          <xsl:variable name="result-doc" as="document-node()">
            <xsl:call-template name="search:execute-search">
              <xsl:with-param name="endpoint" select="$config:sru-endpoint" as="xs:string"/>
              <xsl:with-param name="maximum-records" select="0" as="xs:integer"/>
              <xsl:with-param name="param-name-page" select="'page'" as="xs:string"/>
              <xsl:with-param name="index-def" as="element(index)*"/>
              <xsl:with-param name="x-connection" select="$config:sru-x-connection"/>
              <xsl:with-param name="sru-path" select="'/sru/Search'" as="xs:string"/>
              <xsl:with-param name="timeout" select="30" as="xs:integer"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:variable name="number" select="$result-doc/*/*:numberOfRecords" as="xs:integer"/>
          <xsl:sequence select="webapp:set-cache-value('number-of-documents', 'number-of-documents', $number, 3600, 3600)"/>
          <xsl:sequence select="$number"/>
          <xsl:catch>
            <xsl:sequence select="log:log('ERROR', $err:description || ' (' || $err:code || ' line: ' || $err:line-number || ', column: ' || $err:column-number || ')')"/>
            <xsl:sequence select="0"/>
          </xsl:catch>
        </xsl:try>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="search:get-valuelists" as="document-node()*">
    <xsl:param name="work-labels" as="xs:string*"/>
    <xsl:variable name="key" select="string-join($work-labels, '')" as="xs:string"/>
    <xsl:variable name="valuelists" select="webapp:get-cache-value('valuelists', $key)" as="document-node()*"/>
    <xsl:choose>
      <xsl:when test="$valuelists/*">
        <xsl:sequence select="$valuelists"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:try>
          <xsl:variable name="valuelists-geen-algemeen" as="document-node()*">
            <xsl:for-each select="for $l in $work-labels return search:get-latest-valuelist-path($l)">
              <xsl:sequence select="log:log('INFO', 'Loading valuelist &quot;' || . || '&quot;')"/>
              <xsl:variable name="url" select="$config:repos-endpoint || '/waardelijsten/' || ." as="xs:string"/>
              <xsl:sequence select="if (doc-available($url))
                                    then doc($url)
                                    else error(xs:QName('search:valuelists'), 'Error fetching valuelist ' || $url)"/>
            </xsl:for-each>
          </xsl:variable>
          <xsl:variable name="valuelists-with-default" as="document-node()*">
            <xsl:apply-templates select="$valuelists-geen-algemeen" mode="default-value">
              <xsl:with-param name="work-labels" select="$work-labels" as="xs:string*"/>
            </xsl:apply-templates>
          </xsl:variable>
          <xsl:variable name="valuelists-capitalize-first" as="document-node()*">
            <xsl:apply-templates select="$valuelists-with-default" mode="capitalize-first"/>
          </xsl:variable>
          <xsl:for-each select="$valuelists-capitalize-first/waardelijst/waarden//waarde">
            <xsl:sequence select="webapp:set-attribute(code, omschrijving)"/>
            <xsl:sequence select="webapp:set-attribute(functx:substring-after-last(code, '/'), code)"/>
          </xsl:for-each>
          <xsl:variable name="valuelists" as="document-node()*">
            <xsl:apply-templates select="$valuelists-capitalize-first" mode="algemeen"/>
          </xsl:variable>
          <xsl:sequence select="webapp:set-cache-value('valuelists', $key, $valuelists, 3600, 3600)"/>
          <xsl:sequence select="$valuelists"/>
          <xsl:catch>
            <xsl:sequence select="log:log('ERROR', $err:description || ' (' || $err:code || ' line: ' || $err:line-number || ', column: ' || $err:column-number || ')')"/>
          </xsl:catch>
        </xsl:try>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:template match="omschrijving/text()" mode="capitalize-first">
    <xsl:value-of select="functx:capitalize-first(.)"/>
  </xsl:template>

  <xsl:template match="waardelijst/waarden" mode="default-value">
    <xsl:param name="work-labels" as="xs:string*"/>
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
      <xsl:variable name="waardelijst-name" select="root()/*/@name" as="xs:string"/>
      <xsl:choose>
        <xsl:when test="'plooi_documentsoorten_portaal' = $work-labels">
          <waarde>
            <code>https://identifier.overheid.nl/tooi/id/documentsoort_onbekend</code>
            <omschrijving>publicatie</omschrijving>
          </waarde>
        </xsl:when>
        <xsl:when test="'toplijst_1.1' = $work-labels">
          <waarde>
            <code>https://identifier.overheid.nl/tooi/id/thema_onbekend</code>
            <omschrijving>onbekend thema</omschrijving>
          </waarde>
        </xsl:when>
        <xsl:when test="index-of($work-labels, 'col_compleet') = 1">
          <waarde>
            <code>https://identifier.overheid.nl/tooi/id/organisatie_onbekend</code>
            <omschrijving>onbekende organisatie</omschrijving>
          </waarde>
        </xsl:when>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="waarde|sectie" mode="default-value">
    <xsl:copy>
      <xsl:attribute name="sort" select="lower-case(omschrijving)"/>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="waarden/waarde/waarden" mode="algemeen">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="#current"/>
      <waarde sort="aaaa" type="algemeen">
        <code>
          <xsl:value-of select="../code"/>
        </code>
        <omschrijving>algemeen</omschrijving>
      </waarde>
      <xsl:apply-templates select="node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="waarde|sectie" mode="algemeen">
    <xsl:copy>
      <xsl:attribute name="sort" select="omschrijving"/>
      <xsl:apply-templates select="@*" mode="#current"/>
      <xsl:apply-templates select="node()" mode="algemeen"/>
    </xsl:copy>
  </xsl:template>

  <xsl:function name="search:get-latest-valuelist-path" as="xs:string">
    <xsl:param name="work-label" as="xs:string"/>
    <xsl:variable name="url" select="$config:repos-endpoint || '/waardelijsten/' || encode-for-uri($work-label) || '/manifest.xml'" as="xs:string"/>
    <xsl:variable name="manifest-doc" select="doc($url)"/>
    <xsl:variable name="highest-label" select="max(for $l in $manifest-doc/work/expression/@label[. castable as xs:integer] return $l cast as xs:integer)" as="xs:integer?"/>
    <xsl:value-of select="string-join($manifest-doc/work/expression[@label = $highest-label]/manifestation[@label = 'xml']/item/ancestor-or-self::*/@label, '/')"/>
  </xsl:function>

</xsl:stylesheet>
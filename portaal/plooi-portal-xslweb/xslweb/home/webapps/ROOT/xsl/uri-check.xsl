<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:i18n="https://koop.overheid.nl/namespaces/i18n"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:search="https://koop.overheid.nl/namespaces/search"
  xmlns:sru="http://docs.oasis-open.org/ns/search-ws/sruResponse"
  xmlns:facet="http://docs.oasis-open.org/ns/search-ws/facetedResults"
  xmlns:functx="http://www.functx.com"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:import href="page.xsl"/>

  <xsl:param name="config:error-code" as="xs:string"/>

  <xsl:template name="title">Platform open overheidsinformatie</xsl:template>

  <xsl:function name="cl:get-selected-nav-id" as="xs:string">
    <xsl:text>home</xsl:text>
  </xsl:function>

  <xsl:template name="breadcrumb" as="element(cl:breadcrumb)">
    <cl:breadcrumb>
      <cl:title>U bent hier:</cl:title>
      <cl:level>
        <cl:path>
          <xsl:value-of select="$context-path"/>
        </cl:path>
        <cl:label>Home</cl:label>
      </cl:level>
    </cl:breadcrumb>
  </xsl:template>

  <xsl:variable name="sru-result" as="document-node()">
    <xsl:call-template name="search:execute-search">
      <xsl:with-param name="endpoint" select="$config:sru-endpoint" as="xs:string"/>
      <xsl:with-param name="maximum-records" select="0" as="xs:integer"/>
      <xsl:with-param name="param-name-page" select="'page'" as="xs:string"/>
      <xsl:with-param name="index-def" as="element(index)*" select="$index-def"/>
      <xsl:with-param name="x-connection" select="$config:sru-x-connection"/>
      <xsl:with-param name="sru-path" select="'/sru/Search'" as="xs:string"/>
      <xsl:with-param name="timeout" select="30" as="xs:integer"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:template name="main">
    <div class="container row">
      <div class="columns columns--sidebar-left">
        <div class="column">
          <xsl:variable name="faceted-results" select="($sru-result/sru:searchRetrieveResponse/sru:extraResponseData/sru:facetedResults | $sru-result/sru:searchRetrieveResponse/sru:facetedResults)[1]" as="element(sru:facetedResults)?"/>
          <xsl:variable
            name="terms-informatiesoort"
            select="$faceted-results/facet:datasource/facet:facets/facet:facet[facet:index = 'plooi.informatiecategorie.identifier']/facet:terms/facet:term/facet:actualTerm"
            as="element(facet:actualTerm)*"/>
          <xsl:variable
            name="terms-thema"
            select="$faceted-results/facet:datasource/facet:facets/facet:facet[facet:index = 'plooi.topthema.identifier']/facet:terms/facet:term/facet:actualTerm"
            as="element(facet:actualTerm)*"/>
          <xsl:variable
            name="terms-organisatie"
            select="$faceted-results/facet:datasource/facet:facets/facet:facet[facet:index = 'plooi.verantwoordelijke.identifier']/facet:terms/facet:term/facet:actualTerm"
            as="element(facet:actualTerm)*"/>

          <h1>Uris wel aanwezig in content maar niet in betreffende waardelijst</h1>
          <h2>Documentsoort</h2>
          <ul>
            <xsl:for-each select="functx:value-except($terms-informatiesoort, search:get-valuelists($valuelist-label-type)/*//waarde/code)">
              <li>
                <xsl:value-of select="."/>
              </li>
            </xsl:for-each>
          </ul>
          <h2>Thema</h2>
          <ul>
            <xsl:for-each select="functx:value-except($terms-thema, search:get-valuelists($valuelist-label-thema)/*//waarde/code)">
              <li>
                <xsl:value-of select="."/>
              </li>
            </xsl:for-each>
          </ul>
          <h2>Verantwoordelijke</h2>
          <ul>
            <xsl:for-each select="functx:value-except($terms-organisatie, search:get-valuelists($valuelist-labels-organisation)/*//waarde/code)">
              <li>
                <xsl:value-of select="."/>
              </li>
            </xsl:for-each>
          </ul>
        </div>
      </div>
    </div>
  </xsl:template>

</xsl:stylesheet>

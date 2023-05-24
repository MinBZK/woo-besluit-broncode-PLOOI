<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:search="https://koop.overheid.nl/namespaces/search"
  xmlns:idx="https://koop.overheid.nl/namespaces/index"
  xmlns:facet="http://docs.oasis-open.org/ns/search-ws/facetedResults"
  xmlns:sru="http://docs.oasis-open.org/ns/search-ws/sruResponse"
  xmlns:functx="http://www.functx.com"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:import href="page.xsl"/>
  <xsl:include href="form-common.xsl"/>

  <!-- Required overrides: -->
  <xsl:template name="title">Platform open overheidsinformatie</xsl:template>

  <xsl:function name="cl:get-selected-nav-id" as="xs:string">
    <xsl:text>searchform</xsl:text>
  </xsl:function>

  <xsl:template name="breadcrumb" as="element(cl:breadcrumb)">
    <cl:breadcrumb>
      <cl:title>U bent hier:</cl:title>
      <cl:level>
        <cl:path>
          <xsl:value-of select="$context-path || '/'"/>
        </cl:path>
        <cl:label>Home</cl:label>
      </cl:level>
      <cl:level>
        <cl:label>Uitgebreid zoeken</cl:label>
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
    <div class="container row" role="main" id="content">
      <div class="row row--spacer">
        <cl:jumbotron>
          <cl:heading>Wegwijzer naar <xsl:value-of select="xs:string(local:format-number(search:get-number-of-documents()))"/> actief openbaar gemaakte documenten</cl:heading>
          <cl:sub-heading>Platform open overheidsinformatie</cl:sub-heading>
          <cl:hstyle>h2</cl:hstyle>
          <cl:size>medium</cl:size>
          <cl:centered>true</cl:centered>
          <cl:partial-block>
            <form class="form" id="searchform" action="{$context-path || '/zoekresultaten'}">
              <div class="form__row form__row--large">
                <fieldset>
                  <legend class="visually-hidden">Doorzoek overheidsdocumenten</legend>
                  <div class="form__row">
                    <!-- In ontwerp van CB staat hier geen aanroep van component input-text?? -->
                    <input type="text" name="{idx:get-form-field('text')}" class="input-text input-text--searchmedium" placeholder="Vul hier uw zoekwoord(en) in" autofocus="autofocus">
                      <xsl:if test="$query-params[@name = 'text']/req:value[normalize-space()]">
                        <xsl:attribute name="value" select="($query-params[@name = 'text']/req:value)[1]"/>
                      </xsl:if>
                    </input>
                  </div>

                  <div class="form__row">
                    <cl:checkbox>
                      <cl:moreless>false</cl:moreless>
                      <xsl:variable name="form-field" select="'ftsscope'" as="xs:string"/>
                      <cl:option>
                        <cl:name>
                          <xsl:value-of select="$form-field"/>
                        </cl:name>
                        <cl:value>title</cl:value>
                        <xsl:if test="$query-params[@name = $form-field]/req:value = 'title'">
                          <cl:checked>true</cl:checked>
                        </xsl:if>
                        <cl:text>Zoek alleen in titels</cl:text>
                      </cl:option>
                    </cl:checkbox>
                  </div>
                </fieldset>
              </div>
              <div class="columns columns--dominant">
                <div class="column">
                  <div class="form__row form__row--large">
                    <xsl:call-template name="fieldset-datum-beschikbaar"/>
                  </div>
                </div>
                <div class="column">
                  <xsl:variable name="faceted-results" select="($sru-result/sru:searchRetrieveResponse/sru:extraResponseData/sru:facetedResults | $sru-result/sru:searchRetrieveResponse/sru:facetedResults)[1]" as="element(sru:facetedResults)?"/>
                  <div class="form__row form__row--large">
                    <xsl:call-template name="fieldset-informatiesoort">
                      <xsl:with-param name="terms" select="$faceted-results/facet:datasource/facet:facets/facet:facet[facet:index = 'plooi.informatiecategorie.identifier']/facet:terms/facet:term" as="element(facet:term)*"/>
                      <xsl:with-param name="facet-mode" select="false()"/>
                    </xsl:call-template>
                  </div>
                  <div class="form__row form__row--large">
                    <xsl:call-template name="fieldset-thema">
                      <xsl:with-param name="terms" select="$faceted-results/facet:datasource/facet:facets/facet:facet[facet:index = 'plooi.topthema.identifier']/facet:terms/facet:term" as="element(facet:term)*"/>
                      <xsl:with-param name="facet-mode" select="false()" as="xs:boolean"/>
                    </xsl:call-template>
                  </div>
                  <div class="form__row form__row--large">
                    <xsl:call-template name="fieldset-organisatie">
                      <xsl:with-param name="terms" select="$faceted-results/facet:datasource/facet:facets/facet:facet[facet:index = 'plooi.verantwoordelijke.identifier']/facet:terms/facet:term" as="element(facet:term)*"/>
                      <xsl:with-param name="facet-mode" select="false()" as="xs:boolean"/>
                    </xsl:call-template>
                  </div>
                </div>
              </div>
              <div class="align-right">
                <ul class="list list--inline">
                  <li>
                    <a href="{$context-path || /*/req:path}" class="button icon-bg button--erase">Leegmaken</a>
                  </li>
                  <li>
                    <button type="submit" class="button button--primary">Zoeken</button>
                  </li>
                </ul>
              </div>
            </form>
          </cl:partial-block>
        </cl:jumbotron>
      </div>
    </div>
  </xsl:template>

</xsl:stylesheet>

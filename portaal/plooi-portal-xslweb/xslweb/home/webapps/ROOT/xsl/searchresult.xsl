<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:session="http://www.armatiek.com/xslweb/session"
  xmlns:webapp="http://www.armatiek.com/xslweb/functions/webapp"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:base64="http://www.armatiek.com/xslweb/functions/base64"
  xmlns:utils="https://koop.overheid.nl/namespaces/utils"
  xmlns:functx="http://www.functx.com"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:search="https://koop.overheid.nl/namespaces/search"
  xmlns:plooi="http://standaarden.overheid.nl/plooi/terms/"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:sru="http://docs.oasis-open.org/ns/search-ws/sruResponse"
  xmlns:gzd="http://standaarden.overheid.nl/sru"
  xmlns:diag="http://docs.oasis-open.org/ns/search-ws/diagnostic"
  xmlns:facet="http://docs.oasis-open.org/ns/search-ws/facetedResults"
  xmlns:idx="https://koop.overheid.nl/namespaces/index"
  xmlns:map="http://www.w3.org/2005/xpath-functions/map"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:mode name="facet" on-no-match="shallow-skip"/>

  <xsl:import href="page.xsl"/>

  <xsl:include href="form-common.xsl"/>
  <xsl:include href="../../cb-common/xsl/common/pagination.xsl"/>
  <xsl:include href="custom-query-clause.xsl"/>

  <xsl:variable name="root" select="." as="document-node()"/>
  <xsl:variable name="query-string" select="if (/*/req:query-string/text()) then '?' || /*/req:query-string else /*/req:query-string" as="xs:string?"/>

  <xsl:variable name="filter-fields" select="$index-def[form-field]"/>
  <xsl:variable name="filter-param-values" select="$query-params[@name = $filter-fields/form-field]/req:value"/>
  <xsl:variable name="page-param-value" select="$query-params[@name = 'page']/req:value"/>
  <xsl:variable name="reset-param-values" select="$query-params/req:value except ($filter-param-values, $page-param-value)"/>

  <xsl:key name="waarde" match="waarde" use="code"/>

  <!-- Required overrides: -->
  <xsl:template name="title">Platform open overheidsinformatie</xsl:template>

  <xsl:template match="scripts" as="element(script)*" mode="script">
    <xsl:next-match/>
    <script>var cookies =
      ['searchQueryString=<xsl:value-of select="if ($query-string) then base64:encode($query-string) else ''"/>'];</script>
  </xsl:template>

  <xsl:function name="cl:get-selected-nav-id" as="xs:string">
    <xsl:text>search</xsl:text>
  </xsl:function>

  <xsl:template match="cl:navLinks/cl:link[cl:id = 'searchform']/cl:url" mode="header">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:value-of select="utils:build-url($context-path || '/uitgebreid-zoeken', $filter-param-values)"/>
    </xsl:copy>
  </xsl:template>

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
        <cl:path>
          <xsl:value-of select="$context-path || '/uitgebreid-zoeken'"/>
        </cl:path>
        <cl:label>Zoeken</cl:label>
      </cl:level>
      <cl:level>
        <cl:label>Zoekresultaten</cl:label>
      </cl:level>
    </cl:breadcrumb>
  </xsl:template>

  <xsl:variable name="rows-per-page" select="xs:integer((/req:request/req:parameters/req:parameter[@name eq 'count']/req:value,10)[1])" as="xs:integer"/>

  <xsl:variable name="sru-result" as="document-node()">
    <xsl:call-template name="search:execute-search">
      <xsl:with-param name="endpoint" select="$config:sru-endpoint" as="xs:string"/>
      <xsl:with-param name="maximum-records" select="$rows-per-page" as="xs:integer"/>
      <xsl:with-param name="query-parameters" select="($query-params, req:get-attribute('text-query-param'))" as="element(req:parameter)*"/>
      <xsl:with-param name="param-name-page" select="'page'" as="xs:string"/>
      <xsl:with-param name="param-name-sort" select="'sort'" as="xs:string"/>
      <xsl:with-param name="index-def" select="$index-def" as="element(index)*"/>
      <xsl:with-param name="sort-def" select="$sort-def" as="element(sort)*"/>
      <xsl:with-param name="x-connection" select="$config:sru-x-connection"/>
      <xsl:with-param name="sru-path" select="'/sru/Search'" as="xs:string"/>
      <xsl:with-param name="timeout" select="30" as="xs:integer"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="number-of-hits" select="search:get-number-of-hits($sru-result)" as="xs:integer"/>
  <xsl:variable name="current-page" select="cl:get-current-page($query-params, 'page')" as="xs:integer"/>

  <xsl:template name="status" as="element(status)">
    <xsl:choose>
      <xsl:when test="$sru-result/error/@status = '500'">
        <status code="500" message="Error communicating with backend server">
          <xsl:sequence select="log:log('ERROR', xs:string($sru-result/error))"/>
        </status>
      </xsl:when>
      <xsl:when test="$sru-result/error/@status = '600'">
        <status code="500" message="Timeout communicating with backend server">
          <xsl:sequence select="log:log('ERROR', xs:string($sru-result/error))"/>
        </status>
      </xsl:when>
      <xsl:when test="$sru-result/*/*:diagnostics">
        <status code="500" message="Error executing query">
          <xsl:sequence select="log:log('ERROR', $sru-result/*/*:diagnostics)"/>
        </status>
      </xsl:when>
      <xsl:otherwise>
        <status code="200" message=""/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="main">
    <xsl:choose>
      <xsl:when test="$sru-result/error/@status = '500'">
        <div class="container row">
          <div class="columns columns--sidebar-left">
            <div class="column">
              <h2>Er is een probleem opgetreden bij de communicatie met de documenten server</h2>
            </div>
          </div>
        </div>
      </xsl:when>
      <xsl:when test="$sru-result/error/@status = '600'">
        <div class="container row">
          <div class="columns columns--sidebar-left">
            <div class="column">
              <h2>Er is timeout opgetreden bij de communicatie met de documenten server</h2>
            </div>
          </div>
        </div>
      </xsl:when>
      <xsl:when test="$sru-result/*/*:diagnostics">
        <div class="container row">
          <div class="columns columns--sidebar-left">
            <div class="column">
              <h2>Er is een fout opgetreden bij het uitvoeren van de zoekopdracht</h2>
            </div>
          </div>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="sort" as="element(cl:sort)">
          <xsl:variable name="sort" select="$query-params[@name='sort']/req:value[1]"/>
          <xsl:variable name="href-no-sort" select="utils:build-url($context-path || /*/req:path, $query-params/req:value except ($sort, $page-param-value), ?)"/>
          <xsl:variable name="href-no-count" select="utils:build-url($context-path || /*/req:path, $query-params/req:value except ($query-params[@name eq 'count']/req:value, $page-param-value))"/>
          <cl:sort>
            <cl:label>Sorteer op:</cl:label>
            <cl:columns>true</cl:columns>
            <cl:options>
              <xsl:choose>
                <xsl:when test="not($sort) or ($sort = 'relevance-desc')">
                  <cl:option>
                    <cl:label>Relevantie</cl:label>
                    <cl:link>
                      <xsl:value-of select="$href-no-sort(map { 'sort' : 'relevance-desc' })"/>
                    </cl:link>
                    <cl:active>is-active</cl:active>
                    <cl:class/>
                  </cl:option>
                  <cl:option>
                    <cl:label>Datum</cl:label>
                    <cl:link>
                      <xsl:value-of select="$href-no-sort(map { 'sort' : 'date-desc' })"/>
                    </cl:link>
                    <cl:class>sort--descending</cl:class>
                  </cl:option>
                </xsl:when>
                <xsl:when test="($sort = 'date-desc')">
                  <cl:option>
                    <cl:label>Relevantie</cl:label>
                    <cl:link>
                      <xsl:value-of select="$href-no-sort(map { 'sort' : 'relevance-desc' })"/>
                    </cl:link>
                    <cl:class/>
                  </cl:option>
                  <cl:option>
                    <cl:label>Datum</cl:label>
                    <cl:link>
                      <xsl:value-of select="$href-no-sort(map { 'sort' : 'date-asc' })"/>
                    </cl:link>
                    <cl:active>is-active</cl:active>
                    <cl:class>sort--descending</cl:class>
                  </cl:option>
                </xsl:when>
                <xsl:when test="$sort = 'date-asc'">
                  <cl:option>
                    <cl:label>Relevantie</cl:label>
                    <cl:link>
                      <xsl:value-of select="$href-no-sort(map { 'sort' : 'relevance-desc' })"/>
                    </cl:link>
                    <cl:class/>
                  </cl:option>
                  <cl:option>
                    <cl:label>Datum</cl:label>
                    <cl:link>
                      <xsl:value-of select="$href-no-sort(map { 'sort' : 'date-desc' })"/>
                    </cl:link>
                    <cl:active>is-active</cl:active>
                    <cl:class>sort--ascending</cl:class>
                  </cl:option>
                </xsl:when>
              </xsl:choose>
            </cl:options>
            <cl:amount-per-page-filter>
              <cl:label>Aantal: </cl:label>
              <cl:base-url>
                <xsl:value-of select="$href-no-count"/>
              </cl:base-url>
              <cl:amount>
                <xsl:value-of select="$rows-per-page"/>
              </cl:amount>
            </cl:amount-per-page-filter>
          </cl:sort>
        </xsl:variable>

        <div class="container columns columns--sidebar">
          <div class="columns--sidebar__sidebar" role="complementary" data-decorator="add-mobile-foldability" id="toggleable-1">
            <div data-decorator="init-fixedbottom-button">
              <div class="sidebar__section">

                <h2 class="sidebar__header">Zoek documenten</h2>

                <h3 class="visually-hidden">Zoek opnieuw</h3>

                <form class="form" id="searchresult-searchform2" action="{$context-path || $root/*/req:path}" data-decorator="init-form-validation">

                  <!-- Kopieer de zoek criteria die in dit form geen representatie hebben naar hidden form fields: -->
                  <!-- Controleer op parameter name om XSS te voorkomen -->
                  <xsl:for-each select="$query-params[matches(@name, '^[a-z-]+$', 'i') and not(@name = ('text', 'page'))]">
                    <xsl:for-each select="req:value">
                      <input type="hidden" name="{../@name}" value="{.}"/>
                    </xsl:for-each>
                  </xsl:for-each>

                  <fieldset>
                    <legend class="visually-hidden">Zoek bekendmakingen</legend>
                    <div class="form__row">
                      <xsl:variable name="form-field" select="idx:get-form-field('text')" as="xs:string"/>
                      <cl:input-text>
                        <cl:id>title</cl:id>
                        <cl:field-label>Zoeken</cl:field-label>
                        <cl:field-label-visible>false</cl:field-label-visible>
                        <cl:label-modifier>accent</cl:label-modifier>
                        <cl:name>
                          <xsl:value-of select="$form-field"/>
                        </cl:name>
                        <cl:value>
                          <xsl:value-of select="$query-params[@name = $form-field]/req:value"/>
                        </cl:value>
                        <cl:field-place-holder>Verfijn uw zoekopdracht</cl:field-place-holder>
                        <cl:autofocus>true</cl:autofocus>
                      </cl:input-text>
                    </div>
                    <!--
                         <div class="form__row">
                         {{ render "@select-custom - - with-label" selectContext merge=true }}
                         </div>
                    -->
                    <div class="form__rowsubmit">
                      <div class="form__rowsubmit__item">
                        <cl:button>
                          <cl:text>Zoek opnieuw</cl:text>
                          <cl:type>submit</cl:type>
                          <cl:modifier>button--primary button--block form__button</cl:modifier>
                          <cl:button>true</cl:button>
                        </cl:button>
                      </div>
                    </div>
                  </fieldset>

                </form>

              </div>

              <xsl:variable name="faceted-results" select="($sru-result/sru:searchRetrieveResponse/sru:extraResponseData/sru:facetedResults | $sru-result/sru:searchRetrieveResponse/sru:facetedResults)[1]" as="element(sru:facetedResults)?"/>
              <xsl:variable name="facet" select="$faceted-results/facet:datasource/facet:facets/facet:facet" as="element(facet:facet)*"/>
              <xsl:variable
                name="terms-informatiesoort"
                select="$facet[facet:index = 'plooi.informatiecategorie.identifier']/facet:terms/facet:term"
                as="element(facet:term)*"/>
              <xsl:variable
                name="terms-thema"
                select="$facet[facet:index = 'plooi.topthema.identifier']/facet:terms/facet:term"
                as="element(facet:term)*"/>
              <xsl:variable
                name="terms-organisatie"
                select="$facet[facet:index = 'plooi.verantwoordelijke.identifier']/facet:terms/facet:term"
                as="element(facet:term)*"/>

              <xsl:variable name="values-documentsoort" select="search:get-valuelists($valuelist-label-type)/*/waarden//waarde" as="element(waarde)*"/>
              <xsl:variable name="values-thema" select="search:get-valuelists($valuelist-label-thema)/*/waarden//waarde" as="element(waarde)*"/>
              <xsl:variable name="values-organisatie" select="search:get-valuelists($valuelist-labels-organisation)/*/waarden//waarde" as="element(waarde)*"/>

              <div class="sidebar__section">
                <h2 class="sidebar__header">Filter resultaten</h2>
                <form>
                  <fieldset>
                    <legend class="visually-hidden">Filter resultaten</legend>
                    <xsl:call-template name="facet-link-group">
                      <xsl:with-param name="legend">Documentsoort</xsl:with-param>
                      <xsl:with-param name="terms" select="$terms-informatiesoort" as="element(facet:term)*"/>
                      <xsl:with-param name="values" select="$values-documentsoort" as="element(waarde)*"/>
                      <xsl:with-param name="param-name" as="xs:string">informatiesoort</xsl:with-param>
                    </xsl:call-template>

                    <xsl:call-template name="facet-link-group">
                      <xsl:with-param name="legend">Thema</xsl:with-param>
                      <xsl:with-param name="terms" select="$terms-thema" as="element(facet:term)*"/>
                      <xsl:with-param name="values" select="$values-thema" as="element(waarde)*"/>
                      <xsl:with-param name="param-name" as="xs:string">thema</xsl:with-param>
                    </xsl:call-template>

                    <xsl:call-template name="facet-link-group">
                      <xsl:with-param name="legend">Organisatie</xsl:with-param>
                      <xsl:with-param name="terms" select="$terms-organisatie" as="element(facet:term)*"/>
                      <xsl:with-param name="values" select="$values-organisatie" as="element(waarde)*"/>
                      <xsl:with-param name="param-name" as="xs:string">organisatie</xsl:with-param>
                    </xsl:call-template>

                    <xsl:variable name="datumrange" select="($query-params[@name = 'datumrange']/req:value)[1]"/>
                    <xsl:variable name="periodes-in-range" select="$periodes
                      [empty($datumrange) or index-of($periodes/id, id) le index-of($periodes/id, $datumrange)]/id"/>
                    <xsl:variable name="terms" select="$facet[facet:index = 'dcterms.available.from']/facet:terms/facet:term[number(facet:count) gt 0 and facet:actualTerm = $periodes-in-range]" as="element(facet:term)*"/>
                    <xsl:if test="$terms">
                      <div class="form__row form__row--large">
                        <fieldset>
                          <legend>Datum beschikbaar</legend>
                          <cl:facetlinkgroup>
                            <xsl:for-each select="$terms">
                              <xsl:sort select="index-of($periodes/id, facet:actualTerm)"/>

                              <xsl:variable name="term" select="."/>
                              <xsl:variable name="label" select="$periodes[id eq $term/facet:actualTerm]/label"/>
                              <xsl:variable name="query-param-selector" select="$query-params[@name = 'datumrange']/req:value[. eq $term/facet:actualTerm]"/>
                              <cl:item>
                                <cl:href>
                                  <xsl:variable name="reset-param-values" select="$query-params/req:value except ($query-param-selector, $page-param-value)"/>
                                  <xsl:value-of select="if ($query-param-selector)
                                      then utils:build-url($context-path || $root/*/req:path, $reset-param-values)
                                    else utils:build-url($context-path || $root/*/req:path, ($query-params/req:value except ($page-param-value, $query-params[@name = 'datumrange']/req:value)), map { 'datumrange' : string($term/facet:actualTerm) })"/>
                                </cl:href>
                                <cl:label>
                                  <xsl:value-of select="$label"/>
                                </cl:label>
                                <cl:amount>
                                  <xsl:value-of select="$term/facet:count"/>
                                </cl:amount>
                                <xsl:if test="$query-param-selector">
                                  <cl:selected>true</cl:selected>
                                </xsl:if>
                              </cl:item>
                            </xsl:for-each>
                          </cl:facetlinkgroup>
                        </fieldset>
                      </div>
                    </xsl:if>
                  </fieldset>
                </form>
              </div>
            </div>
          </div>

          <div role="main" id="content">

            <xsl:variable name="hit-from" select="($current-page - 1) * $rows-per-page + 1" as="xs:integer"/>
            <xsl:variable name="hit-to" select="min((($current-page - 1) * $rows-per-page + $rows-per-page, $number-of-hits))" as="xs:integer"/>

            <h1>
              Zoekresultaten
              <xsl:text>&#160;</xsl:text>
              <span class="h1__sub">
                <xsl:choose>
                  <xsl:when test="xs:integer($number-of-hits) = 0">Geen resultaten gevonden</xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$hit-from"/>-<xsl:value-of select="$hit-to"/> van de <xsl:value-of select="xs:string(local:format-number($number-of-hits))"/> resultaten
                  </xsl:otherwise>
                </xsl:choose>
              </span>
            </h1>

            <xsl:variable name="selected-filter" as="element(cl:selectedfilter)">
              <cl:selectedfilter>
                <xsl:for-each select="$index-def[form-field] except $index-def[form-field eq 'text']">
                  <xsl:variable name="index-def" select="." as="element(index)"/>
                  <xsl:variable name="form-field" select="form-field" as="xs:string"/>
                  <xsl:for-each select="functx:distinct-deep($query-params[@name = $form-field]/req:value)">
                    <cl:item>
                      <cl:label>
                        <xsl:choose>
                          <xsl:when test="$index-def/is-uri = 'true'">
                            <xsl:value-of select="$index-def/label-id || ': ' || webapp:get-attribute(.)"/>
                          </xsl:when>
                          <xsl:when test="$index-def/id = 'datumrange'">
                            <xsl:value-of select="$index-def/label-id || ': ' || replace(., '-', ' ')"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:value-of select="$index-def/label-id || ': ' || ."/>
                          </xsl:otherwise>
                        </xsl:choose>
                      </cl:label>
                      <cl:link>
                        <xsl:variable name="reset-param-values" select="$query-params/req:value except (., $page-param-value)"/>
                        <xsl:value-of select="utils:build-url($context-path || /*/req:path, $reset-param-values)"/>
                      </cl:link>
                      <cl:label-remove>verwijder</cl:label-remove>
                    </cl:item>
                  </xsl:for-each>
                </xsl:for-each>
              </cl:selectedfilter>
            </xsl:variable>

            <xsl:if test="$selected-filter/cl:item">
              <cl:selectedfilterbar>
                <cl:heading>Door u gekozen filters</cl:heading>
                <xsl:sequence select="$selected-filter"/>
                <cl:button-reset-action>
                  <xsl:value-of select="utils:build-url($context-path || /*/req:path, $reset-param-values)"/>
                </cl:button-reset-action>
              </cl:selectedfilterbar>
            </xsl:if>

            <xsl:sequence select="$sort"/>

            <cl:result-list>
              <cl:modifier>result--list--wide</cl:modifier>
              <cl:context>
                <cl:items>
                  <xsl:apply-templates select="$sru-result/sru:searchRetrieveResponse/sru:records/sru:record"/>
                </cl:items>
              </cl:context>
            </cl:result-list>

            <xsl:call-template name="cl:pagination">
              <xsl:with-param name="query-parameters" select="$query-params" as="element(req:parameter)*"/>
              <xsl:with-param name="base-path" select="$context-path || /*/req:path" as="xs:string"/>
              <xsl:with-param name="param-name-page">page</xsl:with-param>
              <xsl:with-param name="number-of-hits" select="$number-of-hits" as="xs:integer"/>
              <xsl:with-param name="rows-per-page" select="$rows-per-page" as="xs:integer"/>
            </xsl:call-template>

          </div>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="sru:record">
    <xsl:variable name="position" select="position()" as="xs:integer"/>
    <xsl:variable name="is-part" select="exists(sru:recordData/gzd:gzd/gzd:originalData/plooi:meta/plooi:owmsmantel/dcterms:isPartOf)" as="xs:boolean"/>
    <xsl:variable name="is-bundle" select="exists(sru:recordData/gzd:gzd/gzd:originalData/plooi:meta/plooi:owmsmantel/dcterms:hasPart)" as="xs:boolean"/>
    <xsl:variable name="is-single" select="not($is-part) and not($is-bundle)" as="xs:boolean"/>
    <cl:item>
      <xsl:for-each select="sru:recordData/gzd:gzd">
        <cl:title-header>h2</cl:title-header>
        <xsl:if test="substring(xs:string(current-date()), 1, 10) = substring(gzd:originalData/plooi:meta/plooi:owmsmantel/dcterms:available/start, 1, 10)">
          <cl:label>Nieuw vandaag</cl:label>
        </xsl:if>
        <cl:title>
          <xsl:value-of select="gzd:originalData/plooi:meta/plooi:owmskern/dcterms:title"/>
        </cl:title>
        <cl:title-href>
          <xsl:variable name="hit-num" select="(($current-page - 1) * $rows-per-page) + $position" as="xs:integer"/>
          <xsl:variable name="work-label" select="functx:substring-before-last(gzd:originalData/plooi:meta/plooi:owmskern/dcterms:identifier, '_')" as="xs:string?"/>
          <xsl:variable name="expression-label" select="gzd:originalData/plooi:meta/plooi:plooiipm/plooi:versie" as="xs:string?"/>
          <xsl:value-of select="$context-path || '/Details/' || $work-label || '/' || $expression-label || '?hit=' || string-join((encode-for-uri(xs:string($hit-num)), $root/*/req:query-string[normalize-space()]), '&amp;')"/>
        </cl:title-href>
        <xsl:where-populated>
          <cl:text>
            <xsl:value-of select="gzd:originalData/plooi:meta/plooi:owmsmantel/dcterms:description"/>
          </cl:text>
        </xsl:where-populated>
        <cl:metadatas>
          <cl:metadata>
            <cl:value>
              <xsl:variable name="code-types" select="gzd:originalData/plooi:meta/plooi:plooiipm/plooi:informatiecategorie/@resourceIdentifier" as="xs:string*"/>
              <xsl:choose>
                <xsl:when test="$config:debug and empty($code-types)">Het gegeven "gzd:originalData/plooi:meta/plooi:plooiipm/plooi:informatiecategorie/@resourceIdentifier" is niet beschikbaar</xsl:when>
                <xsl:when test="empty($code-types)"/>
                <xsl:otherwise>
                  <xsl:variable name="values" as="xs:string*">
                    <xsl:for-each select="$code-types">
                      <xsl:variable name="code-type" select="." as="xs:string"/>
                      <xsl:variable name="type" select="functx:capitalize-first((search:get-valuelists($valuelist-label-type)/waardelijst//waarde[not(@type = 'algemeen') and code = $code-type]/omschrijving)[1])" as="xs:string?"/>
                      <xsl:value-of select="if ($type) then $type else 'Onbekend'"/>
                    </xsl:for-each>
                  </xsl:variable>
                  <xsl:value-of select="string-join($values, ', ') || xs:string(if ($is-part) then ' (onderdeel)' else ())"/>
                </xsl:otherwise>
              </xsl:choose>
            </cl:value>
          </cl:metadata>
          <cl:metadata>
            <cl:value>
              <xsl:variable name="codes-eerstverantwoordelijke" select="gzd:originalData/plooi:meta/plooi:plooiipm/plooi:verantwoordelijke/@resourceIdentifier" as="xs:string*"/>
              <xsl:variable name="eerstverantwoordelijken" as="xs:string*">
                <xsl:for-each select="$codes-eerstverantwoordelijke">
                  <xsl:variable name="code" select="." as="xs:string"/>
                  <xsl:variable name="eerstverantwoordelijke" select="functx:capitalize-first((search:get-valuelists($valuelist-labels-organisation)/waardelijst//waarde[not(@type = 'algemeen') and code = $code]/omschrijving)[1])" as="xs:string?"/>
                  <xsl:value-of select="if ($eerstverantwoordelijke) then $eerstverantwoordelijke else 'Onbekend'"/>
                </xsl:for-each>
              </xsl:variable>
              <xsl:value-of select="string-join($eerstverantwoordelijken, '; ')"/>
            </cl:value>
          </cl:metadata>
          <cl:metadata>
            <cl:value>
              <xsl:value-of select="'Beschikbaar sinds: ' || utils:format-date(substring(gzd:originalData/plooi:meta/plooi:owmsmantel/dcterms:available/start, 1, 10))"/>
            </cl:value>
          </cl:metadata>
          <!--
               <xsl:if test="gzd:originalData/plooi:meta/plooi:othermeta/plooi:bronmeta/plooi:subject">
               <cl:metadata>
               <cl:value>
               <xsl:value-of select="'Onderwerp: ' || string-join(gzd:originalData/plooi:meta/plooi:othermeta/plooi:bronmeta/plooi:subject, '; ')"/>
               </cl:value>
               </cl:metadata>
               </xsl:if>
          -->
        </cl:metadatas>
        <!--
             <cl:buttons>
             <cl:button>
             <xsl:variable name="hit-num" select="(($current-page - 1) * $rows-per-page) + position()" as="xs:integer"/>
             <cl:label>Bekijk details</cl:label>
             <cl:link>
             <xsl:variable name="work-label" select="functx:substring-before-last(gzd:originalData/plooi:meta/plooi:owmskern/dcterms:identifier, '_')" as="xs:string?"/>
             <xsl:variable name="expression-label" select="gzd:originalData/plooi:meta/plooi:plooiipm/plooi:versie" as="xs:string?"/>
             <xsl:value-of select="$context-path || '/Details/' || $work-label || '/' || $expression-label || '?hit=' || encode-for-uri(xs:string($hit-num)) || '&amp;' || $root/*/req:query-string"/>
             </cl:link>
             <cl:type>secendary</cl:type>
             </cl:button>
             </cl:buttons>
        -->
        <xsl:if test="not($is-bundle)">
          <xsl:variable name="publicatie-url" select="(gzd:enrichedData/gzd:publicatieurl[not(@type = ('xml', 'html'))])[1]" as="xs:string?"/>
          <xsl:if test="$publicatie-url">
            <cl:linklist>
              <cl:label>Bekijk document gepubliceerd op</cl:label>
              <cl:items>
                <cl:item>
                  <cl:text>(gepubliceerd op <xsl:value-of select="utils:format-date(substring(gzd:originalData/plooi:meta/plooi:owmsmantel/dcterms:available/start, 1, 10))"/> | <xsl:value-of select="upper-case(functx:substring-after-last($publicatie-url, '.'))"/>)</cl:text>
                  <cl:link>
                    <!-- bv http://beta-zoekservice1-tst.overheid.nl/repository-plooi/aanleverloket-257732c0-0c0d-4cb3-88c7-d1e054823a7e/1/pdf/plooicb-2021-3.pdf -->
					<!-- voor SP bv;
					        https://open.overheid.nl/repository/oep-6876bbaf4a382a452d3125547d22db505a44d9e3/1/pdf/blg-1048360.pdf -->
                    <xsl:variable name="relative-path" select="substring-after($publicatie-url, '/repository/')"/>
                    <xsl:variable name="encoded-path" select="($relative-path => tokenize('/')) ! encode-for-uri(.) => string-join('/')"/>
                    <xsl:variable name="file-path" select="$context-path || '/repository/' || $encoded-path" as="xs:string"/>
                    <xsl:value-of select="$file-path"/>
                  </cl:link>
                  <cl:label>
                    <xsl:text>Open.overheid.nl</xsl:text>
                  </cl:label>
                </cl:item>
              </cl:items>
            </cl:linklist>
          </xsl:if>
        </xsl:if>
      </xsl:for-each>
    </cl:item>
  </xsl:template>

  <xsl:template name="selected-filters">
    <xsl:param name="index-id" as="xs:string"/>
    <xsl:variable name="index-def" select="idx:get-index($index-id)" as="element(index)"/>
    <cl:selectedfilter>
      <xsl:for-each select="functx:distinct-deep($query-params[@name = $index-def/form-field]/req:value)">
        <cl:item>
          <cl:label>
            <xsl:choose>
              <xsl:when test="$index-def/is-uri = 'true'">
                <xsl:value-of select="functx:capitalize-first(webapp:get-attribute(.)[1])"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="."/>
              </xsl:otherwise>
            </xsl:choose>
          </cl:label>
          <cl:link>
            <xsl:variable name="page-param-value" select="$query-params[@name = 'page']/req:value"/>
            <xsl:variable name="reset-param-values" select="$query-params/req:value except (., $page-param-value)"/>
            <xsl:value-of select="utils:build-url($context-path || /*/req:path, $reset-param-values)"/>
          </cl:link>
          <cl:label-remove>verwijder</cl:label-remove>
        </cl:item>
      </xsl:for-each>
    </cl:selectedfilter>
  </xsl:template>

  <xsl:template name="facet-link-group">
    <xsl:param name="legend" as="xs:string"/>
    <xsl:param name="terms" as="element(facet:term)*"/>
    <xsl:param name="values" as="element(waarde)*"/>
    <xsl:param name="param-name" as="xs:string"/>
    <xsl:if test="$terms">
      <div class="form__row form__row--large">
        <fieldset>
          <legend>
            <xsl:value-of select="$legend"/>
          </legend>
          <cl:facetlinkgroup>
            <xsl:variable name="items" as="element(cl:item)*">
              <xsl:for-each select="$terms">
                <xsl:variable name="term" select="." as="element(facet:term)"/>
                <xsl:variable name="query-param-selector" select="$query-params[@name = $param-name]/req:value[. eq $term/facet:actualTerm]"/>
                <cl:item>
                  <cl:href>
                    <xsl:variable name="page-param-value" select="$query-params[@name = 'page']/req:value"/>
                    <xsl:variable name="reset-param-values" select="$query-params/req:value except ($query-param-selector, $page-param-value)"/>
                    <xsl:value-of select="if ($query-param-selector)
                        then utils:build-url($context-path || $root/*/req:path, $reset-param-values)
                      else utils:build-url($context-path || $root/*/req:path, ($query-params/req:value except $page-param-value), map { string($param-name) : string($term/facet:actualTerm) })"/>
                  </cl:href>
                  <cl:label>
                    <xsl:choose>
                      <xsl:when test="ends-with($term/facet:actualTerm, '_onbekend_')">- Niet gespecificeerd/geconverteerd -</xsl:when>
                      <xsl:otherwise>
                        <xsl:variable name="label" as="xs:string?">
                          <xsl:for-each select="($values[code = $term/facet:actualTerm])[1]">
                            <xsl:value-of select="string-join((ancestor-or-self::waarde/omschrijving, if (.//waarde) then 'Algemeen' else ()), '/')"/>
                          </xsl:for-each>
                        </xsl:variable>
                        <!--
                             <xsl:variable name="label" select="for $a in ($values[code = $term/facet:actualTerm])[1] return string-join($a/ancestor-or-self::waarde/omschrijving, '/')" as="xs:string?"/>
                        -->
                        <xsl:value-of select="if ($label) then $label else '- Niet beschikbaar in waardelijst -'"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </cl:label>
                  <cl:amount>
                    <xsl:value-of select="$term/facet:count"/>
                  </cl:amount>
                  <cl:selected>
                    <xsl:if test="$query-param-selector">
                      <xsl:value-of select="boolean($query-param-selector)"/>
                    </xsl:if>
                  </cl:selected>
                  <cl:special-order>
                    <xsl:choose>
                      <xsl:when test="ends-with($term/facet:actualTerm, '_onbekend_')">ZZZ</xsl:when>
                      <xsl:when test="empty($values[code = $term/facet:actualTerm])">ZZ</xsl:when>
                    </xsl:choose>
                  </cl:special-order>
                </cl:item>
              </xsl:for-each>
            </xsl:variable>
            <xsl:for-each select="$items">
              <xsl:sort select="cl:special-order"/>
              <xsl:sort select="cl:selected" order="descending"/>
              <xsl:sort select="cl:label"/>
              <xsl:sequence select="."/>
            </xsl:for-each>
            <cl:modal>
              <cl:init-closed>true</cl:init-closed>
              <cl:open-button>Toon alle</cl:open-button>
              <cl:close-button>sluiten</cl:close-button>
              <cl:modal-title>
                <xsl:value-of select="$legend"/>
              </cl:modal-title>
            </cl:modal>
          </cl:facetlinkgroup>
        </fieldset>
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>

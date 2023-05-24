<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY laquo "&#171;">
  <!ENTITY raquo "&#187;">
]>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:session="http://www.armatiek.com/xslweb/session"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:http="http://expath.org/ns/http-client"
  xmlns:base64="http://www.armatiek.com/xslweb/functions/base64"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:frbr="https://koop.overheid.nl/namespaces/frbr"
  xmlns:search="https://koop.overheid.nl/namespaces/search"
  xmlns:functx="http://www.functx.com"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:overheid="http://standaarden.overheid.nl/owms/terms/"
  xmlns:plooi="http://standaarden.overheid.nl/plooi/terms/"
  xmlns:xhtml="http://www.w3.org/1999/xhtml"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:utils="https://koop.overheid.nl/namespaces/utils"
  xmlns:sru="http://docs.oasis-open.org/ns/search-ws/sruResponse"
  xmlns:gzd="http://standaarden.overheid.nl/sru"
  xmlns:map="http://www.w3.org/2005/xpath-functions/map"
  xmlns:hl="info:srw/extension/2/highlight-1.0"
  xmlns:dlogger="http://www.armatiek.nl/xslweb/functions/dlogger"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:import href="../../cb-common/xsl/common/frbr.xsl"/>
  <xsl:import href="page.xsl"/>

  <xsl:include href="custom-query-clause.xsl"/>

  <xsl:param name="config:plooi-repos-endpoint" as="xs:string"/>
  <xsl:param name="config:pdf-viewer" as="xs:boolean?"/>
  <xsl:param name="config:plooi-frontend-endpoint" as="xs:string"/>

  <xsl:variable name="root" select="/" as="document-node()"/>
  <xsl:variable name="identifier" select="$root/*/plooi:plooi/plooi:meta/plooi:owmskern/dcterms:identifier" as="xs:string"/>
  <xsl:variable name="dcnid" select="$root/*/plooi:plooi/plooi:meta/plooi:plooiipm/dcterms:identifier" as="xs:string"/>
  <xsl:variable name="work-label" select="functx:substring-before-last($identifier, '_')" as="xs:string"/>
  <xsl:variable name="expression-label" select="functx:substring-after-last($identifier, '_')" as="xs:string"/>
  <xsl:variable name="search-qs-cookie" select="/*/req:cookies/req:cookie[req:name = $active-session-id || 'searchQueryString']/req:value" as="xs:string?"/>
  <xsl:variable name="search-qs" select="if ($search-qs-cookie) then base64:decode($search-qs-cookie) else ()" as="xs:string?"/>

  <xsl:variable name="search-query-params-cookie" select="/*/req:cookies/req:cookie[req:name = $active-session-id || 'searchQueryParams']/req:value" as="xs:string?"/>
  <xsl:variable name="search-query-params-value" select="if ($search-query-params-cookie) then base64:decode($search-query-params-cookie) else ()" as="xs:string?"/>
  <xsl:variable name="search-query-params" select="if ($search-query-params-value) then parse-xml($search-query-params-value)/* else ()" as="element(req:parameter)*"/>

  <xsl:variable name="is-part" select="exists($root/*/plooi:plooi/plooi:meta/plooi:owmsmantel/dcterms:isPartOf)" as="xs:boolean"/>
  <xsl:variable name="is-bundle" select="exists($root/*/plooi:plooi/plooi:meta/plooi:owmsmantel/dcterms:hasPart)" as="xs:boolean"/>
  <xsl:variable name="is-single" select="not($is-part) and not($is-bundle)" as="xs:boolean"/>

  <!-- Required overrides: -->
  <xsl:template name="title">Platform open overheidsinformatie</xsl:template>

  <xsl:template match="links" as="element(link)*" mode="link">
    <xsl:next-match/>
    <!-- TODO -->
  </xsl:template>

  <xsl:variable name="pdf-path" as="xs:string?">
    <xsl:for-each select="($root/*/plooi:plooi/plooi:body/plooi:documenten/plooi:document[@published='true' and not(plooi:ref)])[1]">
      <!-- Note that PDF documents for the PDF viewer are loaded from a separate PDF highlighter service at this location -->
      <xsl:value-of
              select="$config:plooi-repos-endpoint || '/' || $work-label || '/' || plooi:manifestatie-label || '?max-pages=10' || $token-params"/>
    </xsl:for-each>
  </xsl:variable>
  <xsl:variable name="pdf-path-clean" as="xs:string">
    <xsl:value-of select="functx:substring-before-last($pdf-path, '?')"/>
  </xsl:variable>


  <xsl:template match="scripts" as="element(script)*" mode="script">
    <xsl:if test="$config:pdf-viewer">
      <xsl:next-match/>
      <script>
        $(function() {
          var pdfjsVersion = (supportsES6) ? "2.16.105-dist" : "2.7.570c-es5-dist";
          console.log(pdfjsVersion);
          var src = '<xsl:value-of select="$context-path"/>/pdfjs/' + pdfjsVersion + '/web/viewer.html?file=<xsl:value-of select="encode-for-uri($pdf-path)"/>#zoom=page-width';
          $('#pdfjs-iframe').attr('src', src);
        });
      </script>
      <script>
        document.addEventListener("webviewerloaded", function() {
          var app = $("#pdfjs-iframe")[0].contentWindow.PDFViewerApplication;
          app.initializedPromise.then(function() {
            app.eventBus.on("documentloaded", function(event) {
              event.source.pdfDocument.getMetadata().then(function(stuff) {
                var currentNPages = event.source.pdfDocument.numPages;
                if (stuff.info.Custom.npages === undefined) {
                  $("#btn-load-all-pages").attr("hidden", true);
                } else {
                  var originalNPages = stuff.info.Custom.npages;
                  if (currentNPages >= originalNPages) {
                    $("#btn-load-all-pages").attr("hidden", true);
                  } else {
                    $("#btn-load-all-pages").html("Laad alle " + originalNPages + " pagina's").attr("hidden", false);
                  }
                }
              }).catch(function(err) {
                console.log('Error getting meta data');
                console.log(err);
              });
            });
          });
        });
      </script>
    </xsl:if>
  </xsl:template>

  <xsl:template match="metas" as="element(meta)*" mode="meta">
    <xsl:next-match/>
    <!-- TODO -->
  </xsl:template>

  <xsl:function name="cl:get-selected-nav-id" as="xs:string">
    <xsl:text>detail</xsl:text>
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
        <cl:path>
          <xsl:value-of select="$context-path || '/uitgebreid-zoeken'"/>
        </cl:path>
        <cl:label>Zoeken</cl:label>
      </cl:level>
      <cl:level>
        <cl:path>
          <xsl:value-of select="$context-path || '/zoekresultaten' || xs:string(if ($search-qs) then $search-qs else ())"/>
        </cl:path>
        <cl:label>Zoekresultaten</cl:label>
      </cl:level>
      <cl:level>
        <cl:label>Details</cl:label>
      </cl:level>
    </cl:breadcrumb>
  </xsl:template>

  <xsl:template name="status" as="element(status)">
    <xsl:choose>
      <xsl:when test="$root/*/error/@status = '500'">
        <status code="500" message="Error communicating with backend server"/>
      </xsl:when>
      <xsl:when test="$root/*/error/@status = '600'">
        <status code="500" message="Timeout communicating with backend server"/>
      </xsl:when>
      <xsl:when test="$root/*/error/@status = '404' or not($root/*/plooi:plooi)">
        <status code="404" message="Document not found"/>
      </xsl:when>
      <xsl:otherwise>
        <status code="200" message=""/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:variable name="hit" select="($query-params[@name='hit']/req:value[1], '-1')[1] cast as xs:integer" as="xs:integer"/>

  <xsl:variable name="sru-result" as="document-node()?">
    <xsl:if test="not($hit = -1)">
      <xsl:variable name="result" as="document-node()?">
        <xsl:call-template name="search:execute-search">
          <xsl:with-param name="endpoint" select="$config:sru-endpoint" as="xs:string"/>
          <xsl:with-param name="maximum-records" select="3" as="xs:integer"/>
          <xsl:with-param name="query-parameters" select="($query-params, req:get-attribute('text-query-param'))" as="element(req:parameter)*"/>
          <xsl:with-param name="param-name-page" select="'page'" as="xs:string"/>
          <xsl:with-param name="param-name-sort" select="'sort'" as="xs:string"/>
          <xsl:with-param name="start" select="max(($hit - 1, 1))" as="xs:integer"/>
          <xsl:with-param name="index-def" select="$index-def" as="element(index)*"/>
          <xsl:with-param name="sort-def" select="$sort-def" as="element(sort)*"/>
          <xsl:with-param name="x-connection" select="$config:sru-x-connection"/>
          <xsl:with-param name="sru-path" select="'/sru/Search'" as="xs:string"/>
          <xsl:with-param name="x-params" select="'x-hltokens=true'" as="xs:string*"/>
          <xsl:with-param name="timeout" select="30" as="xs:integer"/>
        </xsl:call-template>
      </xsl:variable>
      <xsl:if test="search:has-identifier($result, $identifier)">
        <xsl:sequence select="$result"/>
      </xsl:if>
    </xsl:if>
  </xsl:variable>

  <xsl:variable name="token-params" as="xs:string?">
    <xsl:variable name="tokens" as="xs:string*"
      select="for $a in $sru-result/sru:searchRetrieveResponse/sru:extraResponseData/hl:highlight/hl:tokens[@id = $identifier]/hl:token return encode-for-uri($a)"/>
    <xsl:if test="exists($tokens)">
      <xsl:value-of select="string-join(('', $tokens), '&amp;token=')"/>
    </xsl:if>
  </xsl:variable>

  <xsl:template name="main">
    <xsl:choose>
      <xsl:when test="$root/*/error/@status = '500'">
        <div class="container row">
          <div class="columns columns--sidebar-left">
            <div class="column">
              <h2>Er is een probleem opgetreden bij de communicatie met de documenten server</h2>
            </div>
          </div>
        </div>
      </xsl:when>
      <xsl:when test="$root/*/error/@status = '600'">
        <div class="container row">
          <div class="columns columns--sidebar-left">
            <div class="column">
              <h2>Er is een timeout opgetreden bij de communicatie met de documenten server</h2>
            </div>
          </div>
        </div>
      </xsl:when>
      <xsl:when test="$root/*/error/@status = '404' or not($root/*/plooi:plooi)">
        <div class="container row">
          <div class="columns columns--sidebar-left">
            <div class="column">
              <h2>Het document kon niet worden gevonden</h2>
            </div>
          </div>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="meta-elem" select="$root/*/plooi:plooi/plooi:meta" as="element(plooi:meta)"/>
        <xsl:variable name="xml-url" select="$root/*/xml-url" as="xs:string"/>
        <div class="container columns columns--sticky-sidebar" data-decorator="init-scroll-chapter">

          <div class="columns--sticky-sidebar__sidebar" data-decorator="stick-sidebar add-mobile-foldability" data-config="{{&quot;scrollContentElement&quot;:&quot;.js-scrollContentElement&quot;}}" id="toggleable-1">
            <div>
              <xsl:if test="$hit gt 0">
                <p>
                  <a href="{$context-path || '/zoekresultaten' || xs:string(if ($search-qs) then $search-qs else ())}" class="icon icon--arrow-cta-right">Terug naar zoekresultaten</a>
                </p>
              </xsl:if>
              <div class="scrollContentReceiver"></div>
              <cl:nav>
                <cl:items/>
              </cl:nav>
            </div>

          </div>

          <div id="content" role="main" class="column content content--publication">

            <cl:pageactions>
              <cl:items>
                <cl:item>
                  <cl:text>Kopieer link</cl:text>
                  <cl:link>#</cl:link>
                  <cl:icon>
                    <xsl:value-of select="$cb-context-path || '/images/icon-permalink.svg'"/>
                  </cl:icon>
                  <cl:modal>true</cl:modal>
                  <cl:modal-id>1</cl:modal-id>
                </cl:item>
                <cl:item>
                  <cl:text>Print</cl:text>
                  <cl:link>javascript:window.print();</cl:link>
                  <cl:icon>
                    <xsl:value-of select="$cb-context-path || '/images/icon-print.svg'"/>
                  </cl:icon>
                </cl:item>
                <xsl:if test="$config:debug">
                  <cl:item>
                    <cl:text>[Bekijken expression XML]</cl:text>
                    <cl:link>
                      <xsl:value-of select="/*/xml-url"/>
                    </cl:link>
                    <cl:icon>
                      <xsl:value-of select="$cb-context-path || '/images/icon-document.svg'"/>
                    </cl:icon>
                  </cl:item>
                </xsl:if>
              </cl:items>
            </cl:pageactions>

            <cl:permalink>
              <cl:id>1</cl:id>
              <cl:modal-title>Permanente link</cl:modal-title>
              <cl:copydata>
                <cl:triggerLabel>Kopieer link</cl:triggerLabel>
                <cl:triggerCopiedlabel>Gekopieerd naar clipboard</cl:triggerCopiedlabel>
                <cl:triggerClassModifier>copydata__trigger</cl:triggerClassModifier>
                <cl:url>
                  <xsl:value-of select="$config:plooi-frontend-endpoint || $root/*/req:path"/>
                </cl:url>
              </cl:copydata>
            </cl:permalink>

            <!--
            <div class="hidden-desktop align-right">
              <button class="button button - - primary button - - icon-filter block-element" data-decorator="init-modalsidebar" data-handler="open-modalsidebar"
                data-modal="modal-filter">Pagina navigatie</button>
            </div>

            <div class="align-right">
              <ul class="list list - - inline">
                <xsl:if test="$config:debug">
                  <li>
                    <a href="{/*/xml-url}" class="link-iconed link-iconed - - download" target="_blank">[Bekijken expression XML]</a>
                  </li>
                </xsl:if>
                <li>
                  <a href="javascript:window.print();" class="link-iconed link-iconed - - print">Printen</a>
                </li>
              </ul>
            </div>
            -->

            <div class="js-scrollContentElement">
              <h1 class="h2">
                <xsl:value-of select="$meta-elem/plooi:owmskern/dcterms:title"/>
              </h1>
            </div>

            <xsl:if test="$meta-elem/plooi:owmsmantel/dcterms:isPartOf">
              <div class="alert alert--info " role="alert">
                <xsl:variable name="part-of" select="$meta-elem/plooi:owmsmantel/dcterms:isPartOf" as="xs:string"/>
                <xsl:variable name="href"
                  select="$context-path || '/Details/' || functx:substring-before-last($part-of, '_') || '/' || functx:substring-after-last($part-of, '_')" as="xs:string"/>
                <div class="alert__inner">Dit document is onderdeel van &quot;<a href="{$href}"><xsl:value-of select="$meta-elem/plooi:owmsmantel/dcterms:alternative[1]"/></a>&quot;.</div>
              </div>
            </xsl:if>

            <xsl:variable name="metadata">
              <xsl:if test="$meta-elem/plooi:owmsmantel/dcterms:description">
                <section class="js-scrollSection" id="section-omschrijving">
                  <h3 id="omschrijving">Omschrijving</h3>
                </section>
                <p>
                  <xsl:value-of select="$meta-elem/plooi:owmsmantel/dcterms:description"/>
                </p>
              </xsl:if>

              <section class="js-scrollSection" id="section-tabelgegevens">
                <table class="table__data-overview">
                  <tbody>
                    <!--
                    <tr>
                      <th>Bron/Auteur</th>
                      <td>
                        <xsl:call-template name="show-multiple-rows">
                          <xsl:with-param name="values" select="for $a in $meta-elem/plooi:owmskern/dcterms:creator[normalize-space()] return functx:capitalize-first($a)" as="xs:string*"/>
                        </xsl:call-template>
                      </td>
                    </tr>
                    -->
                    <tr>
                      <th>Verantwoordelijke</th>
                      <td>
                        <xsl:variable name="verantwoordelijken" as="xs:string*">
                          <xsl:for-each select="$meta-elem/plooi:plooiipm/plooi:verantwoordelijke/@resourceIdentifier">
                            <xsl:variable name="code-eerstverantwoordelijke" select="." as="xs:string?"/>
                            <xsl:variable name="eerstverantwoordelijke" select="functx:capitalize-first((search:get-valuelists($valuelist-labels-organisation)/waardelijst//waarde[not(@type = 'algemeen') and code = $code-eerstverantwoordelijke]/omschrijving)[1])" as="xs:string?"/>
                            <xsl:choose>
                              <xsl:when test="$config:debug">
                                <xsl:choose>
                                  <xsl:when test="not($code-eerstverantwoordelijke)">Het gegeven "/plooi:plooi/plooi:meta/plooi:plooiipm/plooi:verantwoordelijke/@resourceIdentifier" is niet beschikbaar in de <a href="{/*/xml-url}" target="_blank">expression xml</a></xsl:when>
                                  <xsl:when test="not($eerstverantwoordelijke)">De uri "<xsl:value-of select="$code-eerstverantwoordelijke"/>" kon niet worden gevonden in de waardelijsten</xsl:when>
                                  <xsl:otherwise>
                                    <xsl:value-of select="$eerstverantwoordelijke"/>
                                  </xsl:otherwise>
                                </xsl:choose>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:value-of select="if ($eerstverantwoordelijke) then $eerstverantwoordelijke else 'Onbekend'"/>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:for-each>
                        </xsl:variable>
                        <xsl:call-template name="show-multiple-rows">
                          <xsl:with-param name="values" select="$verantwoordelijken" as="xs:string*"/>
                        </xsl:call-template>
                      </td>
                    </tr>
                    <xsl:variable name="code-themas" select="$meta-elem/plooi:plooiipm/plooi:topthema/@resourceIdentifier" as="xs:string*"/>
                    <xsl:if test="exists($code-themas)">
                      <tr>
                        <th>Thema</th>
                        <td>
                          <xsl:variable name="values" as="xs:string*">
                            <xsl:for-each select="$code-themas">
                              <xsl:variable name="code" select="." as="xs:string"/>
                              <xsl:variable name="thema" select="functx:capitalize-first((search:get-valuelists($valuelist-label-thema)/waardelijst//waarde[not(@type = 'algemeen') and code = $code]/omschrijving)[1])" as="xs:string?"/>
                              <xsl:choose>
                                <xsl:when test="$config:debug">
                                  <xsl:choose>
                                    <xsl:when test="not($thema)">
                                      <xsl:value-of select="string-join('De uri &quot;' || . || '&quot; kon niet worden gevonden in de waardelijst')"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:value-of select="$thema"/>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </xsl:when>
                                <xsl:otherwise>
                                  <xsl:value-of select="if ($thema) then $thema else 'Onbekend'"/>
                                </xsl:otherwise>
                              </xsl:choose>
                            </xsl:for-each>
                          </xsl:variable>
                          <xsl:call-template name="show-multiple-rows">
                            <xsl:with-param name="values" select="$values" as="xs:string*"/>
                          </xsl:call-template>
                        </td>
                      </tr>
                    </xsl:if>
                    <xsl:variable name="code-types" select="$meta-elem/plooi:plooiipm/plooi:informatiecategorie/@resourceIdentifier" as="xs:string*"/>
                    <xsl:if test="exists($code-types)">
                      <tr>
                        <th>Documentsoort</th>
                        <td>
                          <xsl:variable name="values" as="xs:string*">
                            <xsl:for-each select="$code-types">
                              <xsl:variable name="code" select="." as="xs:string"/>
                              <xsl:variable name="type" select="functx:capitalize-first((search:get-valuelists($valuelist-label-type)/waardelijst//waarde[not(@type = 'algemeen') and code = $code]/omschrijving)[1])" as="xs:string?"/>
                              <xsl:choose>
                                <xsl:when test="$config:debug">
                                  <xsl:choose>
                                    <xsl:when test="not($type)">
                                      <xsl:value-of select="string-join('De uri &quot;' || . || '&quot; kon niet worden gevonden in plooi_portaal_vX.xml')"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:value-of select="$type"/>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </xsl:when>
                                <xsl:otherwise>
                                  <xsl:value-of select="if ($type) then $type else 'Onbekend'"/>
                                </xsl:otherwise>
                              </xsl:choose>
                            </xsl:for-each>
                          </xsl:variable>
                          <xsl:call-template name="show-multiple-rows">
                            <xsl:with-param name="values" select="$values" as="xs:string*"/>
                          </xsl:call-template>
                        </td>
                      </tr>
                    </xsl:if>
                    <tr>
                      <th>Publicatiedatum</th>
                      <td>
                        <xsl:value-of select="utils:format-date(substring($meta-elem/plooi:owmsmantel/dcterms:available/start, 1, 10))"/>
                      </td>
                    </tr>
                    <tr>
                      <th>Documentdatum</th>
                      <td>
                        <xsl:value-of select="utils:format-date(substring($meta-elem/plooi:owmsmantel/dcterms:issued, 1, 10))"/>
                      </td>
                    </tr>
                    <xsl:variable name="custom-element-names" select="distinct-values(for $a in $meta-elem/plooi:plooiipm/plooi:extrametadata return $a/@name)" as="xs:string*"/>
                    <xsl:for-each select="$custom-element-names">
                      <xsl:sort select="."/>
                      <xsl:variable name="name" select="." as="xs:string"/>
                      <tr>
                        <th>
                          <xsl:value-of select="functx:capitalize-first(lower-case(functx:camel-case-to-words(functx:substring-after-last($name, '.'), ' ')))"/>
                        </th>
                        <td>
                          <xsl:variable
                            name="values"
                            select="for $a in $meta-elem/plooi:plooiipm/plooi:extrametadata[@name = $name] return if ($a castable as xs:date) then utils:format-date($a) else $a" as="xs:string*"/>
                          <xsl:call-template name="show-multiple-rows">
                            <xsl:with-param name="values" select="$values" as="xs:string*"/>
                          </xsl:call-template>
                        </td>
                      </tr>
                    </xsl:for-each>
                    <xsl:if test="$meta-elem/plooi:othermeta/plooi:bronmeta/plooi:subject">
                      <tr>
                        <th>Onderwerp</th>
                        <td>
                          <xsl:call-template name="show-multiple-rows">
                            <xsl:with-param name="values" select="$meta-elem/plooi:othermeta/plooi:bronmeta/plooi:subject" as="xs:string*"/>
                          </xsl:call-template>
                        </td>
                      </tr>
                    </xsl:if>
                  </tbody>
                </table>
              </section>

              <xsl:where-populated>
                <section class="js-scrollSection" id="section-tekst">
                  <xsl:apply-templates select="$root/*/plooi:plooi/plooi:body/plooi:tekst/node()"/>
                </section>
              </xsl:where-populated>

              <section class="js-scrollSection" id="section-publicaties">
                <h2 id="publicaties">Publicaties</h2>
                <cl:result-list>
                  <cl:context>
                    <cl:items>
                      <xsl:if test="$meta-elem/plooi:owmsmantel/dcterms:source/node()">
                        <cl:item>
                          <cl:theme>publication</cl:theme>
                          <cl:title-header>h2</cl:title-header>
                          <cl:title>Publicatie op <xsl:value-of select="$meta-elem/plooi:plooiipm/plooi:aanbieder"/></cl:title>
                          <cl:title-href>
                            <xsl:value-of select="$meta-elem/plooi:owmsmantel/dcterms:source"/>
                          </cl:title-href>
                          <cl:text>&#160;</cl:text>
                          <cl:title-icon>true</cl:title-icon>
                          <!--
                          <cl:buttons>
                            <cl:button>
                              <cl:label>Bekijk publicatie</cl:label>
                              <cl:link>
                                <xsl:value-of select="$meta-elem/plooi:owmsmantel/dcterms:source"/>
                              </cl:link>
                              <cl:type>secendary</cl:type>
                            </cl:button>
                          </cl:buttons>
                          -->
                        </cl:item>
                      </xsl:if>
                      <xsl:for-each select="$root/*/plooi:plooi/plooi:body/plooi:documenten/plooi:document[not(@published='false')]">
                        <cl:item>
                          <cl:theme>publication</cl:theme>
                          <cl:title-header>h2</cl:title-header>
                          <cl:title>Publicatie op open.overheid.nl</cl:title>
                          <cl:title-icon>true</cl:title-icon>
                          <cl:title-href>
                            <xsl:choose>
                              <xsl:when test="plooi:ref">
                                <xsl:variable name="work-label" select="functx:substring-before-last(plooi:ref, '_')" as="xs:string?"/>
                                <xsl:variable name="expr-label" select="functx:substring-after-last(plooi:ref, '_')" as="xs:string?"/>
                                <xsl:value-of select="$context-path || '/Details/' || $work-label || '/' || $expr-label"/>
                              </xsl:when>
                              <xsl:otherwise>
<!--                                <xsl:variable name="file-url" select="resolve-uri('../' || plooi:manifestatie-label || '/' || encode-for-uri(plooi:bestandsnaam), $xml-url)" as="xs:string"/>-->
                                <xsl:variable name="file-url" select="$config:plooi-repos-endpoint || '/' || $dcnid || '/' || $expression-label" as="xs:string"/>
                                <xsl:variable name="file-path" select="$context-path || '/repository' || substring-after($file-url, $config:plooi-repos-endpoint)" as="xs:string"/>
<!--                                <xsl:variable name="pdf-path" select="$pdf-path || '/repository' || substring-after($file-url, $config:plooi-repos-endpoint)" as="xs:string"/>-->
                                <xsl:if test="$config:development-mode">
                                  <xsl:sequence select="log:log('INFO', 'plooi-ref-file-url: ' || $file-url)"/>
                                  <xsl:sequence select="log:log('INFO', 'plooi-ref-file-path: ' || $file-path)"/>
                                  <xsl:sequence select="log:log('INFO', 'pdf-path: ' || $pdf-path)"/>
                                  <xsl:sequence select="log:log('INFO', 'pdf-path-clean: ' || $pdf-path-clean)"/>
                                </xsl:if>
                                <xsl:value-of select="$pdf-path-clean"/>
                              </xsl:otherwise>
                            </xsl:choose>
                          </cl:title-href>
                          <cl:text>
                            <xsl:choose>
                              <xsl:when test="plooi:titel">
                                <xsl:value-of select="plooi:titel"/>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:value-of select="'Bestand: ' || plooi:bestandsnaam"/>
                              </xsl:otherwise>
                            </xsl:choose>
                          </cl:text>
                          <!--
                          <cl:buttons>
                            <cl:button>
                              <cl:label>Bekijk publicatie</cl:label>
                              <cl:link>
                                <xsl:choose>
                                  <xsl:when test="plooi:ref">
                                    <xsl:variable name="work-label" select="functx:substring-before-last(plooi:ref, '_')" as="xs:string?"/>
                                    <xsl:variable name="expr-label" select="functx:substring-after-last(plooi:ref, '_')" as="xs:string?"/>
                                    <xsl:value-of select="$context-path || '/Details/' || $work-label || '/' || $expr-label"/>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:value-of select="resolve-uri('../' || plooi:manifestatie-label || '/' || plooi:bestandsnaam, $xml-url)"/>
                                  </xsl:otherwise>
                                </xsl:choose>
                              </cl:link>
                              <cl:type>secendary</cl:type>
                            </cl:button>
                          </cl:buttons>
                          -->
                        </cl:item>
                      </xsl:for-each>
                    </cl:items>
                  </cl:context>
                </cl:result-list>
              </section>
            </xsl:variable>

            <!--
            <xsl:if test="$root/*/plooi:plooi/plooi:meta/plooi:owmsmantel/dcterms:relation">
              <h2 id="relaties">Relaties</h2>
              <ul class="list list - - unstyled">
                <xsl:for-each select="()">
                  <li>
                    <a href="#"></a>
                  </li>
                </xsl:for-each>
              </ul>
            </xsl:if>
            -->

            <xsl:choose>
              <xsl:when test="($is-part or $is-single) and exists($pdf-path) and $config:pdf-viewer">
                <cl:tabs>
                  <cl:tab>
                    <cl:id>gegevens</cl:id>
                    <cl:label>Document gegevens</cl:label>
                    <cl:content>
                      <section class="js-scrollSection" id="section-metadata">
                        <xsl:sequence select="$metadata"/>
                      </section>
                    </cl:content>
                  </cl:tab>
                  <cl:tab>
                    <cl:id>tekst</cl:id>
                    <cl:label>Document tekst</cl:label>
                    <cl:content>
                      <section class="js-scrollSection" id="section-preview">
                        <cl:button>
                          <cl:id>btn-load-all-pages</cl:id>
                          <cl:modifier>button--secondary</cl:modifier>
                          <cl:text>Laad alle pagina's</cl:text>
                          <cl:button>true</cl:button>
                          <cl:hidden>true</cl:hidden>
                          <cl:type>button</cl:type>
                        </cl:button>
                        <iframe id="pdfjs-iframe" allowfullscreen="allowfullscreen" webkitallowfullscreen="webkitallowfullscreen" style="width:100%;height:80vh;"/>
                      </section>
                    </cl:content>
                  </cl:tab>
                </cl:tabs>
              </xsl:when>
              <xsl:otherwise>
                <xsl:sequence select="$metadata"/>
              </xsl:otherwise>
            </xsl:choose>
          </div>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="show-multiple-rows">
    <xsl:param name="values" as="xs:string*"/>
    <xsl:choose>
      <xsl:when test="count($values) eq 0"/>
      <xsl:when test="count($values) eq 1">
        <xsl:value-of select="$values"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:attribute name="class">u-nopadding</xsl:attribute>
        <div data-decorator="showmoreless">
          <xsl:attribute name="data-config">{"labelmore":"Toon meer"}</xsl:attribute>
          <ul class="list list--underlined">
            <xsl:for-each select="$values">
              <li>
                <xsl:value-of select="."/>
              </li>
            </xsl:for-each>
          </ul>
        </div>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="xhtml:h2">
    <h3>
      <xsl:apply-templates select="@*|node()"/>
    </h3>
  </xsl:template>

  <xsl:template match="xhtml:*">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="xhtml:*/@*">
    <xsl:attribute name="{local-name()}" select="."/>
  </xsl:template>

  <xsl:function name="search:get-identifier" as="xs:string?">
    <xsl:param name="sru-result" as="document-node()"/>
    <xsl:param name="hit" as="xs:integer"/>
    <xsl:value-of select="$sru-result/sru:searchRetrieveResponse/sru:records/sru:record[$hit]/sru:recordData/gzd:gzd/gzd:originalData/plooi:meta/plooi:owmskern/dcterms:identifier"/>
  </xsl:function>

  <xsl:function name="search:get-identifier" as="xs:string?">
    <xsl:param name="sru-result" as="document-node()"/>
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:param name="offset" as="xs:integer"/>
    <xsl:variable name="identifiers" select="$sru-result/sru:searchRetrieveResponse/sru:records/sru:record/sru:recordData/gzd:gzd/gzd:originalData/plooi:meta/plooi:owmskern/dcterms:identifier" as="xs:string*"/>
    <xsl:value-of select="$identifiers[index-of($identifiers, $identifier) + $offset]"/>
  </xsl:function>

  <xsl:function name="search:has-identifier" as="xs:boolean">
    <xsl:param name="sru-result" as="document-node()"/>
    <xsl:param name="identifier" as="xs:string"/>
    <xsl:sequence select="exists($sru-result/sru:searchRetrieveResponse/sru:records/sru:record/sru:recordData/gzd:gzd/gzd:originalData/plooi:meta/plooi:owmskern/dcterms:identifier[. = $identifier])"/>
  </xsl:function>

  <xsl:function name="local:get-expression-path" as="xs:string">
    <xsl:param name="expression-id" as="xs:string"/>
    <xsl:value-of select="functx:substring-before-last($expression-id, '_') || '/' || functx:substring-after-last($expression-id, '_') "/>
  </xsl:function>

</xsl:stylesheet>

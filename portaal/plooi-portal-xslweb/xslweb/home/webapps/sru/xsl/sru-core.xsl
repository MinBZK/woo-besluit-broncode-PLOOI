<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:map="http://www.w3.org/2005/xpath-functions/map"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:wfx="http://www.armatiek.com/xslweb/functions/webapp"
  xmlns:ext="http://zoekservice.overheid.nl/extensions"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="2.0">

  <xsl:param name="config:development-mode" as="xs:boolean"/>
  <xsl:param name="config:solr-uri" as="xs:string"/>
  <xsl:param name="config:sru-uri" as="xs:string"/>
  <xsl:param name="config:max-records" as="xs:integer"/>

  <xsl:include href="sru-validation.xsl"/>

  <xsl:variable name="query-params" select="/*/req:parameters/req:parameter" as="element()*"/>

  <xsl:variable name="sru-version" select="$query-params[@name='version']/req:value[1]" as="xs:string?"/>

  <xsl:variable name="facet-limit-specified" select="$query-params[@name='facetLimit']/req:value[1]" as="xs:string?"/>
  <xsl:variable name="facet-limit" select="if ($facet-limit-specified) then $facet-limit-specified else '-1'" as="xs:string"/>

  <xsl:variable name="default-num-records" as="xs:integer">
    <xsl:call-template name="get-default-num-records"/>
  </xsl:variable>

  <xsl:variable name="apos">&apos;</xsl:variable>
  <xsl:variable name="quote">&quot;</xsl:variable>
  <xsl:variable name="amp">&amp;</xsl:variable>

  <xsl:variable name="start" as="xs:integer">
    <xsl:variable name="start-record" select="$query-params[@name='startRecord']/req:value" as="xs:string?"/>
    <xsl:value-of select="if ($start-record) then (xs:integer($start-record) - 1) else 0"/>
  </xsl:variable>

  <xsl:variable name="facet-template" as="element()?">
    <xsl:choose>
      <xsl:when test="($sru-version = '1.2') and ($query-params[@name='x-info-1-accept']/req:value = 'any')">
        <xsl:call-template name="facetedResults"/>
      </xsl:when>
      <xsl:when test="$sru-version = '2.0'">
        <xsl:call-template name="facetedResults"/>
      </xsl:when>
    </xsl:choose>
  </xsl:variable>

  <xsl:function name="local:convert-boolean-operator" as="xs:string">
    <xsl:param name="operator" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="lower-case($operator) = ('and', 'or', 'not')">
        <xsl:value-of select="upper-case($operator)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">Boolean operator "<xsl:value-of select="$operator"/>" not supported</xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="local:convert-sort-modifier" as="xs:string">
    <xsl:param name="modifier" as="xs:string?"/>
    <xsl:choose>
      <xsl:when test="not($modifier) or $modifier = 'sort.ascending'">asc</xsl:when>
      <xsl:when test="$modifier = 'sort.descending'">desc</xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">Sort modifier "<xsl:value-of select="$modifier"/>" not supported</xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="local:trim-quotes" as="xs:string">
    <xsl:param name="arg" as="xs:string?"/>
    <xsl:param name="type" as="xs:string?"/>
    <xsl:sequence select="replace(replace($arg,'&quot;$',''),'^&quot;','')"/>
  </xsl:function>

  <xsl:function name="local:trim" as="xs:string">
    <xsl:param name="arg" as="xs:string?"/>
    <xsl:sequence select="replace(replace($arg,'\s+$', ''),'^\s+', '')"/>
  </xsl:function>

  <xsl:function name="local:left-pad" as="xs:string">
    <xsl:param name="stringToPad" as="xs:string?"/>
    <xsl:param name="length" as="xs:integer"/>
    <xsl:param name="padChar" as="xs:string"/>
    <xsl:sequence select="string-join((for $i in (1 to $length - string-length($stringToPad)) return $padChar, $stringToPad), '')"/>
  </xsl:function>

  <xsl:function name="local:convert-date-to-solr" as="xs:string">
    <xsl:param name="date" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="matches($date, '\d{4}-\d{1,2}-\d{1,2}')">
        <xsl:variable name="parts" select="tokenize($date, '-')" as="xs:string+"/>
        <xsl:value-of select="concat($parts[1], '-', local:left-pad($parts[2], 2, '0'), '-', local:left-pad($parts[3], 2, '0'), 'T00:00:00.000Z')"/>
      </xsl:when>
      <xsl:when test="matches($date, '\d{1,2}-\d{1,2}-\d{4}')">
        <xsl:variable name="parts" select="tokenize($date, '-')" as="xs:string+"/>
        <xsl:value-of select="concat($parts[3], '-', local:left-pad($parts[2], 2, '0'), '-', local:left-pad($parts[1], 2, '0'), 'T00:00:00.000Z')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$date"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="local:convert-boolean-to-solr" as="xs:string">
    <xsl:param name="value" as="xs:string?"/>
    <xsl:choose>
      <xsl:when test="lower-case($value) = ('true', '1', 'ja')">true</xsl:when>
      <xsl:otherwise>false</xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <xsl:function name="local:escape-term" as="xs:string">
    <!-- + - && || ! ( ) { } [ ] ^ " ~ : \ / -->
    <xsl:param name="term" as="xs:string"/>
    <xsl:value-of select="replace($term, '(\+|-|&amp;&amp;|\|\||!|\(|\)|\{|\}|\[|\]|\^|&quot;|~|:|\\|/)', '\\$1')"/>
  </xsl:function>

  <xsl:function name="local:truncate-str-to-date" as="xs:string">
    <xsl:param name="str" as="xs:string?"/>
    <xsl:value-of select="substring($str, 1, 10)"/>
  </xsl:function>

  <xsl:template match="/">
    <!--
    <xsl:sequence select="log:log('INFO', xs:string(/*/req:query-string))"/>
    -->
    <resp:response>
      <xsl:variable name="http-accept" select="
        (tokenize($query-params[@name='httpAccept']/req:value, '\s*,\s*'),
         tokenize(/*/req:headers/req:header[@name='accept'], '\s*,\s*'))" as="xs:string*"/>
      <xsl:attribute name="status">
        <xsl:choose>
          <xsl:when test="$sru-version = '2.0' and exists($http-accept) and not($http-accept = ('application/sru+xml', 'application/xml', 'text/xml'))">406</xsl:when>
          <xsl:otherwise>200</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <resp:headers>
        <resp:header name="Content-Type">application/xml;charset=UTF-8</resp:header>
        <resp:header name="Content-Type">text/html; charset=utf-8</resp:header>
        <resp:header name="X-Content-Type-Options">nosniff</resp:header>
        <resp:header name="Strict-Transport-Security">max-age=1000; includeSubDomains; preload</resp:header>
        <resp:header name="Content-Security-Policy"> upgrade-insecure-requests</resp:header>
        <!--        <resp:header name="Content-Security-Policy">default-src self; frame-ancestors self;</resp:header>
                       This header prevents the download of images and other layout related items.

                      Using CSP, you can nail down what your site should include and what not. But it is hard and can break your site
                      Using the Content-Security-Policy-Report-Only mode browsers only log resources that would be blocked
                      to the console instead of blocking them. This reporting mechanism gives you a way to check and adjust your ruleset.
                      <resp:header name="Content-Security-Policy-Report-Only"> default-src 'self'; ... report-uri https://reporting URI</resp:header>
                   -->
        <resp:header name="Cache-Control">max-age=30, public, immutable</resp:header>
        <resp:header name="Referrer-Policy">same-origin</resp:header>
        <resp:header name="X-Frame-Options">SAMEORIGIN</resp:header>
      </resp:headers>
      <resp:body>
        <xsl:variable name="diagnostics-params" as="element()">
          <xsl:call-template name="validate-params"/>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="exists($diagnostics-params/diagnostic)">
            <xsl:sequence select="$diagnostics-params"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="$query-params[@name='operation']/req:value = 'explain'">
                <xsl:call-template name="explain"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:variable name="rows" as="xs:integer">
                  <xsl:variable name="max-records" select="$query-params[@name='maximumRecords']/req:value" as="xs:string?"/>
                  <xsl:value-of select="if ($max-records) then min((5000, xs:integer($max-records))) else $default-num-records"/>
                </xsl:variable>
                <xsl:variable name="q" select="$query-params[@name='query']/req:value" as="xs:string"/>
                <xsl:variable name="q1" select="replace($q, '/ascending', '/sort.ascending')" as="xs:string"/>
                <xsl:variable name="query" select="replace($q1, '/descending', '/sort.descending')" as="xs:string"/>
                <xsl:variable name="xcql" select="ext:parse-cql($query)" as="document-node()"/>
                <xsl:choose>
                  <xsl:when test="$config:development-mode and $query-params[@name='cql']/req:value = 'true'">
                    <xsl:sequence select="$xcql"/>
                  </xsl:when>
                  <xsl:when test="$xcql/exception">
                    <diagnostics>
                      <diagnostic>
                        <uri>info:srw/diagnostic/1/22</uri>
                        <details>
                          <xsl:value-of select="$xcql/exception"/>
                        </details>
                        <message>Query syntax error</message>
                      </diagnostic>
                    </diagnostics>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:variable name="diagnostics-query" as="element()">
                      <xsl:call-template name="validate-query">
                        <xsl:with-param name="query" select="$xcql" as="document-node()"/>
                      </xsl:call-template>
                    </xsl:variable>
                    <xsl:choose>
                      <xsl:when test="exists($diagnostics-query/diagnostic)">
                        <xsl:sequence select="$diagnostics-query"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:variable name="params" as="element()">
                          <params>
                            <xsl:if test="$result-block-type">
                              <param>
                                <name>fq</name>
                                <value>
                                  <xsl:value-of select="concat('blocktype:', $result-block-type)"/>
                                </value>
                              </param>
                            </xsl:if>

                            <xsl:variable name="default-filter" as="xs:string?">
                              <xsl:call-template name="default-filter">
                                <xsl:with-param name="xcql" select="$xcql" as="document-node()"/>
                              </xsl:call-template>
                            </xsl:variable>

                            <xsl:if test="$default-filter">
                              <param>
                                <name>fq</name>
                                <value>
                                  <xsl:value-of select="$default-filter"/>
                                </value>
                              </param>
                            </xsl:if>

                            <!-- Query: -->
                            <param>
                              <name>q</name>
                              <value>
                                <xsl:variable name="q" as="xs:string*">
                                  <xsl:apply-templates select="$xcql" mode="query"/>
                                </xsl:variable>
                                <xsl:variable name="nq" select="normalize-space(string-join($q, ''))" as="xs:string?"/>
                                <xsl:value-of select="if ($nq) then $nq else '*:*'"/>
                              </value>
                            </param>

                            <xsl:variable name="cursor-mark" select="if (($start gt 0) and $query) then wfx:get-cache-value('cursor-mark', concat($start, '_', $query)) else ()" as="xs:string?"/>

                            <!-- Sort: -->
                            <xsl:variable name="sort-keys" as="xs:string*"
                              select="for $k in $xcql/*/sortKeys/key return
                              concat((local:get-index-info($k/index, '==')/sort-field, local:get-index-info($k/index, '==')/field)[1], ' ', local:convert-sort-modifier($k/modifiers/modifier/type))"/>
                            <param>
                              <name>sort</name>
                              <value>
                                <xsl:value-of select="string-join((if (empty($sort-keys)) then 'score desc' else $sort-keys, 'identifier asc'), ',')"/> <!-- id asc is voorwaarde voor gebruik deep paging met cursorMark -->
                              </value>
                            </param>

                            <!-- Response fields (fl) -->
                            <param>
                              <name>fl</name>
                              <value>
                                <xsl:value-of select="$response-fields"/>
                              </value>
                            </param>

                            <xsl:if test="$facet-template and not($sru-version = '2.0' and $facet-limit = '0')">
                              <param>
                                <name>facet</name>
                                <value>true</value>
                              </param>
                              <param>
                                <name>facet.mincount</name>
                                <value>1</value>
                              </param>
                              <xsl:choose>
                                <xsl:when test="$sru-version = '1.2'">
                                  <xsl:for-each select="$facet-template/datasource/facets/facet/index">
                                    <param>
                                      <name>facet.field</name>
                                      <value>
                                        <xsl:value-of select="local:get-index-info(current(), '==')/facet-field"/>
                                      </value>
                                    </param>
                                  </xsl:for-each>
                                </xsl:when>
                                <xsl:when test="$sru-version = '2.0'">
                                  <xsl:variable name="facet-counts" select="$query-params[starts-with(@name, 'facetCount')]/req:value" as="element(req:value)*"/>
                                  <xsl:variable name="facet-starts" select="$query-params[starts-with(@name, 'facetStart')]/req:value" as="element(req:value)*"/>
                                  <xsl:variable name="facet-sort" select="$query-params[@name='facetSort']/req:value[1]" as="xs:string?"/>
                                  <xsl:choose>
                                    <xsl:when test="exists($facet-counts)">
                                      <!-- facetCount -->
                                      <xsl:for-each select="$facet-counts">
                                        <xsl:variable name="solr-field-name" select="local:get-index-info(substring-after(../@name, ':'), '==')/facet-field" as="xs:string"/>
                                        <param>
                                          <name>facet.field</name>
                                          <value>
                                            <xsl:value-of select="$solr-field-name"/>
                                          </value>
                                        </param>
                                        <param>
                                          <name>f.<xsl:value-of select="$solr-field-name"/>.facet.prefix</name>
                                          <value>
                                            <xsl:value-of select="."/>
                                          </value>
                                        </param>
                                      </xsl:for-each>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <!-- facetLimit -->
                                      <xsl:variable name="parts" select="tokenize($facet-limit, ',')" as="xs:string*"/>
                                      <xsl:variable name="has-unspecified-limit" select="$parts[1] castable as xs:integer" as="xs:boolean"/>
                                      <xsl:variable name="unspecified-limit" select="if ($parts[1] castable as xs:integer) then xs:integer($parts[1]) else -1" as="xs:integer"/>
                                      <xsl:variable
                                        name="specified-facets"
                                        select="map:merge(for $f in $parts[contains(., ':') and local:get-index-info(substring-after(.,':'), '==')/index] return map:entry(xs:string(local:get-index-info(substring-after($f,':'), '==')/index), substring-before($f,':')))"
                                        as="map(xs:string, xs:string)"/>
                                      <xsl:for-each select="$facet-template/datasource/facets/facet/index">
                                        <xsl:variable name="index-info" select="local:get-index-info(., '==')" as="element()"/>
                                        <xsl:variable name="field-name" select="$index-info/index" as="xs:string"/>
                                        <xsl:variable name="is-specified" select="map:contains($specified-facets, $field-name)" as="xs:boolean"/>
                                        <xsl:variable name="solr-field-name" select="local:get-index-info($field-name, '==')/facet-field" as="xs:string"/>
                                        <xsl:variable name="facet-queries" select="local:get-index-info($field-name, '==')/facet-query" as="element(facet-query)*"/>

                                        <xsl:if test="not($unspecified-limit = 0) and ($has-unspecified-limit or $is-specified)">
                                          <xsl:choose>
                                            <xsl:when test="not($facet-queries)">
                                              <param>
                                                <name>facet.field</name>
                                                <value>
                                                  <xsl:value-of select="$solr-field-name"/>
                                                </value>
                                              </param>
                                              <param>
                                                <name>f.<xsl:value-of select="$solr-field-name"/>.facet.limit</name>
                                                <value>
                                                  <xsl:value-of select="if ($is-specified) then map:get($specified-facets, $field-name) else $unspecified-limit"/>
                                                </value>
                                              </param>
                                            </xsl:when>
                                            <xsl:otherwise>
                                              <xsl:for-each select="$facet-queries">
                                                <param>
                                                  <name>facet.query</name>
                                                  <value>
                                                    <xsl:value-of select="'{!tag=' || @tag || '}' || $solr-field-name || ':' || ."/>
                                                  </value>
                                                </param>
                                              </xsl:for-each>
                                            </xsl:otherwise>
                                          </xsl:choose>
                                        </xsl:if>
                                      </xsl:for-each>

                                      <!-- facetStart: -->
                                      <xsl:for-each select="$facet-starts">
                                        <xsl:choose>
                                          <xsl:when test="../@name = 'facetStart'">
                                            <param>
                                              <name>facet.offset</name>
                                              <value>
                                                <xsl:sequence select="xs:integer(.) - 1"/>
                                              </value>
                                            </param>
                                          </xsl:when>
                                          <xsl:otherwise>
                                            <param>
                                              <name>f.<xsl:value-of select="local:get-index-info(substring-after(../@name, ':'), '==')/facet-field"/>.facet.offset</name>
                                              <value>
                                                <xsl:sequence select="xs:integer(.) - 1"/>
                                              </value>
                                            </param>
                                          </xsl:otherwise>
                                        </xsl:choose>
                                      </xsl:for-each>

                                      <!-- facetSort -->
                                      <xsl:variable name="sort" select="tokenize($facet-sort, ',')[1]" as="xs:string?"/>
                                      <xsl:if test="$sort">
                                        <param>
                                          <name>facet.sort</name>
                                          <value>
                                            <xsl:value-of select="if ($sort = 'recordCount') then 'count' else 'index'"/>
                                          </value>
                                        </param>
                                      </xsl:if>

                                    </xsl:otherwise>
                                  </xsl:choose>

                                </xsl:when>
                              </xsl:choose>
                            </xsl:if>

                            <xsl:choose>
                              <!-- cursorMark and start are mutually exclusive parameters: -->
                              <xsl:when test="$start = 0">
                                <param>
                                  <name>cursorMark</name>
                                  <value>*</value>
                                </param>
                              </xsl:when>
                              <xsl:when test="$cursor-mark">
                                <param>
                                  <name>cursorMark</name>
                                  <value>
                                    <xsl:value-of select="$cursor-mark"/>
                                  </value>
                                </param>
                              </xsl:when>
                              <xsl:otherwise>
                                <param>
                                  <name>start</name>
                                  <value>
                                    <xsl:value-of select="$start"/>
                                  </value>
                                </param>
                              </xsl:otherwise>
                            </xsl:choose>

                            <param>
                              <name>rows</name>
                              <value>
                                <xsl:value-of select="$rows"/>
                              </value>
                            </param>

                            <xsl:call-template name="extra-parameters"/>

                            <xsl:variable name="relevant-modifier-clause" select="$xcql//searchClause[relation/modifiers/modifier[normalize-space(type) = 'relevant']]" as="element(searchClause)?"/>
                            <xsl:if test="$relevant-modifier-clause">
                              <param>
                                <name>user-query</name>
                                <value>
                                  <xsl:value-of select="$relevant-modifier-clause/term"/>
                                </value>
                              </param>
                            </xsl:if>

                          </params>
                        </xsl:variable>

                        <xsl:choose>
                          <xsl:when test="$query-params[@name='operation']/req:value = 'cql2solr'">
                            <xsl:sequence select="$params"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:variable name="solr-query-string" select="string-join(for $a in $params/param return concat($a/name, '=', encode-for-uri(normalize-space($a/value))), '&amp;')" as="xs:string"/>
                            <xsl:variable name="solr-uri" select="concat(normalize-space($config:solr-uri), '/select?', $solr-query-string)" as="xs:string"/>

                            <xsl:choose>
                              <xsl:when test="not(doc-available($solr-uri))">
                                <diagnostics>
                                  <diagnostic>
                                    <uri>info:srw/diagnostic/1/1</uri>
                                    <details>Error executing request to backend search engine</details>
                                    <xsl:if test="$config:development-mode">
                                      <solr>
                                        <xsl:value-of select="$solr-uri"/>
                                      </solr>
                                    </xsl:if>
                                    <message>General system error</message>
                                  </diagnostic>
                                </diagnostics>
                              </xsl:when>
                              <!--
                              <xsl:when test="$solr-result/*/lst[@name = 'error']">
                                <diagnostics>
                                  <diagnostic>
                                    <uri>info:srw/diagnostic/1/10</uri>
                                    <message>Query syntax error</message>
                                  </diagnostic>
                                </diagnostics>
                              </xsl:when>
                              -->
                              <xsl:otherwise>
                                <xsl:variable name="solr-result" select="document($solr-uri)" as="document-node()"/>
                                <xsl:variable name="num-found" select="xs:integer($solr-result/response/result[@name='response']/@numFound)" as="xs:integer?"/>
                                <xsl:choose>
                                  <xsl:when test="$num-found gt $config:max-records">
                                    <diagnostics>
                                      <diagnostic>
                                        <uri>info:srw/diagnostic/1/60</uri>
                                        <details>
                                          <xsl:value-of select="$config:max-records"/>
                                        </details>
                                        <message>Result set not created: too many matching records</message>
                                      </diagnostic>
                                    </diagnostics>
                                  </xsl:when>
                                  <xsl:when test="$config:development-mode and $query-params[@name='solr']/req:value = 'true'">
                                    <xsl:sequence select="$solr-result"/>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:variable name="next-cursor-mark" select="$solr-result/response/str[@name = 'nextCursorMark']" as="xs:string?"/>
                                    <xsl:if test="$next-cursor-mark">
                                      <xsl:variable name="cache-key" select="concat($start + $rows, '_', $query)" as="xs:string"/>
                                      <xsl:value-of select="wfx:set-cache-value('cursor-mark', $cache-key, $next-cursor-mark, 60, 60)"/>
                                    </xsl:if>
                                    <xsl:apply-templates select="$solr-result/*" mode="output">
                                      <xsl:with-param name="solr-uri" select="$solr-uri" as="xs:string" tunnel="yes"/>
                                    </xsl:apply-templates>
                                  </xsl:otherwise>
                                </xsl:choose>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </resp:body>
    </resp:response>
  </xsl:template>

  <!-- MODE: QUERY -->

  <xsl:template match="triple" mode="query">
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="leftOperand" mode="query"/>
    <xsl:apply-templates select="boolean" mode="query"/>
    <xsl:apply-templates select="rightOperand" mode="query"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <xsl:template match="boolean" mode="query">
    <xsl:value-of select="concat(' ', local:convert-boolean-operator(value), ' ')"/>
  </xsl:template>

  <xsl:template match="leftOperand|rightOperand" mode="query">
    <xsl:apply-templates select="triple|searchClause" mode="query"/>
  </xsl:template>

  <xsl:template match="searchClause[not(normalize-space(term))]" mode="query">
    <!--
    <searchClause>
      <index>identifier</index>
      <relation>
        <value>&lt;&gt;</value>
      </relation>
      <term>pipotheclown</term>
    </searchClause>
    -->
    <xsl:text>-identifier:pipotheclown</xsl:text>
  </xsl:template>

  <!-- Template that can be overriden to process term with collection and field specific logic: -->
  <xsl:template name="process-term" as="xs:string">
    <xsl:param name="field-name" as="xs:string"/>
    <xsl:param name="term" as="xs:string"/>
    <xsl:sequence select="$term"/>
  </xsl:template>

  <xsl:template match="searchClause[normalize-space(term) and (index = $supported-utility-indexes)]" mode="query">
    <xsl:choose>
      <xsl:when test="index = 'cql.allRecords'">*:*</xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="searchClause[normalize-space(term) and not(index = $supported-utility-indexes)]" mode="query">
    <xsl:variable name="relation" select="relation/value" as="xs:string"/>
    <xsl:variable name="index-info" select="local:get-index-info(index, $relation)" as="element()"/>
    <xsl:variable name="trimmed-term" select="local:trim(local:trim-quotes(term,$index-info/type))" as="xs:string?"/>
    <xsl:variable name="field-name" select="$index-info/field" as="xs:string"/>
    <xsl:variable name="term" as="xs:string">
      <xsl:variable name="t" as="xs:string">
        <xsl:choose>
          <xsl:when test="$index-info/type = 'date'">
            <xsl:value-of select="local:convert-date-to-solr($trimmed-term)"/>
          </xsl:when>
          <xsl:when test="$index-info/type = 'boolean'">
            <xsl:value-of select="local:convert-boolean-to-solr($trimmed-term)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="local:escape-term($trimmed-term)"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:call-template name="process-term">
        <xsl:with-param name="field-name" select="$field-name" as="xs:string"/>
        <xsl:with-param name="term" select="$t" as="xs:string"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="criterium" as="xs:string?">
      <xsl:choose>
        <xsl:when test="relation/modifiers/modifier[normalize-space(type) = 'relevant'] and $index-info/edismax">
          <xsl:value-of select="$index-info/edismax"/>
        </xsl:when>
        <!--
        <xsl:when test="$index-info/edismax">
          <xsl:value-of select="concat('_query_:&quot;', $index-info/edismax, $term, '&quot;')"/>
        </xsl:when>
        -->
        <xsl:when test="$relation = 'adj'">
          <xsl:value-of select="concat($field-name, ':&quot;', $term, '&quot;')"/>
        </xsl:when>
        <xsl:when test="$relation = 'all'">
          <xsl:value-of select="concat($field-name, ':', '(', string-join(for $t in tokenize($term, '\s+') return concat('&quot;', $t, '&quot;'), ' AND '), ')')"/>
        </xsl:when>
        <xsl:when test="$relation = 'any'">
          <xsl:value-of select="concat($field-name, ':', '(', string-join(for $t in tokenize($term, '\s+') return concat('&quot;', $t, '&quot;'), ' OR '), ')')"/>
        </xsl:when>
        <xsl:when test="$relation = ('=', '==')">
          <xsl:variable name="clause" as="xs:string">
            <!-- Collection specific template with default implementation: -->
            <xsl:call-template name="clause-for-equals-operator">
              <xsl:with-param name="term" select="$term" as="xs:string"/>
              <xsl:with-param name="relation" select="$relation" as="xs:string"/>
              <xsl:with-param name="index-info" select="$index-info" as="element()"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="$clause"/>
        </xsl:when>
        <!--
        <xsl:when test="$relation = '=='">
          <xsl:value-of select="concat($field-name, ':', '&quot;', $term, '&quot;')"/>
        </xsl:when>
        -->
        <xsl:when test="$relation = '&gt;'">
          <xsl:value-of select="concat($field-name, ':{', $term, ' TO *}')"/>
        </xsl:when>
        <xsl:when test="$relation = '&lt;'">
          <xsl:value-of select="concat($field-name, ':{* TO ', $term, '}')"/>
        </xsl:when>
        <xsl:when test="$relation = '&gt;='">
          <xsl:value-of select="concat($field-name, ':[', $term, ' TO *]')"/>
        </xsl:when>
        <xsl:when test="$relation = '&lt;='">
          <xsl:value-of select="concat($field-name, ':[* TO ', $term, ']')"/>
        </xsl:when>
        <xsl:when test="$relation = '&lt;&gt;'">
          <xsl:value-of select="concat('-', $field-name, ':&quot;', $term, '&quot;')"/>
        </xsl:when>
        <xsl:when test="$relation = 'within'">
          <xsl:variable name="tokens" select="tokenize($term, '\s+')" as="xs:string*"/>
          <xsl:variable name="count" select="count($tokens)" as="xs:integer"/>
          <xsl:choose>
            <xsl:when test="$count = 1 and $tokens[1] = 'vandaag'">
              <xsl:value-of select="concat($field-name, ':[NOW/DAY TO NOW/DAY+1DAY]')"/>
            </xsl:when>
            <xsl:when test="$count = 1 and $tokens[1] = 'afgelopen-week'">
              <xsl:value-of select="concat($field-name, ':[NOW/DAY-7DAYS TO NOW]')"/>
            </xsl:when>
            <xsl:when test="$count = 1 and $tokens[1] = 'afgelopen-maand'">
              <xsl:value-of select="concat($field-name, ':[NOW/DAY-1MONTH TO NOW]')"/>
            </xsl:when>
            <xsl:when test="$count = 1 and $tokens[1] = 'afgelopen-jaar'">
              <xsl:value-of select="concat($field-name, ':[NOW/DAY-1YEAR TO NOW]')"/>
            </xsl:when>
            <xsl:when test="$count = 1 and $tokens[1] = 'ouder-dan-een-jaar'">
              <xsl:value-of select="concat($field-name, ':[* TO NOW-1YEAR]')"/>
            </xsl:when>
            <xsl:when test="$count = 1">
              <xsl:value-of select="concat($field-name, ':[&quot;', local:convert-date-to-solr($tokens[1]), '&quot; TO &quot;', local:convert-date-to-solr($tokens[1]), '&quot;]')"/>
            </xsl:when>
            <xsl:when test="$count ge 2">
              <xsl:value-of select="concat($field-name, ':[&quot;', local:convert-date-to-solr($tokens[1]), '&quot; TO &quot;', local:convert-date-to-solr($tokens[2]), '&quot;]')"/>
            </xsl:when>
            <xsl:otherwise>
              <!-- Does not happen, must be parser error? -->
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$index-info/is-child = 'true'">
        <xsl:value-of select="concat('{!child of=blocktype:', $parent-block-type, ' v=', $apos, $criterium, $apos, '}')"/>
      </xsl:when>
      <xsl:when test="$index-info/is-parent = 'true'">
        <xsl:value-of select="concat('{!parent which=blocktype:', $parent-block-type, ' v=', $apos, $criterium, $apos, '}')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$criterium"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- MODE: OUTPUT -->

  <xsl:template match="response" mode="output">
    <xsl:param name="solr-uri" as="xs:string?" tunnel="yes"/>
    <searchRetrieveResponse>
      <xsl:if test="$config:development-mode and $query-params[@name='debug']/req:value = 'true'">
        <debug>
          <solr-uri>
            <xsl:value-of select="$solr-uri"/>
          </solr-uri>
          <solr-uri-decoded>
            <xsl:value-of select="ext:decode-uri($solr-uri)"/>
          </solr-uri-decoded>
          <solr-response>
            <xsl:sequence select="."/>
          </solr-response>
        </debug>
      </xsl:if>
      <version>
        <xsl:value-of select="$sru-version"/>
      </version>
      <xsl:variable name="num-found" select="xs:integer(result[@name='response']/@numFound)" as="xs:integer"/>
      <numberOfRecords>
        <xsl:value-of select="$num-found"/>
      </numberOfRecords>
      <xsl:variable name="docs" select="result[@name='response']/doc" as="element()*"/>
      <xsl:if test="$docs">
        <records>
          <xsl:apply-templates select="$docs" mode="output"/>
        </records>
      </xsl:if>
      <xsl:variable name="specified-rows" select="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='rows']" as="xs:string?"/>
      <xsl:variable name="rows" select="if ($specified-rows) then xs:integer($specified-rows) else 10" as="xs:integer"/>
      <xsl:variable name="next-record-position" select="$start + $rows + 1" as="xs:integer"/>
      <xsl:if test="$next-record-position &lt; ($num-found+1)">
        <nextRecordPosition>
          <xsl:value-of select="$next-record-position"/>
        </nextRecordPosition>
      </xsl:if>
      <xsl:if test="$facet-template and not($sru-version = '2.0' and $facet-limit = '0')">
        <!-- A version with the extraResponseData wrapper was in use on production so we're always adding it here
             even though SRU version 2.0 does not need this anymore -->
        <extraResponseData>
          <xsl:apply-templates select="$facet-template" mode="facets">
            <xsl:with-param name="response" select="/response" as="element(response)" tunnel="yes"/>
          </xsl:apply-templates>
        </extraResponseData>
      </xsl:if>

      <xsl:call-template name="extra-response-data"/>

    </searchRetrieveResponse>
  </xsl:template>

  <xsl:template match="doc" mode="output">
    <record>
      <recordSchema>http://standaarden.overheid.nl/sru/</recordSchema>
      <recordPacking>xml</recordPacking>
      <recordData>
        <xsl:call-template name="record-data"/>
      </recordData>
      <recordPosition>
        <xsl:value-of select="xs:integer($start) + position()"/>
      </recordPosition>
    </record>
  </xsl:template>

  <xsl:template name="default-filter" as="xs:string?">
    <xsl:param name="xcql" as="document-node()"/>
  </xsl:template>

  <xsl:template name="record-data" />

  <xsl:template name="extra-parameters" as="element(param)*"/>

  <xsl:template name="extra-response-data" as="element(extraResponseData)*"/>

  <xsl:template name="clause-for-equals-operator" as="xs:string?">
    <xsl:param name="term" as="xs:string"/>
    <xsl:param name="relation" as="xs:string"/>
    <xsl:param name="index-info" as="element()"/>
    <xsl:value-of select="concat($index-info/field, ':', $quote, $term, $quote)"/>
  </xsl:template>

  <xsl:template name="get-default-num-records" as="xs:integer">
    <xsl:sequence select="50"/>
  </xsl:template>

  <xsl:template name="facetedResults" as="element()?"/>

  <xsl:template name="rdfEnrichment" as="element()?"/>

  <xsl:template match="*:terms" mode="facets">
    <xsl:param name="response" as="element(response)" tunnel="yes"/>
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="#current"/>
      <xsl:variable name="index" select="ancestor::*:facet/*:index" as="xs:string"/>
      <xsl:variable name="relation" select="ancestor::*:facet/*:relation" as="xs:string?"/>
      <xsl:variable name="field" select="local:get-index-info($index, '==')/facet-field" as="xs:string"/>
      <xsl:variable name="queries" select="local:get-index-info($index, '==')/facet-query" as="element(facet-query)*"/>
      <xsl:choose>
        <xsl:when test="$queries">
          <xsl:apply-templates select="$response/lst[@name='facet_counts']/lst[@name='facet_queries']/int[contains(@name, $field || ':')]" mode="facets-queries">
            <xsl:sort select="@name"/>
            <xsl:with-param name="index" select="$index" as="xs:string"/>
            <xsl:with-param name="relation" select="$relation" as="xs:string?"/>
          </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="$response/lst[@name='facet_counts']/lst[@name='facet_fields']/lst[@name = $field]/int" mode="#current">
            <xsl:sort select="@name"/>
            <xsl:with-param name="index" select="$index" as="xs:string"/>
          </xsl:apply-templates>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="int" mode="facets-queries">
    <xsl:param name="index" as="xs:string"/>
    <xsl:param name="relation" as="xs:string?"/>
    <term>
      <xsl:variable name="term" select="substring-before(substring-after(@name, '{!tag='), '}')" as="xs:string?"/>
      <actualTerm>
        <xsl:value-of select="$term"/>
      </actualTerm>
      <xsl:variable name="query" select="concat($index, ' ', $relation, ' ', $quote, $term, $quote, ' AND ', normalize-space($query-params[@name='query']/req:value))" as="xs:string"/>
      <query>
        <xsl:value-of select="$query"/>
      </query>
      <requestUrl>
        <xsl:value-of select="concat(normalize-space($config:sru-uri),
          '?version=', $sru-version,
          $amp, 'operation=', $query-params[@name='operation']/req:value,
          $amp, 'x-connection=', $query-params[@name='x-connection']/req:value,
          $amp, 'query=', encode-for-uri($query),
          if ($sru-version = '1.2') then concat($amp, 'x-info-1-accept=any') else ())"/>
      </requestUrl>
      <count>
        <xsl:value-of select="."/>
      </count>
    </term>
  </xsl:template>

  <xsl:template match="int" mode="facets">
    <xsl:param name="index" as="xs:string"/>
    <term>
      <actualTerm>
        <xsl:value-of select="@name"/>
      </actualTerm>
      <xsl:variable name="query" select="concat($index, ' = ', $quote, @name, $quote, ' AND ', normalize-space($query-params[@name='query']/req:value))" as="xs:string"/>
      <query>
        <xsl:value-of select="$query"/>
      </query>
      <requestUrl>
        <xsl:value-of select="concat(normalize-space($config:sru-uri),
          '?version=', $sru-version,
          $amp, 'operation=', $query-params[@name='operation']/req:value,
          $amp, 'x-connection=', $query-params[@name='x-connection']/req:value,
          $amp, 'query=', encode-for-uri($query),
          if ($sru-version = '1.2') then concat($amp, 'x-info-1-accept=any') else ())"/>
      </requestUrl>
      <count>
        <xsl:value-of select="."/>
      </count>
    </term>
  </xsl:template>

  <xsl:template match="@*|node()" mode="facets facets-queries facet-12 facet-20">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>

  <xsl:variable name="notInContextOfXslweb"   as="xs:boolean" select="string-length(system-property('xslweb.home'))=0" static="true"/>  
  
  <!-- Dummy functions om in Oxygen alleen de echte fouten te zien -->
  <xsl:function name="ext:parse-cql" use-when="$notInContextOfXslweb">
    <xsl:param as="xs:string" name="param-1"/>
    <xsl:document/>
  </xsl:function>
  
  <xsl:function name="wfx:get-cache-value" use-when="$notInContextOfXslweb">
    <xsl:param as="xs:string" name="param-1"/>
    <xsl:param as="xs:string" name="param-2"/>
    <xsl:value-of select="'abcdefgh'"/>
  </xsl:function>
  
  <xsl:function name="wfx:set-cache-value" use-when="$notInContextOfXslweb">
    <xsl:param as="xs:string" name="param-1"/>
    <xsl:param as="xs:string" name="param-2"/>
    <xsl:param name="param-3"/>
    <xsl:param as="xs:integer" name="param-4"/>
    <xsl:param as="xs:integer" name="param-5"/>
  </xsl:function>

  <xsl:function name="ext:decode-uri" use-when="$notInContextOfXslweb">
    <xsl:param as="xs:string" name="param-1"/>
    <xsl:value-of select="'abcdefgh'"/>
  </xsl:function>
 
  <xsl:function name="log:log" use-when="$notInContextOfXslweb">
    <xsl:param as="xs:string" name="param-1"/>
    <xsl:param as="xs:string" name="param-2"/>
  </xsl:function>
  
  <xsl:function name="wfx:set-attribute" use-when="$notInContextOfXslweb">
    <xsl:param as="xs:string" name="param-1"/>
    <xsl:param as="xs:string" name="param-2"/>
  </xsl:function>
 
  <xsl:function name="wfx:get-attribute" use-when="$notInContextOfXslweb">
    <xsl:param as="xs:string" name="param-1"/>
  </xsl:function>

</xsl:stylesheet>

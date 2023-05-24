<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:map="http://www.w3.org/2005/xpath-functions/map"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:err="http://www.w3.org/2005/xqt-errors"  
  xmlns:sru="http://docs.oasis-open.org/ns/search-ws/sruResponse"
  xmlns:facet="http://docs.oasis-open.org/ns/search-ws/facetedResults"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:search="https://koop.overheid.nl/namespaces/search"
  xmlns:utils="https://koop.overheid.nl/namespaces/utils"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:http="http://expath.org/ns/http-client"
  xmlns:functx="http://www.functx.com"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template name="search:execute-search" as="document-node()">
    <xsl:param name="endpoint" as="xs:string"/>
    <xsl:param name="username" as="xs:string?"/>
    <xsl:param name="password" as="xs:string?"/>
    <xsl:param name="auth-method" as="xs:string?"/>
    <xsl:param name="maximum-records" as="xs:integer?"/><!-- when specified, takes precedence over any &count parameter passed -->
    <xsl:param name="record-schema" as="xs:string?"/>
    <xsl:param name="query-parameters" as="element(req:parameter)*"/>
    <xsl:param name="query-clauses" as="xs:string*"/>
    <xsl:param name="param-name-page" as="xs:string"/>
    <xsl:param name="param-name-pagecount" as="xs:string?"/><!-- the name of the &count parameter (i.e. "count") holding any value 10, 20 or 50 -->
    <xsl:param name="param-name-sort" as="xs:string?"/>
    <xsl:param name="start" as="xs:integer?"/>
    <xsl:param name="index-def" as="element(index)*"/>
    <xsl:param name="sort-def" as="element(sort)*"/>
    <xsl:param name="x-connection" as="xs:string?"/>
    <xsl:param name="sru-path" as="xs:string?"/>
    <xsl:param name="x-params" as="xs:string*"/>
    <xsl:param name="timeout" as="xs:integer?"/>
    
    <xsl:variable name="current-page-param" select="$query-parameters[@name = $param-name-page]/req:value[1]" as="xs:string?"/>
    <xsl:variable name="current-page" select="if ($current-page-param) then xs:integer($current-page-param) else 1" as="xs:integer"/>
    <xsl:variable name="current-pagecount-param" select="$query-parameters[@name = $param-name-pagecount]/req:value[1]" as="xs:string?"/>
    <xsl:variable name="current-pagecount" select="if ($current-pagecount-param) then xs:integer($current-pagecount-param) else 10" as="xs:integer"/>
    
    <xsl:variable name="maximum-records-calc" select="if ($maximum-records) then $maximum-records else $current-pagecount" as="xs:integer"/>
    <xsl:variable name="start-record" select="if ($start) then $start else (($current-page - 1) * $maximum-records-calc) + 1" as="xs:integer"/>
    <xsl:variable name="params" as="xs:string+" select="(
      'operation=searchRetrieve',
      'version=2.0',
      if ($start-record gt 1) then 'startRecord=' || $start-record else (),
      if (exists($maximum-records-calc)) then 'maximumRecords=' || $maximum-records-calc else (),
      if ($record-schema) then 'recordSchema=' || encode-for-uri($record-schema) else (),
      if ($x-connection) then 'x-connection=' || $x-connection else (),
      if ($x-params) then string-join($x-params, '&amp;') else (),
      'query=' || search:generate-query($query-parameters, $query-clauses, $index-def, $sort-def, $param-name-sort),
      search:generate-facet-parameters($index-def)
    )"/>
    
    <xsl:variable name="sru-uri" select="$endpoint || xs:string(if ($sru-path) then $sru-path else '/sru') || '?' || string-join($params, '&amp;')"/>
    
    <xsl:if test="$config:development-mode">
      <xsl:sequence select="if (req:set-attribute('sru-uri', $sru-uri)) then document('http://dummy') else ()"/>
      <xsl:sequence select="if (log:log('INFO', $sru-uri)) then document('http://dummy') else ()"/>
      <xsl:sequence select="if (log:log('INFO', utils:decode-uri(string-join($params, '&amp;')))) then document('http://dummy') else ()"/>
    </xsl:if>
    
    <xsl:try>
      <xsl:variable name="sru-get-request" as="element(http:request)">
        <http:request 
          method="GET" 
          href="{$sru-uri}"
          timeout="{($timeout, 10)[1]}"
          override-media-type="text/xml">
          <xsl:if test="$username and $password and $auth-method">
            <xsl:attribute name="username" select="$username"/>
            <xsl:attribute name="password" select="$password"/>
            <xsl:attribute name="auth-method" select="$auth-method"/>
            <xsl:attribute name="send-authorization">true</xsl:attribute>
          </xsl:if>
          <http:header name="Accept" value="text/xml,application/xml,application/sru+xml"/>
        </http:request>
      </xsl:variable>
      <xsl:variable name="response" select="http:send-request($sru-get-request)"/> 
      <xsl:choose>
        <xsl:when test="$response[1]/xs:integer(@status) ne 200">
          <xsl:document>
            <error status="{$response[1]/@status}">
              <xsl:sequence select="$response[1]"/>
            </error>
          </xsl:document>
        </xsl:when>
        <xsl:otherwise>
          <xsl:sequence select="$response[2]"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:catch>
        <xsl:document>
          <xsl:choose>
            <xsl:when test="local-name-from-QName($err:code) = 'HC006'">
              <xsl:variable name="msg" select="'Timeout communicating with backend server (' || $sru-uri || ')'"/>
              <xsl:document>
                <error status="600">
                  <xsl:value-of select="$msg"/>
                </error>  
              </xsl:document>
              <xsl:sequence select="log:log('ERROR', $msg)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="msg" select="string-join($err:description, ', ') || ' (' || string-join($err:code, ', ') || ', line: ' || string-join($err:line-number, ', ') || ', column: ' || string-join($err:column-number, ', ') || ', url: ' || $sru-uri || ')'" as="xs:string"/>
              <xsl:document>
                <error status="500">
                  <xsl:value-of select="$msg"/>
                </error>  
              </xsl:document>
              <xsl:sequence select="log:log('ERROR', $msg)"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:document>
      </xsl:catch>
    </xsl:try>
  </xsl:template>
  
  <xsl:function name="search:generate-query" as="xs:string">
    <xsl:param name="query-parameters" as="element(req:parameter)*"/>
    <xsl:param name="query-clauses" as="xs:string*"/>
    <xsl:param name="index-def" as="element(index)*"/>
    <xsl:param name="sort-def" as="element(sort)*"/>
    <xsl:param name="param-name-sort" as="xs:string?"/>
    <xsl:variable name="clauses" as="xs:string*">
      <xsl:for-each select="$index-def[index-name]">
        <xsl:variable name="index" select="." as="element(index)"/>
        <xsl:variable name="from-values" select="$query-parameters[@name = $index/form-field]/req:value/text()" as="xs:string*"/>
        <xsl:variable name="to-values" select="$query-parameters[@name = $index/form-field-to]/req:value/text()" as="xs:string*"/>
        <xsl:if test="exists($from-values) or exists($to-values)">
          <xsl:choose>
            <!-- Special cases: -->
            <xsl:when test="operation = 'custom'">
              <xsl:call-template name="get-query-clause">
                <xsl:with-param name="index" select="$index" as="element(index)"/>
                <xsl:with-param name="from-values" select="$from-values" as="xs:string*"/>
                <xsl:with-param name="to-values" select="$to-values" as="xs:string*"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="operation = 'vd_isactive'">
              <xsl:variable name="date-now" select="'&quot;' || substring(xs:string(current-date()), 1, 10) || '&quot;'" as="xs:string"/>
              <xsl:value-of select="'(' || index-name || '_startdatum' || ' &lt;= ' || $date-now || ' AND ' || index-name || '_einddatum' || ' &gt;= ' || $date-now || ')'"/>
            </xsl:when>
            <xsl:when test="operation = 'daterange'">
              <xsl:choose>
                <xsl:when test="empty($from-values)">
                  <xsl:value-of select="'('  || index-name || ' &lt;= ' || local:normalize-date($to-values[1]) || ')'"/>
                </xsl:when>
                <xsl:when test="empty($to-values)">
                  <xsl:value-of select="'(' || index-name || ' &gt;= ' || local:normalize-date($from-values[1]) || ')'"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="'(' || index-name || ' &gt;= ' || local:normalize-date($from-values[1]) || ' AND ' || index-name || ' &lt;= ' || local:normalize-date($to-values[1]) || ')'"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="operator" select="if (multi-value-operator) then ' ' || multi-value-operator || ' ' else ' OR '" as="xs:string"/>
              <xsl:sequence select="'(' || string-join(for $a in $from-values return $index/index-name || ' ' || $index/operation || ' &quot;' || $a || '&quot;', $operator) || ')'"/>
            </xsl:otherwise>
          </xsl:choose> 
        </xsl:if>
      </xsl:for-each>
      
      <!--
      <xsl:for-each select="$query-parameters[@name = $index-def/form-field]">
        <xsl:variable name="index" select="$index-def[form-field = current()/@name]" as="element(index)"/>
        <xsl:variable name="index-name" select="$index/index-name" as="xs:string?"/>
        <xsl:if test="$index-name">
          <xsl:choose>
            <xsl:when test="$index/operation = 'vd_isactive'">
              <xsl:variable name="date-now" select="'&quot;' || xs:string(current-date()) || '&quot;'" as="xs:string"/>
              <xsl:value-of select="'(' || $index/index-name || '_startdatum' || ' &lt;= ' || $date-now || ' AND ' || $index/index-name || '_einddatum' || ' &gt;= ' || $date-now || ')'"/>
            </xsl:when>
            <xsl:when test="$index/operation = 'daterange'">
              <xsl:variable name="from-value" select="req:value[1]" as="xs:string?"/>
              <xsl:variable name="to-value" select="$query-parameters[@name = $index/form-field-to]/req:value[1]" as="xs:string?"/>
              <xsl:choose>
                <xsl:when test="empty($from-value)">
                  <xsl:value-of select="'('  || $index/index-name || ' &lt;= ' || local:normalize-date($to-value) || ')'"/>
                </xsl:when>
                <xsl:when test="empty($to-value)">
                  <xsl:value-of select="'(' || $index/index-name || ' &gt;= ' || local:normalize-date($from-value) || ')'"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="'(' || $index/index-name || ' &gt;= ' || local:normalize-date($from-value) || ' AND ' || $index/index-name || ' &lt;= ' || local:normalize-date($to-value) || ')'"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="operator" select="if ($index/multi-value-operator) then ' ' || $index/multi-value-operator || ' ' else ' OR '" as="xs:string"/>
              <xsl:sequence select="'(' || string-join(for $a in req:value return $index/index-name || ' ' || $index/operation || ' &quot;' || $a || '&quot;', $operator) || ')'"/>
            </xsl:otherwise>
          </xsl:choose> 
        </xsl:if>
      </xsl:for-each>
      -->
      <xsl:sequence select="$query-clauses"/>
    </xsl:variable>
    <xsl:variable name="sort" as="xs:string?">
      <xsl:variable name="sort-def" select="($sort-def[id = $query-parameters[@name = $param-name-sort]/req:value], $sort-def[is-default = 'true'])[1]" as="element(sort)?"/>
      <xsl:if test="$sort-def">
        <xsl:value-of select="'sortBy ' || $sort-def/index-name || '/sort.' || xs:string(if ($sort-def/direction = 'asc') then 'ascending' else 'descending')"/>
      </xsl:if>
    </xsl:variable>
    <xsl:value-of select="
      encode-for-uri(
        string-join(
          (
            if (exists($clauses)) then string-join($clauses, ' AND ') else 'cql.allRecords=1',
            $sort
          ), 
          ' '
        )
      )"/>
  </xsl:function>
  
  <xsl:function name="search:generate-sort" as="xs:string?">
    <xsl:param name="query-parameters" as="element(req:parameter)*"/>
    <xsl:param name="index-def" as="element(index)*"/>
    <xsl:variable name="sort-spec" select="$query-parameters[@name = 'sort']/req:value[1]" as="xs:string?"/>
    <xsl:if test="$sort-spec">
      <xsl:variable name="parts" select="tokenize($sort-spec, ',')" as="xs:string+"/>
      <xsl:variable name="key" select="$index-def[form-field = $parts[1]]/index-name" as="xs:string"/>
      <xsl:variable name="sort" select="if ($parts[2] = 'asc') then ',,0' else ',,1'" as="xs:string"/>
      <xsl:value-of select="'sortKeys=' || $key || $sort"/>
    </xsl:if>
  </xsl:function>
  
  <xsl:function name="search:generate-facet-parameters" as="xs:string?">
    <xsl:param name="index-def" as="element(index)*"/>
    <xsl:variable name="facet-limit" select="string-join(for $f in $index-def/facet return $f/max-query || ':' || $f/../index-name, ',')" as="xs:string"/>
    <xsl:sequence select="if (string-length($facet-limit) gt 0) then 'facetLimit=' || encode-for-uri($facet-limit) else ()"/>
  </xsl:function>
  
  <xsl:function name="search:get-number-of-hits" as="xs:integer">
    <xsl:param name="search-result" as="document-node()"/>
    <xsl:variable name="count" select="$search-result/sru:searchRetrieveResponse/sru:numberOfRecords" as="xs:string?"/>
    <xsl:sequence select="if ($count) then ($count cast as xs:integer) else 0"/>
  </xsl:function>
  
  <!--
  <xsl:template name="search:search-results" as="element(cl:search-results)">
    <xsl:param name="search-result" as="document-node()"/>
    <cl:result-list>
      <xsl:apply-templates select="$search-result/sru:searchRetrieveResponse/sru:records/sru:record"/>  
    </cl:result-list>
  </xsl:template>
  -->
  
  <xsl:template name="get-query-clause" as="xs:string?">
    <xsl:param name="index" as="element(index)"/>
    <xsl:param name="from-values" as="xs:string*"/>
    <xsl:param name="to-values" as="xs:string*"/>
  </xsl:template>
  
  <xsl:template name="facets" as="element(cl:facets)">
    <xsl:param name="header" as="xs:string"/>
    <xsl:param name="index-def" as="element(index)*"/>
    <xsl:param name="search-result" as="document-node()"/>
    <xsl:param name="query-params" as="element(req:parameter)*"/>
    <xsl:param name="base-path" as="xs:string"/>
    <cl:facets>
      <cl:header>
        <xsl:value-of select="$header"/>
      </cl:header>
      <xsl:for-each select="$index-def/facet">
        <xsl:variable 
          name="sru-facet" 
          select="$search-result/sru:searchRetrieveResponse/sru:facetedResults/facet:datasource/facet:facets/facet:facet[facet:index = current()/index-name]" 
          as="element(facet:facet)?"/>
        <xsl:if test="$sru-facet/facet:terms/facet:term">
          <cl:facet>
            <cl:label>
              <xsl:value-of select="label"/>
            </cl:label>
            <xsl:if test="max-show">
              <cl:max-show>
                <xsl:value-of select="max-show"/>
              </cl:max-show>
            </xsl:if> 
            <xsl:for-each select="$sru-facet/facet:terms/facet:term"> 
              <cl:term>
                <cl:label>
                  <xsl:value-of select="facet:actualTerm"/>
                </cl:label>
                <cl:count>
                  <xsl:value-of select="facet:count"/>
                </cl:count>
                <cl:path>
                  <xsl:variable name="form-field" select="$index-def/index[name = $sru-facet/facet:index]/form-field" as="xs:string"/>
                  <xsl:variable name="term" select="facet:actualTerm" as="xs:string"/>
                  <xsl:variable name="params" as="map(xs:string, xs:string)" select="
                    map:merge((
                      map:entry($form-field, $term),
                      map:entry('page', '1')
                    ))"/>
                  <xsl:value-of select="utils:replace-in-url($query-params, $base-path, (), (), $params)"/>
                </cl:path>
              </cl:term>   
            </xsl:for-each>
          </cl:facet>
        </xsl:if>
      </xsl:for-each>
    </cl:facets>
  </xsl:template>
  
  <xsl:function name="local:normalize-date">
    <xsl:param name="date" as="xs:string?"/>
    <xsl:analyze-string select="$date" regex="^(\d{{2}})-(\d{{2}})-(\d{{4}})">
      <xsl:matching-substring>
        <xsl:value-of select="substring(regex-group(3) || '-' || regex-group(2) || '-' || regex-group(1), 1, 10)"/>
      </xsl:matching-substring>
      <xsl:non-matching-substring>
        <xsl:value-of select="substring($date, 1, 10)"/>
      </xsl:non-matching-substring>
    </xsl:analyze-string>
  </xsl:function>
    
<xsl:variable name="notInContextOfXslweb"   as="xs:boolean" select="string-length(system-property('xslweb.home'))=0" static="true"/>  

  <!-- Dummy functions om in Oxygen alleen de echte fouten te zien -->
  <xsl:function name="req:set-attribute" use-when="$notInContextOfXslweb">
    <xsl:param as="xs:string" name="first"/>
    <xsl:param as="xs:string" name="second"/>
    <xsl:value-of select="'abcdefgh'"/>
  </xsl:function>
  
</xsl:stylesheet>
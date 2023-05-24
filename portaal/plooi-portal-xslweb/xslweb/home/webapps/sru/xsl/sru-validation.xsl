<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="2.0">
  
  <xsl:variable name="supported-parameters" select="('operation', 'version', 'query', 'startRecord', 'maximumRecords', 'recordPacking', 'recordSchema', 'resultSetTTL', 'stylesheet', 'extraRequestData', 'x-connection', 'x-info-1-accept', 'x-hltokens', 'solr', 'cql', 'debug')" as="xs:string+"/>
  <xsl:variable name="supported-parameters-20" select="($supported-parameters, 'queryType', 'sortKeys', 'renderedBy', 'httpAccept', 'responseType', 'facetLimit', 'facetSort')" as="xs:string+"/>
  <xsl:variable name="supported-relations" select="('adj', 'all', 'any', '==', '=', '&gt;', '&lt;', '&gt;=', '&lt;=', '&lt;&gt;', 'within')" as="xs:string+"/>
  <xsl:variable name="supported-boolean-operators" select="('and', 'or', 'not')" as="xs:string+"/>
  <xsl:variable name="supported-sort-modifiers" select="('sort.ascending', 'sort.descending')" as="xs:string+"/>
  <xsl:variable name="supported-utility-indexes" select="('cql.allRecords')" as="xs:string+"/>
  <xsl:variable name="supported-date-keywords" select="('vandaag', 'afgelopen-week', 'afgelopen-maand', 'afgelopen-jaar', 'ouder-dan-een-jaar')" as="xs:string+"/>
  
  <xsl:template name="validate-params" as="element()?">
    <xsl:variable name="operation" select="$query-params[@name='operation']/req:value[1]" as="xs:string?"/>
    <xsl:variable name="version" select="$query-params[@name='version']/req:value[1]" as="xs:string?"/>
    <xsl:variable name="query" select="$query-params[@name='query']/req:value[1]" as="xs:string?"/>
    <xsl:variable name="query-type" select="$query-params[@name='queryType']/req:value[1]" as="xs:string?"/>
    <xsl:variable name="record-packing" select="$query-params[@name='recordPacking']/req:value[1]" as="xs:string?"/>
    <xsl:variable name="record-schema" select="$query-params[@name='recordSchema']/req:value[1]" as="xs:string?"/>
    <xsl:variable name="record-xpath" select="$query-params[@name='recordXPath']/req:value[1]" as="xs:string?"/>
    <xsl:variable name="result-ttl" select="$query-params[@name='resultSetTTL']/req:value[1]" as="xs:string?"/>
    <xsl:variable name="stylesheet" select="$query-params[@name='stylesheet']/req:value[1]" as="xs:string?"/>
    <xsl:variable name="unsupported-parameters" select="for $p in $query-params/@name[not(. = $supported-parameters)] return $p" as="xs:string*"/>
    <xsl:variable name="unsupported-parameters-20" select="for $p in $query-params/@name[not(. = $supported-parameters-20)] return $p" as="xs:string*"/>
    
    <diagnostics>      
      <xsl:choose>                        
        <xsl:when test="not($operation)">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/7</xsl:with-param>
            <xsl:with-param name="details">operation</xsl:with-param>
            <xsl:with-param name="message">Mandatory parameter not supplied</xsl:with-param>
          </xsl:call-template>                                   
        </xsl:when>
        <xsl:when test="not($operation = ('searchRetrieve', 'explain', 'cql2solr'))">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/4</xsl:with-param>            
            <xsl:with-param name="message">Unsupported operation</xsl:with-param>  
          </xsl:call-template>                        
        </xsl:when>
        <xsl:when test="not($version)">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/7</xsl:with-param>
            <xsl:with-param name="details">version</xsl:with-param>
            <xsl:with-param name="message">Mandatory parameter not supplied</xsl:with-param>  
          </xsl:call-template>                                    
        </xsl:when>
        <xsl:when test="not($version = ('1.2', '2.0'))">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/5</xsl:with-param>
            <xsl:with-param name="details">1.2,2.0</xsl:with-param>
            <xsl:with-param name="message">Unsupported version</xsl:with-param>              
          </xsl:call-template>            
        </xsl:when>
        <xsl:when test="($operation = ('searchRetrieve', 'cql2solr')) and not($query)">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/7</xsl:with-param>
            <xsl:with-param name="details">query</xsl:with-param>
            <xsl:with-param name="message">Mandatory parameter not supplied</xsl:with-param>  
          </xsl:call-template>                                    
        </xsl:when>
        <xsl:when test="$record-packing and not($record-packing = 'xml')">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/6</xsl:with-param>
            <xsl:with-param name="details">recordPacking</xsl:with-param>
            <xsl:with-param name="message">Unsupported parameter value</xsl:with-param>  
          </xsl:call-template>                                   
        </xsl:when>
        <xsl:when test="$record-packing and not($record-packing = 'xml')">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/6</xsl:with-param>
            <xsl:with-param name="details">recordPacking</xsl:with-param>
            <xsl:with-param name="message">Unsupported parameter value</xsl:with-param>  
          </xsl:call-template>                                    
        </xsl:when>
        <xsl:when test="$record-schema">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/67</xsl:with-param>
            <xsl:with-param name="message">The record schema is known, but this particular record cannot be transformed into it</xsl:with-param>  
          </xsl:call-template>                                   
        </xsl:when>
        <xsl:when test="$record-xpath">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/8</xsl:with-param>
            <xsl:with-param name="details">recordXPath</xsl:with-param>
            <xsl:with-param name="message">Unsupported Parameter</xsl:with-param>  
          </xsl:call-template>                                   
        </xsl:when>
        <xsl:when test="($version = '1.2') and exists($unsupported-parameters)">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/8</xsl:with-param>
            <xsl:with-param name="details">
              <xsl:value-of select="string-join($unsupported-parameters, ', ')"/>
            </xsl:with-param>
            <xsl:with-param name="message">Unsupported Parameter</xsl:with-param>  
          </xsl:call-template>         
        </xsl:when>
        <xsl:when test="($version = '2.0') and exists($unsupported-parameters-20)">
          <xsl:variable name="params" select="for $p in $unsupported-parameters-20[not(starts-with(., 'facetStart') or starts-with(., 'facetCount'))] return $p" as="xs:string*"/>
          <xsl:if test="$params">
            <xsl:call-template name="diagnostics">
              <xsl:with-param name="uri">info:srw/diagnostic/1/8</xsl:with-param>
              <xsl:with-param name="details">
                <xsl:value-of select="string-join($params, ', ')"/>
              </xsl:with-param>
              <xsl:with-param name="message">Unsupported Parameter</xsl:with-param>  
            </xsl:call-template>  
          </xsl:if>
        </xsl:when>
        <xsl:when test="($version = '2.0') and exists($query-type) and (not($query-type = 'cql'))">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/48</xsl:with-param>
            <xsl:with-param name="details">only "queryType=cql" is supported</xsl:with-param>
            <xsl:with-param name="message">Query feature unsupported</xsl:with-param>  
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="exists($stylesheet)">
          <xsl:call-template name="diagnostics">
            <xsl:with-param name="uri">info:srw/diagnostic/1/110</xsl:with-param>
            <xsl:with-param name="details">stylesheets</xsl:with-param>
            <xsl:with-param name="message">Stylesheets not supported</xsl:with-param>  
          </xsl:call-template>
        </xsl:when>
      </xsl:choose>      
    </diagnostics>
  </xsl:template>
  
  <xsl:template name="validate-query" as="element()">
    <xsl:param name="query" as="document-node()"/>
    <diagnostics>
      
      <!-- Check boolean operators: -->
      <xsl:for-each select="$query//triple/boolean/value[not(lower-case(.) = $supported-boolean-operators)]">
        <xsl:call-template name="diagnostics">
          <xsl:with-param name="uri">info:srw/diagnostic/1/37</xsl:with-param>
          <xsl:with-param name="details" select="."/>
          <xsl:with-param name="message">Unsupported boolean operator</xsl:with-param>  
        </xsl:call-template>        
      </xsl:for-each>
      
      <!-- Check search clauses: -->
      <xsl:for-each select="$query//searchClause">
        <xsl:variable name="index-info" select="local:get-index-info(index, relation/value)" as="element()?"/>
        <xsl:variable name="trimmed-term" select="local:trim-quotes(term,$index-info/type)" as="xs:string?"/>        
        <xsl:choose>
          <xsl:when test="not($index-info) and not(index = $supported-utility-indexes)">
            <xsl:call-template name="diagnostics">
              <xsl:with-param name="uri">info:srw/diagnostic/1/16</xsl:with-param>
              <xsl:with-param name="details" select="index"/>
              <xsl:with-param name="message">Unsupported index</xsl:with-param>  
            </xsl:call-template>            
          </xsl:when>
          <xsl:when test="not(relation/value = $supported-relations)">                        
            <xsl:call-template name="diagnostics">
              <xsl:with-param name="uri">info:srw/diagnostic/1/19</xsl:with-param>
              <xsl:with-param name="details" select="relation/value"/>
              <xsl:with-param name="message">Unsupported relation</xsl:with-param>  
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="not(relation/value = $index-info/relation) and not(index = 'cql.allRecords')">                        
            <xsl:call-template name="diagnostics">
              <xsl:with-param name="uri">info:srw/diagnostic/1/22</xsl:with-param>
              <xsl:with-param name="details" select="concat(index, ' ', relation/value)"/>
              <xsl:with-param name="message">Unsupported combination of relation and index</xsl:with-param>  
            </xsl:call-template>            
          </xsl:when>
          <xsl:when test="($index-info/type = 'date') and not(matches($trimmed-term, '\d{4}-\d{1,2}-\d{1,2}') or matches($trimmed-term, '\d{1,2}-\d{1,2}-\d{4}') or $trimmed-term = $supported-date-keywords)">
            <xsl:call-template name="diagnostics">
              <xsl:with-param name="uri">info:srw/diagnostic/1/36</xsl:with-param>
              <xsl:with-param name="details" select="concat(index, ' ', $trimmed-term)"/>
              <xsl:with-param name="message">Term in invalid format for index or relation</xsl:with-param>  
            </xsl:call-template>
          </xsl:when>          
          <xsl:when test="($index-info/type = 'boolean') and not(lower-case($trimmed-term) = ('true', '1', 'ja', 'false', '0', 'nee'))">
            <xsl:call-template name="diagnostics">
              <xsl:with-param name="uri">info:srw/diagnostic/1/36</xsl:with-param>
              <xsl:with-param name="details" select="concat(index, ' ', $trimmed-term)"/>
              <xsl:with-param name="message">Term in invalid format for index or relation</xsl:with-param>  
            </xsl:call-template>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each>
      
      <!-- Check sort modifiers: -->
      <xsl:for-each select="$query//sortKeys/key">
        <xsl:variable name="index-info" select="local:get-index-info(index, '==')" as="element()?"/>
        <xsl:choose>
          <xsl:when test="not($index-info/sortable = 'true')">            
            <xsl:call-template name="diagnostics">
              <xsl:with-param name="uri">info:srw/diagnostic/1/16</xsl:with-param>
              <xsl:with-param name="details" select="index"/>
              <xsl:with-param name="message">Unsupported sort index</xsl:with-param>  
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="modifiers/modifier/type[not(. = $supported-sort-modifiers)]">            
            <xsl:call-template name="diagnostics">
              <xsl:with-param name="uri">info:srw/diagnostic/1/20</xsl:with-param>
              <xsl:with-param name="details" select="."/>
              <xsl:with-param name="message">Unsupported sort modifier</xsl:with-param>  
            </xsl:call-template>
          </xsl:when>
        </xsl:choose>
      </xsl:for-each> 
      <!--
      <xsl:for-each select="$query//relation/modifiers/modifier/type">
        <xsl:call-template name="diagnostics">
          <xsl:with-param name="uri">info:srw/diagnostic/1/20</xsl:with-param>
          <xsl:with-param name="details" select="."/>
          <xsl:with-param name="message">Unsupported relation modifier</xsl:with-param>  
        </xsl:call-template>      
      </xsl:for-each>
      -->
      <xsl:for-each select="$query//relation/modifiers/modifier[not(normalize-space(.) = 'relevant')]">
        <xsl:call-template name="diagnostics">
          <xsl:with-param name="uri">info:srw/diagnostic/1/20</xsl:with-param>
          <xsl:with-param name="details">
            <xsl:value-of select="."/>
          </xsl:with-param>          
          <xsl:with-param name="message">Unsupported relation modifier</xsl:with-param>    
        </xsl:call-template>      
      </xsl:for-each>      
      <xsl:for-each select="$query//prefixes/prefix">
        <xsl:call-template name="diagnostics">
          <xsl:with-param name="uri">info:srw/diagnostic/1/48</xsl:with-param>
          <xsl:with-param name="details">Prefix assignment</xsl:with-param>          
          <xsl:with-param name="message">Query feature unsupported</xsl:with-param>  
        </xsl:call-template>      
      </xsl:for-each>
    </diagnostics>
  </xsl:template>
  
  <xsl:template name="diagnostics">
    <xsl:param name="uri" as="xs:string?"/>
    <xsl:param name="details" as="xs:string?"/>
    <xsl:param name="message" as="xs:string?"/>
    <diagnostic>
      <xsl:if test="$uri">
        <uri>
          <xsl:value-of select="$uri"/>
        </uri>
      </xsl:if>
      <xsl:if test="$details">
        <details>
          <xsl:value-of select="$details"/>  
        </details>       
      </xsl:if>
      <xsl:if test="$message">
        <message>
          <xsl:value-of select="$message"/>  
        </message>        
      </xsl:if>
    </diagnostic>    
  </xsl:template>
  
</xsl:stylesheet>
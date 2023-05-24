<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"  
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:functx="http://www.functx.com" 
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:function name="cl:get-current-page" as="xs:integer">
    <xsl:param name="query-parameters" as="element(req:parameter)*"/>
    <xsl:param name="param-name-page" as="xs:string"/>
    <xsl:variable name="current-page-param" select="$query-parameters[@name = $param-name-page]/req:value[1]" as="xs:string?"/>
    <xsl:sequence select="if ($current-page-param) then xs:integer($current-page-param) else 1"/>  
  </xsl:function>
  
  <xsl:template name="cl:pagination" as="element(cl:pagination)">
    <xsl:param name="query-parameters" as="element(req:parameter)*"/>
    <xsl:param name="base-path" as="xs:string"/>
    <xsl:param name="number-of-hits" as="xs:integer"/>
    <xsl:param name="rows-per-page" as="xs:integer"/>
    <xsl:param name="param-name-page" as="xs:string"/>
    <xsl:variable name="current-page" select="cl:get-current-page($query-parameters, $param-name-page)" as="xs:integer"/>
    <xsl:variable name="max-page" select="ceiling($number-of-hits div $rows-per-page) cast as xs:integer" as="xs:integer"/>
    <cl:pagination>
      <xsl:if test="$current-page gt 1">
        <cl:previous-page>
          <cl:path>
            <xsl:value-of select="cl:get-pagination-path($query-parameters, $base-path, $param-name-page, xs:string($current-page - 1), true())"/>
          </cl:path>
        </cl:previous-page> 
      </xsl:if> 
      <xsl:if test="$current-page ge 3">
        <cl:page>
          <cl:label>1</cl:label>
          <cl:path>
            <xsl:value-of select="cl:get-pagination-path($query-parameters, $base-path, $param-name-page, '1', true())"/>
          </cl:path>
        </cl:page> 
        <xsl:if test="$current-page gt 3">
          <cl:ellipsis/>
        </xsl:if>
      </xsl:if>
      <xsl:for-each select="max((($current-page - 1), 1)) to min((($current-page + 1), $max-page))">
        <xsl:choose>
          <xsl:when test=". = $current-page">
            <cl:active-page>
              <cl:label>
                <xsl:value-of select="$current-page"/>
              </cl:label>
            </cl:active-page>
          </xsl:when>
          <xsl:otherwise>
            <cl:page>
              <cl:label>
                <xsl:value-of select="."/>
              </cl:label>
              <cl:path>
                <xsl:value-of select="cl:get-pagination-path($query-parameters, $base-path, $param-name-page, xs:string(.), true())"/>
              </cl:path>
            </cl:page>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:if test="$current-page le ($max-page - 2)">
        <xsl:if test="$current-page lt ($max-page - 2)">
          <cl:ellipsis/>
        </xsl:if>
        <cl:last-page>
          <cl:label>
            <xsl:value-of select="$max-page"/>
          </cl:label>
          <cl:path>
            <xsl:value-of select="cl:get-pagination-path($query-parameters, $base-path, $param-name-page, xs:string($max-page), true())"/>
          </cl:path>  
        </cl:last-page>
      </xsl:if>
      <xsl:if test="$current-page lt $max-page">
        <cl:next-page>
          <cl:path>
            <xsl:value-of select="cl:get-pagination-path($query-parameters, $base-path, $param-name-page, xs:string($current-page + 1), true())"/>
          </cl:path>  
        </cl:next-page> 
      </xsl:if>
    </cl:pagination>
  </xsl:template>
  
  <xsl:function name="cl:get-pagination-path" as="xs:string">
    <xsl:param name="query-parameters" as="element(req:parameter)*"/>
    <xsl:param name="context-path" as="xs:string"/>
    <xsl:param name="name" as="xs:string"/>
    <xsl:param name="values" as="xs:string*"/>
    <xsl:param name="replace" as="xs:boolean"/>
    <xsl:variable name="parts" as="xs:string*">
      <xsl:for-each select="$query-parameters">
        <xsl:if test="not(@name = $name) or not($replace)">
          <xsl:for-each select="req:value">
            <xsl:sequence select="../@name || '=' || encode-for-uri(.)"/>
          </xsl:for-each>  
        </xsl:if>
      </xsl:for-each>
      <xsl:for-each select="$values">
        <xsl:sequence select="$name || '=' || encode-for-uri(.)"/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:sequence select="$context-path || '?' || string-join($parts, '&amp;')"/>
  </xsl:function>
    
</xsl:stylesheet>
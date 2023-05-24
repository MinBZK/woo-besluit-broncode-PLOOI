<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:map="http://www.w3.org/2005/xpath-functions/map"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:utils="https://koop.overheid.nl/namespaces/utils"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:function name="utils:build-url" as="xs:string">
    <xsl:param name="path" as="xs:string"/>
    <xsl:param name="values" as="element(req:value)*"/>
    <xsl:param name="add-params" as="map(xs:string, xs:string+)?"/>

    <xsl:sequence select="
      let $params := (for $value in $values
          return $value/../@name || '=' || encode-for-uri($value),
            if (exists($add-params))
              then map:for-each($add-params, function($k, $v) { $k || '=' || encode-for-uri($v) })
            else ()),
        $qs := string-join($params, '&amp;')
      return $path || (if ($qs) then '?' || $qs else '')
                    "/>
  </xsl:function>

  <xsl:function name="utils:build-url" as="xs:string">
    <xsl:param name="path" as="xs:string"/>
    <xsl:param name="values" as="element(req:value)*"/>

    <xsl:sequence select="utils:build-url($path, $values, ())"/>
  </xsl:function>

  <xsl:function name="utils:construct-url" as="xs:string">
    <xsl:param name="path" as="xs:string"/>
    <xsl:param name="values" as="element(req:value)*"/>
    <xsl:param name="for-appending" as="xs:boolean"/>
    <xsl:variable name="parts" as="xs:string*">
      <xsl:value-of select="$path"/>
      <xsl:if test="exists($values)">?</xsl:if>
      <xsl:value-of select="string-join(for $v in $values return $v/../@name || '=' || encode-for-uri($v), '&amp;')"/>
      <xsl:if test="$for-appending">
        <xsl:choose>
          <xsl:when test="exists($values)">&amp;</xsl:when>
          <xsl:otherwise>?</xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:variable>
    <xsl:value-of select="string-join($parts, '')"/>
  </xsl:function>

  <xsl:function name="utils:replace-in-url" as="xs:string">
    <xsl:param name="query-parameters" as="element(req:parameter)*"/>
    <xsl:param name="path" as="xs:string?"/>
    <xsl:param name="add-params" as="map(xs:string, xs:string+)?"/>
    <xsl:param name="del-params" as="map(xs:string, xs:string+)?"/>
    <xsl:param name="upd-params" as="map(xs:string, xs:string)?"/>
    <xsl:variable name="parts" as="xs:string*">
      <xsl:for-each select="$query-parameters">
        <xsl:for-each select="req:value">
          <xsl:variable name="must-delete" select="exists($del-params) and map:get($del-params, ../@name) = (xs:string(.), '*')" as="xs:boolean"/>
          <xsl:if test="not($must-delete)">
            <xsl:variable name="update-value" select="if (exists($upd-params)) then map:get($upd-params, ../@name) else ()" as="xs:string?"/>
            <xsl:sequence select="../@name || '=' || encode-for-uri(if ($update-value) then $update-value else .)"/>
          </xsl:if>
        </xsl:for-each>
      </xsl:for-each>
      <xsl:if test="exists($add-params)">
        <xsl:for-each select="map:keys($add-params)">
          <xsl:variable name="key" select="." as="xs:string"/>
          <xsl:for-each select="map:get($add-params, $key)">
            <xsl:sequence select="$key || '=' || encode-for-uri(.)"/>
          </xsl:for-each>
        </xsl:for-each>
      </xsl:if>
    </xsl:variable>
    <xsl:sequence select="$path || '?' || string-join($parts, '&amp;')"/>
  </xsl:function>

  <xsl:function name="utils:format-date" as="xs:string?">
    <xsl:param name="date" as="xs:string?"/>
    <xsl:value-of select="if ($date castable as xs:date) then xs:string(format-date(xs:date($date), '[D01]-[M01]-[Y]')) else ()"/>
  </xsl:function>

  <xsl:function name="utils:format-date-dmy" as="xs:string?">
    <xsl:param name="date" as="xs:string?"/>
    <xsl:value-of select="if ($date castable as xs:date) then substring($date, 9, 2) || '-' || substring($date, 6, 2) || '-' || substring($date, 1, 4) else $date"/>
  </xsl:function>

  <xsl:function name="utils:hex-to-dec" as="xs:integer">
    <xsl:param name="hex" as="xs:string"/>
    <xsl:variable name="length" select="string-length($hex)" as="xs:integer"/>
    <xsl:value-of select="
      if ($length &gt; 0) then
        if ($length &lt; 2) then
          string-length(substring-before('0 1 2 3 4 5 6 7 8 9 AaBbCcDdEeFf',$hex)) idiv 2
        else
          utils:hex-to-dec(substring($hex,1,$length - 1))*16+utils:hex-to-dec(substring($hex,$length))
      else
        0"/>
  </xsl:function>

  <xsl:function name="utils:decode-uri" as="xs:string">
    <xsl:param name="uri-component" as="xs:string"/>
    <xsl:variable name="decoded-component" as="xs:string*">
      <xsl:analyze-string select="$uri-component" regex="%([A-Fa-f0-9]{{2}})">
        <xsl:matching-substring>
          <xsl:value-of select="codepoints-to-string(utils:hex-to-dec(regex-group(1)))"/>
        </xsl:matching-substring>
        <xsl:non-matching-substring>
          <xsl:value-of select="."/>
        </xsl:non-matching-substring>
      </xsl:analyze-string>
    </xsl:variable>
    <xsl:value-of select="string-join($decoded-component,'')"/>
  </xsl:function>

  <!-- Wrapper function, only to be used for static documents. The purpose is to make it easy to identify URIs whose
       document may be cached in xslweb, if a caching facility is needed and gets available.
  -->
  <xsl:function name="utils:static-doc" as="document-node()">
    <xsl:param name="uri" as="xs:anyURI"/>
    <xsl:sequence select="doc($uri)"/>
  </xsl:function>

  <xsl:function name="utils:left-pad" as="xs:string">
    <xsl:param name="string-to-pad" as="xs:string?"/>
    <xsl:param name="length" as="xs:integer"/>
    <xsl:param name="pad-char" as="xs:string"/>
    <xsl:sequence select="string-join((for $i in (1 to $length - string-length($string-to-pad)) return $pad-char, $string-to-pad), '')"/>
  </xsl:function>

  <xsl:function name="utils:month-name-nl" as="xs:string?">
    <xsl:param name="date" as="xs:anyAtomicType?"/>
    <xsl:sequence select="
      ('Januari', 'Februari', 'Maart', 'April', 'Mei', 'Juni',
      'Juli', 'Augustus', 'September', 'Oktober', 'November', 'December')
      [month-from-date(xs:date($date))]"/>
  </xsl:function>

  <xsl:function name="utils:to-iso-date" as="xs:string?">
    <xsl:param name="date" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="matches($date, '\d\d\d\d-\d\d-\d\d')">
        <xsl:value-of select="$date"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat(substring($date, 7, 4), '-', substring($date, 4, 2), '-', substring($date, 1, 2))"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

</xsl:stylesheet>
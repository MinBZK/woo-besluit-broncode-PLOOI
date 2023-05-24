<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:dcterms="http://purl.org/dc/terms/"
  xmlns:plooi="http://standaarden.overheid.nl/plooi/terms/"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:output="http://www.w3.org/2010/xslt-xquery-serialization"
  xmlns:functx="http://www.functx.com"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:mode name="meta" on-no-match="shallow-copy"/>

  <xsl:include href="../../../../common/xsl/lib/functx/functx-1.0.xsl"/>

  <xsl:param name="config:plooi-base-repos-url" as="xs:string"/>
  <xsl:param name="config:plooi-base-frontend-url" as="xs:string"/>

  <xsl:variable name="output-parameters" as="element()">
    <output:serialization-parameters>
      <output:method value="xml"/>
      <output:indent value="no"/>
      <output:omit-xml-declaration value="yes"/>
    </output:serialization-parameters>
  </xsl:variable>

  <xsl:template name="record-data">
    <xsl:variable name="xml-url" select="$config:plooi-base-repos-url || str[@name='repos_url']" as="xs:string"/>
    <xsl:variable name="documenten-xml" select="str[@name='documentenxml']" as="xs:string?"/>
    <xsl:variable name="documenten" select="if (normalize-space($documenten-xml)) then parse-xml($documenten-xml)/* else ()" as="element(plooi:documenten)?"/>
    <gzd
      xmlns="http://standaarden.overheid.nl/sru"
      xmlns:dcterms="http://purl.org/dc/terms/"
      xmlns:overheid="http://standaarden.overheid.nl/owms/terms/"
      xmlns:plooi="http://standaarden.overheid.nl/plooi/terms/">
      <originalData>
        <xsl:apply-templates select="parse-xml(str[@name='metaxml'])/*" mode="meta">
          <xsl:with-param name="xml-url" select="$xml-url" as="xs:string"/>
        </xsl:apply-templates>
      </originalData>
      <enrichedData>
        <publicatieurl type="xml">
          <xsl:value-of select="$xml-url"/>
        </publicatieurl>
        <publicatieurl type="html">
          <xsl:value-of select="$config:plooi-base-frontend-url || '/Details/' || str[@name='workid'] || '/' || int[@name='versie'] || '.html'"/>
        </publicatieurl>
        <xsl:for-each select="$documenten/plooi:document[not(@published='false')]">
          <xsl:variable name="type" select="functx:substring-after-last(plooi:bestandsnaam, '.')" as="xs:string?"/>
          <publicatieurl type="{$type}">
            <xsl:if test="lower-case($type) = '.pdf' and int[@name='npages'][normalize-space()]">
              <xsl:attribute name="pages" select="int[@name='npages']"/>
            </xsl:if>
            <xsl:value-of select="resolve-uri('../' || plooi:manifestatie-label || '/' || plooi:bestandsnaam, $xml-url)"/>
          </publicatieurl>
        </xsl:for-each>
      </enrichedData>
    </gzd>
  </xsl:template>

  <xsl:template match="dcterms:source/text()" mode="meta">
    <xsl:param name="xml-url" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="functx:is-absolute-uri(.)">
        <xsl:value-of select="."/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="resolve-uri(., $xml-url)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
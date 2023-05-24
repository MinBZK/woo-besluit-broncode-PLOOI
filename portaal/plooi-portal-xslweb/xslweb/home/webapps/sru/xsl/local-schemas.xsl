<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:sr12="http://www.loc.gov/zing/srw/"
  xmlns:sr20="http://docs.oasis-open.org/ns/search-ws/sruResponse"
  xmlns:gzd="http://standaarden.overheid.nl/sru"
  xmlns:exp="http://explain.z3950.org/dtd/2.0/"
  xmlns:fc12="http://docs.oasis-open.org/ns/search-ws/sru-facetedResults"
  xmlns:fc20="http://docs.oasis-open.org/ns/search-ws/facetedResults"
  xmlns:dg12="http://www.loc.gov/zing/srw/diagnostic/"
  xmlns:dg20="http://docs.oasis-open.org/ns/search-ws/diagnostic"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  exclude-result-prefixes="#all"
  version="2.0">
  
  <xsl:output indent="yes"/>
  
  <xsl:param name="x-connection" as="xs:string"/>
  
  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="sr12:searchRetrieveResponse/@xsi:schemaLocation|sr12:explainResponse/@xsi:schemaLocation">
    <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="concat('http://www.loc.gov/zing/srw/', ' sru-1.2/srw-types.xsd')"/>
  </xsl:template>
  
  <xsl:template match="sr20:searchRetrieveResponse/@xsi:schemaLocation|sr20:explainResponse/@xsi:schemaLocation|sr20:diagnostics/@xsi:schemaLocation">
    <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="concat('http://docs.oasis-open.org/ns/search-ws/sruResponse', ' sru-2.0/sruResponse.xsd')"/>
  </xsl:template>
  
  <xsl:template match="sr12:explainResponse//exp:explain/@xsi:schemaLocation">
    <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="concat('http://explain.z3950.org/dtd/2.0/', ' sru-1.2/zeerex-2.0.xsd')"/>
  </xsl:template>
  
  <xsl:template match="sr20:explainResponse//exp:explain/@xsi:schemaLocation">
    <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="concat('http://explain.z3950.org/dtd/2.0/', ' sru-2.0/explain.xsd')"/>
  </xsl:template>
  
  <xsl:template match="fc12:facetedResults/@xsi:schemaLocation">
    <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="concat('http://docs.oasis-open.org/ns/search-ws/sru-facetedResults', ' sru-1.2/facetedResults.xsd')"/>
  </xsl:template>
  
  <xsl:template match="fc20:facetedResults/@xsi:schemaLocation">
    <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="concat('http://docs.oasis-open.org/ns/search-ws/facetedResults', ' sru-2.0/facetedResults.xsd')"/>
  </xsl:template>
  
  <xsl:template match="diagnostics/dg12:diagnostic/@xsi:schemaLocation">
    <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="concat('http://www.loc.gov/zing/srw/diagnostic/', ' sru-1.2/diagnostics.xsd')"/>
  </xsl:template>
    
  <xsl:template match="sr20:diagnostics/dg20:diagnostic/@xsi:schemaLocation">
    <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="concat('http://docs.oasis-open.org/ns/search-ws/diagnostic', ' sru-2.0/diagnostic.xsd')"/>
  </xsl:template>
  
  <xsl:template match="gzd:gzd/@xsi:schemaLocation">
    <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="concat('http://standaarden.overheid.nl/sru', ' gzd/', $x-connection, '/gzd.xsd')"/>
  </xsl:template>
  
</xsl:stylesheet>
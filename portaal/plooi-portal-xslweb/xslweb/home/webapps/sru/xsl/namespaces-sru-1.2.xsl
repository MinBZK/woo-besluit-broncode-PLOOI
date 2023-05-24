<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:gzd="http://standaarden.overheid.nl/sru"
  xmlns:exp="http://explain.z3950.org/dtd/2.0/"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  exclude-result-prefixes="#all"
  version="2.0">
  
  <xsl:output indent="yes"/>
  
  <xsl:template match="resp:*">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
    
  <xsl:template match="searchRetrieveResponse">
    <searchRetrieveResponse xmlns="http://www.loc.gov/zing/srw/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://www.loc.gov/zing/srw/ http://www.loc.gov/standards/sru/xmlFiles/srw-types.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://www.loc.gov/zing/srw/'"/>
      </xsl:apply-templates>
    </searchRetrieveResponse>
  </xsl:template>
  
  <xsl:template match="explainResponse">
    <explainResponse xmlns="http://www.loc.gov/zing/srw/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://www.loc.gov/zing/srw/ http://www.loc.gov/standards/sru/xmlFiles/srw-types.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://www.loc.gov/zing/srw/'"/>
      </xsl:apply-templates>
    </explainResponse>
  </xsl:template>
  
  <xsl:template match="diagnostics">
    <diagnostics>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="''"/>
      </xsl:apply-templates>
    </diagnostics>
  </xsl:template>
  
  <xsl:template match="gzd:gzd" mode="ns-change">
    <xsl:copy>
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://standaarden.overheid.nl/sru http://standaarden.overheid.nl/sru/gzd.xsd</xsl:attribute>
      <xsl:apply-templates select="@*|node()" mode="copy"/>  
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="exp:explain" mode="ns-change">
    <xsl:copy>
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://explain.z3950.org/dtd/2.0/ http://standaarden.overheid.nl/sru/zeerex-2.0.xsd</xsl:attribute>
      <xsl:apply-templates select="@*|node()" mode="copy"/>  
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="diagnostic" mode="ns-change">
    <diagnostic xmlns="http://www.loc.gov/zing/srw/diagnostic/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://www.loc.gov/zing/srw/diagnostic/ http://www.loc.gov/standards/sru/xmlFiles/diagnostics.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://www.loc.gov/zing/srw/diagnostic/'"/>
      </xsl:apply-templates>
    </diagnostic>
  </xsl:template>
  
  <xsl:template match="facetedResults" mode="ns-change">
    <facetedResults xmlns="http://docs.oasis-open.org/ns/search-ws/sru-facetedResults">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://docs.oasis-open.org/ns/search-ws/sru-facetedResults http://standaarden.overheid.nl/sru/facetedResults.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://docs.oasis-open.org/ns/search-ws/sru-facetedResults'"/>
      </xsl:apply-templates>
    </facetedResults>
  </xsl:template>
  
  <xsl:template match="highlight" mode="ns-change">
    <highlight xmlns="info:srw/extension/2/highlight-1.0">
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'info:srw/extension/2/highlight-1.0'"/>
      </xsl:apply-templates>
    </highlight>
  </xsl:template>
  
  <xsl:template match="text()|attribute()" mode="ns-change">
    <xsl:copy/>
  </xsl:template>
  
  <xsl:template match="element()" mode="ns-change">
    <xsl:param name="namespace" as="xs:string"/>
    <xsl:element name="{local-name()}" namespace="{$namespace}">
      <xsl:apply-templates select="@*|node()" mode="#current">
        <xsl:with-param name="namespace" select="$namespace"/>
      </xsl:apply-templates>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="@*|node()" mode="#all" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
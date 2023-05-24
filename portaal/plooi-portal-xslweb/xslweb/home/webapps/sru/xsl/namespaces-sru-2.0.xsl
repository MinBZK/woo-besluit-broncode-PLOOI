<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:gzd="http://standaarden.overheid.nl/sru"
  xmlns:exp="http://explain.z3950.org/dtd/2.0/"
  xmlns:diag="http://docs.oasis-open.org/ns/search-ws/diagnostic"
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
    <searchRetrieveResponse xmlns="http://docs.oasis-open.org/ns/search-ws/sruResponse" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://docs.oasis-open.org/ns/search-ws/sruResponse http://docs.oasis-open.org/search-ws/searchRetrieve/v1.0/os/schemas/sruResponse.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://docs.oasis-open.org/ns/search-ws/sruResponse'"/>
      </xsl:apply-templates>
    </searchRetrieveResponse>
  </xsl:template>
  
  <xsl:template match="explainResponse">
    <explainResponse xmlns="http://docs.oasis-open.org/ns/search-ws/sruResponse" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://docs.oasis-open.org/ns/search-ws/sruResponse http://docs.oasis-open.org/search-ws/searchRetrieve/v1.0/os/schemas/sruResponse.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://docs.oasis-open.org/ns/search-ws/sruResponse'"/>
      </xsl:apply-templates>
    </explainResponse>
  </xsl:template>
  
  <xsl:template match="diagnostics">
    <diagnostics xmlns="http://docs.oasis-open.org/ns/search-ws/sruResponse" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://docs.oasis-open.org/ns/search-ws/sruResponse http://docs.oasis-open.org/search-ws/searchRetrieve/v1.0/os/schemas/sruResponse.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://docs.oasis-open.org/ns/search-ws/sruResponse'"/>
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
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://explain.z3950.org/dtd/2.0/ http://docs.oasis-open.org/search-ws/searchRetrieve/v1.0/os/schemas/explain.xsd</xsl:attribute>
      <xsl:apply-templates select="@*|node()" mode="copy"/>  
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="diagnostic" mode="ns-change">
    <diagnostic xmlns="http://docs.oasis-open.org/ns/search-ws/diagnostic">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://docs.oasis-open.org/ns/search-ws/diagnostic http://docs.oasis-open.org/search-ws/searchRetrieve/v1.0/os/schemas/diagnostic.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://docs.oasis-open.org/ns/search-ws/diagnostic'"/>
      </xsl:apply-templates>
    </diagnostic>
  </xsl:template>
  
  <xsl:template match="datasource" mode="ns-change">
    <datasource xmlns="http://docs.oasis-open.org/ns/search-ws/facetedResults">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://docs.oasis-open.org/ns/search-ws/facetedResults http://docs.oasis-open.org/search-ws/searchRetrieve/v1.0/os/schemas/facetedResults.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://docs.oasis-open.org/ns/search-ws/facetedResults'"/>
      </xsl:apply-templates>
    </datasource>
  </xsl:template>
  
  <xsl:template match="facet" mode="ns-change">
    <facet xmlns="http://docs.oasis-open.org/ns/search-ws/facetedResults">
      <xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://docs.oasis-open.org/ns/search-ws/facetedResults http://docs.oasis-open.org/search-ws/searchRetrieve/v1.0/os/schemas/facetedResults.xsd</xsl:attribute>
      <xsl:apply-templates mode="ns-change">
        <xsl:with-param name="namespace" select="'http://docs.oasis-open.org/ns/search-ws/facetedResults'"/>
      </xsl:apply-templates>
    </facet>
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
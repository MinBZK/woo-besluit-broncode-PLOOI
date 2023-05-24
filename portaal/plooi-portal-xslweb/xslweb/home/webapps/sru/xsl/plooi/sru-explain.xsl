<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template name="explain">
    <explainResponse>      
      <version>
        <xsl:value-of select="$sru-version"/>
      </version>
      <record>
        <recordSchema>http://standaarden.overheid.nl/owms/sru</recordSchema>
        <recordPacking>xml</recordPacking>
        <recordData>
          <xsl:variable name="last-update" select="document(concat($config:solr-uri, '/select?q=timestamp:*&amp;sort=timestamp%20desc&amp;fl=timestamp&amp;rows=1'))/response/result[@name='response']/doc/date[@name = 'timestamp']"/>
          <xsl:variable name="aantal" select="document(concat($config:solr-uri, '/select?q=*:*&amp;rows=0'))/response/result/@numFound"/>
          <explain xmlns="http://explain.z3950.org/dtd/2.0/">
            <serverInfo protocol="SRU" version="{$sru-version}" transport="http">
              <host>zoekservice.overheid.nl</host>
              <port>80</port>
              <database numRecs="{$aantal}" lastUpdate="{$last-update}">CVDR Repository</database>
            </serverInfo>
            <databaseInfo>
              <title lang="en" primary="true">Overheid.nl search and retrieval service</title>
              <description lang="nl" primary="true">Gemeenschappelijke zoekdienst van overheid.nl voor actief openbare documenten volgens de Wet Open Overheid (WOO)</description>
              <author>UBR|KOOP</author>
              <contact>servicedesk@koop.overheid.nl</contact>
              <langUsage>Records are in Dutch</langUsage>
              <restrictions>Database is freely available</restrictions>
            </databaseInfo>
            <metaInfo>
              <dateModified>
                <xsl:value-of select="substring($last-update, 1, 10)"/>
              </dateModified>
            </metaInfo>
            <indexInfo>
              <xsl:for-each-group select="$index-info" group-by="substring-before(index, '.')">
                <xsl:variable name="group" select="current-grouping-key()" as="xs:string"/>
                <xsl:if test="normalize-space($group)">
                  <xsl:variable name="ns" select="namespace-uri-for-prefix($group, $index-info[1])"/>
                  <set name="{$group}" identifier="{$ns}">
                    <title>Indexes in the namespace "<xsl:value-of select="$ns"/>"</title>
                  </set>  
                </xsl:if>
                <xsl:for-each select="current-group()">
                  <index search="true" sort="{sortable}">
                    <map>
                      <name>
                        <xsl:if test="normalize-space($group)">
                          <xsl:attribute name="set" select="$group"/>
                        </xsl:if>
                        <xsl:value-of select="if (contains(index, '.')) then substring-after(index, '.') else index"/>
                      </name>
                    </map>
                  </index>
                </xsl:for-each>
              </xsl:for-each-group>              
            </indexInfo>
          </explain>
        </recordData>
      </record>
    </explainResponse>
  </xsl:template>
  
</xsl:stylesheet>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:webapp="http://www.armatiek.com/xslweb/functions/webapp"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:import href="../sru-core.xsl"/>
  
  <xsl:output method="xml" indent="no"/>
    
  <xsl:include href="sru-recorddata.xsl"/>
  <xsl:include href="sru-explain.xsl"/>
  
  <xsl:variable name="result-block-type" select="()" as="xs:string?"/>
  <xsl:variable name="child-block-type" select="()" as="xs:string?"/>
  <xsl:variable name="parent-block-type" select="()" as="xs:string?"/>
  <xsl:variable name="response-fields">id,workid,versie,issued,metaxml,documentenxml,repos_url,npages</xsl:variable>
   
  <xsl:variable name="index-info" as="element()*">
    <!-- Full text search: -->
    <info 
      xmlns:dcterms="http://purl.org/dc/terms/" 
      xmlns:plooi="http://standaarden.overheid.nl/plooi/terms/" 
      xmlns:gzd="http://standaarden.overheid.nl/sru">
      <index>plooi.text</index>
      <field>text</field>
      <type>text</type>      
      <relation>=</relation>      
      <relation>adj</relation>
      <relation>all</relation>
      <relation>any</relation>
      <sortable>false</sortable>
      <edismax>(_query_:"{!edismax qf=$text-qf pf=$text-qf q.alt=*:* mm=100% uf=-* v=$user-query boost=0.0}" AND _query_:"{!func}scale(query($textsq),0,40)" AND _query_:"{!func}recip(div(ms(NOW,issued),1440000),0.05,300,10)")</edismax>
    </info>
    
    <!-- OWMS kern: -->
    <info>
      <index>dcterms.identifier</index>
      <field>identifier</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>
      <sortable>false</sortable>
    </info>
    <info>
      <index>dcterms.title</index>
      <field>title</field>
      <sort-field>title_sort</sort-field>
      <type>text</type>      
      <relation>=</relation> 
      <relation>==</relation>
      <relation>adj</relation>
      <relation>all</relation>
      <relation>any</relation>
      <sortable>true</sortable>
      <edismax>(_query_:"{!edismax qf=$title-qf pf=$title-qf q.alt=*:* uf=-* mm=100% v=$user-query boost=0.0}" AND _query_:"{!func}scale(query($titlesq),0,40)" AND _query_:"{!func}recip(div(ms(NOW, issued),1440000),0.05,300,10)")</edismax>
    </info>
    <info>
      <index>dcterms.type</index>
      <field>type</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
    </info>
    <info>
      <index>dcterms.type.identifier</index>
      <field>type_id</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
    </info>
    <info>
      <index>dcterms.creator</index>
      <field>creator</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>dcterms.creator.identifier</index>
      <field>creator_id</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>dcterms.language</index>
      <field>language</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>
      <sortable>false</sortable>
    </info>
    <info>
      <index>dcterms.modified</index>
      <field>modified</field>
      <type>date</type>
      <relation>=</relation>
      <relation>==</relation>
      <relation>&gt;</relation>
      <relation>&lt;</relation>
      <relation>&gt;=</relation>
      <relation>&lt;=</relation>
      <relation>&lt;&gt;</relation>
      <relation>within</relation>
      <sortable>true</sortable>
    </info>
    
    <!-- OWMS mantel: -->
    <info>
      <index>dcterms.publisher</index>
      <field>creator</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>dcterms.publisher.identifier</index>
      <field>publisher_id</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>dcterms.available.from</index>
      <field>available_start</field>
      <facet-field>available_start</facet-field>
      <facet-query tag="vandaag">[NOW/DAY TO NOW/DAY+1DAY]</facet-query>
      <facet-query tag="afgelopen-week">[NOW/DAY-7DAYS TO NOW]</facet-query>
      <facet-query tag="afgelopen-maand">[NOW/DAY-1MONTH TO NOW]</facet-query>
      <facet-query tag="afgelopen-jaar">[NOW/DAY-1YEAR TO NOW]</facet-query>
      <facet-query tag="ouder-dan-een-jaar">[* TO NOW-1YEAR]</facet-query>
      <type>date</type>
      <relation>=</relation>
      <relation>==</relation>
      <relation>&gt;</relation>
      <relation>&lt;</relation>
      <relation>&gt;=</relation>
      <relation>&lt;=</relation>
      <relation>&lt;&gt;</relation>
      <relation>within</relation>
      <sortable>true</sortable>
    </info>
    <info>
      <index>dcterms.available.until</index>
      <field>available_tot</field>
      <type>date</type>
      <relation>=</relation>
      <relation>==</relation>
      <relation>&gt;</relation>
      <relation>&lt;</relation>
      <relation>&gt;=</relation>
      <relation>&lt;=</relation>
      <relation>&lt;&gt;</relation>
      <relation>within</relation>
      <sortable>true</sortable>
    </info>
    <info>
      <index>dcterms.issued</index>
      <field>issued</field>
      <type>date</type>
      <relation>=</relation>
      <relation>==</relation>
      <relation>&gt;</relation>
      <relation>&lt;</relation>
      <relation>&gt;=</relation>
      <relation>&lt;=</relation>
      <relation>&lt;&gt;</relation>
      <relation>within</relation>
      <sortable>true</sortable>
    </info>
    
    <!-- Plooi specific: -->
    <info>
      <index>plooi.versie</index>
      <field>versie</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>true</sortable>
    </info>
    <info>
      <index>plooi.informatiecategorie</index>
      <field>informatiecategorie</field>
      <facet-field>informatiecategorie</facet-field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>plooi.informatiecategorie.identifier</index>
      <field>informatiecategorie_id</field>
      <facet-field>informatiecategorie_id</facet-field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>plooi.verantwoordelijke</index>
      <field>verantwoordelijke</field>
      <facet-field>verantwoordelijke</facet-field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>plooi.verantwoordelijke.identifier</index>
      <field>verantwoordelijke_id</field>
      <facet-field>verantwoordelijke_id</facet-field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>plooi.topthema</index>
      <field>topthema</field>
      <facet-field>topthema</facet-field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>plooi.topthema.identifier</index>
      <field>topthema_id</field>
      <facet-field>topthema_id</facet-field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>plooi.aanbieder</index>
      <field>aanbieder</field>
      <type>string</type>
      <relation>=</relation>
      <relation>==</relation>      
      <sortable>false</sortable>
    </info>
    <info>
      <index>plooi.indexeerdatum</index>
      <field>timestamp</field>
      <type>date</type>
      <relation>=</relation>
      <relation>==</relation>
      <relation>&gt;</relation>
      <relation>&lt;</relation>
      <relation>&gt;=</relation>
      <relation>&lt;=</relation>
      <relation>&lt;&gt;</relation>
      <relation>within</relation>
      <sortable>false</sortable>
    </info>
    <info>
      <index>plooi.workid</index>
      <field>workid</field>
      <type>string</type>      
      <relation>=</relation>
      <relation>==</relation>
      <sortable>false</sortable>
    </info>
    <info>
      <index>relevance</index>
      <field>NONE</field>
      <sort-field>score</sort-field>
      <type>text</type>      
      <relation>=</relation> 
      <sortable>true</sortable>
    </info>
  </xsl:variable>
  
  <xsl:function name="local:get-index-info" as="element()*">
    <xsl:param name="index" as="xs:string"/>
    <xsl:param name="relation" as="xs:string"/>
    <xsl:variable name="index-lc" select="lower-case($index)" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="$index-lc = ('plooi.text', 'text', 'keywords')">
        <xsl:sequence select="$index-info[index = 'plooi.text']"/>
      </xsl:when> 
      <xsl:when test="$index-lc = ('dcterms.identifier', 'identifier', 'id')">
        <xsl:sequence select="$index-info[index = 'dcterms.identifier']"/>
      </xsl:when>      
      <xsl:when test="$index-lc = ('dcterms.title', 'title')">
        <xsl:sequence select="$index-info[index = 'dcterms.title']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.type', 'type')">
        <xsl:sequence select="$index-info[index = 'dcterms.type']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.type.identifier', 'type.identifier')">
        <xsl:sequence select="$index-info[index = 'dcterms.type.identifier']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.creator', 'creator')">
        <xsl:sequence select="$index-info[index = 'dcterms.creator']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.creator.identifier', 'creator.identifier')">
        <xsl:sequence select="$index-info[index = 'dcterms.creator.identifier']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.language', 'language')">
        <xsl:sequence select="$index-info[index = 'dcterms.language']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.modified', 'modified')">
        <xsl:sequence select="$index-info[index = 'dcterms.modified']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.modified', 'modified')">
        <xsl:sequence select="$index-info[index = 'dcterms.modified']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.publisher', 'publisher')">
        <xsl:sequence select="$index-info[index = 'dcterms.publisher']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.publisher.identifier', 'publisher.identifier')">
        <xsl:sequence select="$index-info[index = 'dcterms.publisher.identifier']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.available.from', 'available.from')">
        <xsl:sequence select="$index-info[index = 'dcterms.available.from']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.available.tot', 'available.tot')">
        <xsl:sequence select="$index-info[index = 'dcterms.available.tot']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('dcterms.issued', 'issued')">
        <xsl:sequence select="$index-info[index = 'dcterms.issued']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('plooi.versie', 'versie')">
        <xsl:sequence select="$index-info[index = 'plooi.versie']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('plooi.informatiecategorie', 'informatiecategorie')">
        <xsl:sequence select="$index-info[index = 'plooi.informatiecategorie']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('plooi.informatiecategorie.identifier', 'informatiecategorie.identifier')">
        <xsl:sequence select="$index-info[index = 'plooi.informatiecategorie.identifier']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('plooi.verantwoordelijke', 'verantwoordelijke')">
        <xsl:sequence select="$index-info[index = 'plooi.verantwoordelijke']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('plooi.verantwoordelijke.identifier', 'verantwoordelijke.identifier')">
        <xsl:sequence select="$index-info[index = 'plooi.verantwoordelijke.identifier']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('plooi.topthema', 'topthema')">
        <xsl:sequence select="$index-info[index = 'plooi.topthema']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('plooi.topthema.identifier', 'topthema.identifier')">
        <xsl:sequence select="$index-info[index = 'plooi.topthema.identifier']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('plooi.aanbieder', 'aanbieder')">
        <xsl:sequence select="$index-info[index = 'plooi.aanbieder']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('plooi.workid', 'workid')">
        <xsl:sequence select="$index-info[index = 'plooi.workid']"/>
      </xsl:when>
      <xsl:when test="$index-lc = ('relevance')">
        <xsl:sequence select="$index-info[index = 'relevance']"/>
      </xsl:when>
    </xsl:choose>
  </xsl:function>
  
  <xsl:template name="extra-parameters" as="element(param)*">
    <param>
      <name>text-qf</name>
      <value>title^5.0 title_stemmed^5.0 title_ngram^4.0 description^4.0 description_stemmed^4.0 description_ngram^4.0 metadata^3.0 text^3.0 text_stemmed^3.0 text_ngram^3.0</value>
    </param>
    <param>
      <name>title-qf</name>
      <value>title title_stemmed title_ngram</value>
    </param>
    <param>
      <name>textsq</name>
      <value>{!edismax v=$user-query qf=$text-qf pf=$text-qf q.alt=*:* ps=2 qs=2 uf=-* mm=100%}</value>
    </param>
    <param>
      <name>titlesq</name>
      <value>{!edismax v=$user-query qf=$title-qf pf=$title-qf q.alt=*:* ps=2 qs=2 uf=-* mm=100%}</value>
    </param>
    <xsl:if test="$query-params[@name='x-hltokens']/req:value[1] = 'true'">
      <param>
        <name>hl</name>
        <value>on</value>
      </param>
      <param>
        <name>hl.method</name>
        <value>unified</value>
      </param>
      <param>
        <name>hl.simple.pre</name>
        <value>[[</value>
      </param>
      <param>
        <name>hl.simple.post</name>
        <value>]]</value>
      </param>
      <param>
        <name>hl.usePhraseHighlighter</name>
        <value>true</value>
      </param>
      <param>
        <name>hl.fl</name>
        <value>text,text_stemmed,text_ngram</value>
      </param>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="extra-response-data" as="element(extraResponseData)*">
    <xsl:if test="$query-params[@name='x-hltokens']/req:value[1] = 'true'">
      <extraResponseData>
        <xsl:for-each select="root()/response/lst[@name='highlighting']/lst">
          <highlight>
            <tokens id="{@name}">
              <xsl:variable name="highlights" as="xs:string*">
                <xsl:for-each select="arr/str">
                  <xsl:variable name="text" select="replace(., '\]\]\s+\[\[', ' ')" as="xs:string?"/>
                  <xsl:analyze-string select="$text" regex="\[\[(.*?)\]\]">
                    <xsl:matching-substring>
                      <xsl:value-of select="regex-group(1)"/>
                    </xsl:matching-substring>
                  </xsl:analyze-string>
                </xsl:for-each>  
              </xsl:variable>
              <xsl:for-each select="distinct-values($highlights)">
                <token>
                  <xsl:value-of select="."/>
                </token>
              </xsl:for-each>  
            </tokens>  
          </highlight>
        </xsl:for-each>
      </extraResponseData>
    </xsl:if>
  </xsl:template>
  

  <xsl:template name="default-filter" as="xs:string?">
    <xsl:param name="xcql" as="document-node()"/>
    <xsl:text>fq=((*:* -zichtbaarheidsdatumtijd:[* TO *]) OR (zichtbaarheidsdatumtijd:[* TO NOW])) AND ((*:* -publicatiestatus:*) OR publicatiestatus:"gepubliceerd")</xsl:text>
  </xsl:template>

  
  <!--
  <xsl:template name="query-type" as="xs:string">
    <xsl:param name="xcql" as="document-node()"/>
    legacy, edismax, fq 
  </xsl:template>
  -->
  
  <!--
  <xsl:template name="default-filter" as="xs:string?">    
    <xsl:param name="xcql" as="document-node()"/>
    <xsl:if test="not($xcql//index[contains(., 'is-laatste-versie')])">
      <xsl:text>+is-laatste-versie:true</xsl:text>  
    </xsl:if>        
  </xsl:template>
  -->
  
  <!-- Override for default interpretation of = and == operator: -->
  <!--
  <xsl:template name="clause-for-equals-operator" as="xs:string?">
    <xsl:param name="term" as="xs:string"/>
    <xsl:param name="relation" as="xs:string"/>
    <xsl:param name="index-info" as="element()"/>
  </xsl:template>
  -->
  
  <xsl:template name="get-default-num-records" as="xs:integer">
    <xsl:sequence select="10"/>
  </xsl:template>
  
  <xsl:template name="facetedResults">
    <facetedResults>
      <datasource>
        <datasourceDisplayLabel>Woo documenten</datasourceDisplayLabel>
        <datasourceDescription>Actief openbaar gemaakte documenten volgens Wet open overheid (Woo).</datasourceDescription>
        <baseURL>
          <xsl:value-of select="$config:sru-uri"/>
        </baseURL>
        <facets>
          <facet>
            <facetDisplayLabel>Informatiecategorie</facetDisplayLabel>
            <facetDescription>Informatiecategorie</facetDescription>
            <index>plooi.informatiecategorie.identifier</index>
            <relation>=</relation>
            <terms/>
          </facet>
          <facet>
            <facetDisplayLabel>Verantwoordelijke</facetDisplayLabel>
            <facetDescription>Verantwoordelijke</facetDescription>
            <index>plooi.verantwoordelijke.identifier</index>
            <relation>=</relation>
            <terms/>
          </facet>
          <facet>
            <facetDisplayLabel>Topthema</facetDisplayLabel>
            <facetDescription>Topthema</facetDescription>
            <index>plooi.topthema.identifier</index>
            <relation>=</relation>
            <terms/>
          </facet>
          <facet>
            <facetDisplayLabel>Datum beschikbaar</facetDisplayLabel>
            <facetDescription>Datum beschikbaar</facetDescription>
            <index>dcterms.available.from</index>
            <relation>within</relation>
            <terms/>
          </facet>
        </facets>
      </datasource>
    </facetedResults>
  </xsl:template>
  
  <xsl:template name="rdfEnrichment"/>
  
</xsl:stylesheet>

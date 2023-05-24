<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:idx="https://koop.overheid.nl/namespaces/index"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:variable name="index-def" as="element(index)*">
    <index>
      <id>text</id>
      <index-name>plooi.text</index-name>
      <form-field>text</form-field>
      <operation>custom</operation>
      <label-id>Tekst</label-id>
    </index>
    <index>
      <id>datumbeschikbaarvanaf</id>
      <index-name>dcterms.available.from</index-name>
      <form-field>datumBeschikbaarVanaf</form-field>
      <form-field-to>datumBeschikbaarTot</form-field-to>
      <operation>daterange</operation>
      <label-id>Datum beschikbaar vanaf</label-id>
      <facet>
        <max-query>1000</max-query>
      </facet>
    </index>
    <index>
      <id>datumbeschikbaartot</id>
      <form-field>datumBeschikbaarTot</form-field>
      <operation>=</operation>
      <label-id>Datum beschikbaar tot</label-id>
    </index>
    <index>
      <id>datumrange</id>
      <index-name>none</index-name>
      <form-field>datumrange</form-field>
      <operation>custom</operation>
      <label-id>Datum beschikbaar</label-id>
    </index>
    <index>
      <id>informatiesoort</id>
      <index-name>plooi.informatiecategorie.identifier</index-name>
      <form-field>informatiesoort</form-field>
      <operation>==</operation>
      <label-id>Documentsoort</label-id>
      <multi-value-operator>OR</multi-value-operator>
      <is-uri>true</is-uri>
      <facet>
        <max-query>1000</max-query>
      </facet>
    </index>
    <index>
      <id>thema</id>
      <index-name>plooi.topthema.identifier</index-name>
      <form-field>thema</form-field>
      <operation>==</operation>
      <label-id>Thema</label-id>
      <multi-value-operator>OR</multi-value-operator>
      <is-uri>true</is-uri>
      <facet>
        <max-query>1000</max-query>
      </facet>
    </index>
    <index>
      <id>organisatie</id>
      <index-name>plooi.verantwoordelijke.identifier</index-name>
      <form-field>organisatie</form-field>
      <operation>==</operation>
      <label-id>Organisatie</label-id>
      <multi-value-operator>OR</multi-value-operator>
      <is-uri>true</is-uri>
      <facet>
        <max-query>1000</max-query>
      </facet>
    </index>
    <index>
      <id>relevance</id>
      <index-name>relevance</index-name>
    </index>
  </xsl:variable>

  <xsl:variable name="sort-def" as="element(sort)*">
    <sort>
      <id>relevance-desc</id>
      <index-name>relevance</index-name>
      <direction>desc</direction>
      <is-default>true</is-default>
    </sort>
    <sort>
      <id>date-asc</id>
      <index-name>dcterms.available.from</index-name>
      <direction>asc</direction>
    </sort>
    <sort>
      <id>date-desc</id>
      <index-name>dcterms.available.from</index-name>
      <direction>desc</direction>
      <!--
      <is-default>true</is-default>
      -->
    </sort>
    <sort>
      <id>title-asc</id>
      <index-name>dcterms.title</index-name>
      <direction>asc</direction>
    </sort>
    <sort>
      <id>title-desc</id>
      <index-name>dcterms.title</index-name>
      <direction>desc</direction>
    </sort>
  </xsl:variable>

  <xsl:function name="idx:get-index" as="element(index)">
    <xsl:param name="id" as="xs:string"/>
    <xsl:sequence select="$index-def[id = $id]"/>
  </xsl:function>

  <xsl:function name="idx:get-index-name" as="xs:string">
    <xsl:param name="id" as="xs:string"/>
    <xsl:value-of select="idx:get-index($id)/index-name"/>
  </xsl:function>

  <xsl:function name="idx:get-form-field" as="xs:string">
    <xsl:param name="id" as="xs:string"/>
    <xsl:value-of select="idx:get-index($id)/form-field"/>
  </xsl:function>

  <xsl:function name="idx:get-operation" as="xs:string">
    <xsl:param name="id" as="xs:string"/>
    <xsl:value-of select="idx:get-index($id)/operation"/>
  </xsl:function>

</xsl:stylesheet>
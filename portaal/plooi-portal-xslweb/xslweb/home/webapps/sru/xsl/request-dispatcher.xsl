<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"  
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:pipeline="http://www.armatiek.com/xslweb/pipeline"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  exclude-result-prefixes="#all"
  version="2.0">
  
  <xsl:param name="config:development-mode" as="xs:boolean"/>
  <xsl:param name="config:bwb-solr-uri" as="xs:string"/>
  <xsl:param name="config:bwb-base-repos-url" as="xs:string"/>
  <xsl:param name="config:bwb-max-records" as="xs:integer"/>
  <xsl:param name="config:bwbtt-solr-uri" as="xs:string"/>
  <xsl:param name="config:bwbtt-base-repos-url" as="xs:string"/>
  <xsl:param name="config:bwbtt-max-records" as="xs:integer"/>
  <xsl:param name="config:er-solr-uri" as="xs:string"/>
  <xsl:param name="config:er-max-records" as="xs:integer"/>
  <xsl:param name="config:plooi-solr-uri" as="xs:string"/>
  <xsl:param name="config:plooi-max-records" as="xs:integer"/>
  <xsl:param name="config:wk-solr-uri" as="xs:string"/>
  <xsl:param name="config:wk-max-records" as="xs:integer"/>
  <xsl:param name="config:cvdr-solr-uri" as="xs:string"/>
  <xsl:param name="config:cvdr-max-records" as="xs:integer"/>
  <xsl:param name="config:cvdr2020-solr-uri" as="xs:string"/>
  <xsl:param name="config:cvdr2020-max-records" as="xs:integer"/>
  <xsl:param name="config:oo-solr-uri" as="xs:string"/>
  <xsl:param name="config:oo-max-records" as="xs:integer"/>
  <xsl:param name="config:pod-solr-uri" as="xs:string"/>
  <xsl:param name="config:pod-max-records" as="xs:integer"/>
  <xsl:param name="config:repos-solr-uri" as="xs:string"/>
  <xsl:param name="config:repos-max-records" as="xs:integer"/>
  
  <xsl:template match="/">
    <pipeline:pipeline>
      <xsl:apply-templates/>
    </pipeline:pipeline>
  </xsl:template>
  
  <xsl:template match="/req:request[req:path = '/Search']">    
    <xsl:variable name="x-connection" select="lower-case(req:parameters/req:parameter[@name = 'x-connection']/req:value[1])" as="xs:string?"/>
    <xsl:variable name="version" select="lower-case(req:parameters/req:parameter[@name = 'version']/req:value[1])" as="xs:string?"/>
    <xsl:choose>
      <xsl:when test="$x-connection = 'bwb'">
        <pipeline:transformer name="sru" xsl-path="bwb/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:bwb-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="bwb-base-repos-url" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:bwb-base-repos-url"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:bwb-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      <xsl:when test="$x-connection = 'bwbtt'">
        <pipeline:transformer name="sru" xsl-path="bwb/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:bwbtt-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="bwb-base-repos-url" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:bwb-base-repos-url"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="bwbtt-base-repos-url" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:bwbtt-base-repos-url"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:bwbtt-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      
      <xsl:when test="$x-connection = 'plooi'">
        <pipeline:transformer name="sru" xsl-path="plooi/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:plooi-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:plooi-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      
      <xsl:when test="$x-connection = 'eur'">
        <pipeline:transformer name="sru" xsl-path="eur/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:er-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:er-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      <xsl:when test="$x-connection = 'wgk' and req:method = 'POST'">
        <pipeline:transformer name="sru" xsl-path="redirect.xsl"/>
      </xsl:when>
      <xsl:when test="$x-connection = 'wgk'">
        <pipeline:transformer name="sru" xsl-path="wgk/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:wk-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:wk-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      <xsl:when test="$x-connection = 'cvdr'">
        <pipeline:transformer name="sru" xsl-path="cvdr/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:cvdr-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:cvdr-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      <xsl:when test="$x-connection = 'cvdr2020'">
        <pipeline:transformer name="sru" xsl-path="cvdr2020/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:cvdr2020-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:cvdr2020-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      <xsl:when test="$x-connection = 'oo' and req:method = 'POST'">
        <pipeline:transformer name="sru" xsl-path="redirect.xsl"/>
      </xsl:when>
      <xsl:when test="$x-connection = 'oo'">
        <pipeline:transformer name="sru" xsl-path="oo/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:oo-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:oo-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      <xsl:when test="$x-connection = 'pod'">
        <pipeline:transformer name="sru" xsl-path="pod/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:pod-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:pod-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      <xsl:when test="$x-connection = 'repos'">
        <pipeline:transformer name="sru" xsl-path="repos/sru.xsl">
          <pipeline:parameter name="solr-uri" uri="http://www.armatiek.com/xslweb/configuration" type="xs:string">
            <pipeline:value>
              <xsl:value-of select="$config:repos-solr-uri"/>
            </pipeline:value>
          </pipeline:parameter>
          <pipeline:parameter name="max-records" uri="http://www.armatiek.com/xslweb/configuration" type="xs:integer">
            <pipeline:value>
              <xsl:value-of select="$config:repos-max-records"/>
            </pipeline:value>
          </pipeline:parameter>
        </pipeline:transformer>
      </xsl:when>
      <xsl:otherwise>
        <pipeline:transformer name="sru-x-connection-error" xsl-path="sru-x-connection-error.xsl"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:choose>
      <xsl:when test="$version = '1.2'">
        <pipeline:transformer name="namespaces-sru-1.2" xsl-path="namespaces-sru-1.2.xsl"/>
      </xsl:when>
      <xsl:when test="$version = '2.0'">
        <pipeline:transformer name="namespaces-sru-2.0" xsl-path="namespaces-sru-2.0.xsl"/>
      </xsl:when>
    </xsl:choose>
    <xsl:if test="$config:development-mode">
      <pipeline:transformer name="local-schemas" xsl-path="local-schemas.xsl">
        <pipeline:parameter name="x-connection" type="xs:string">
          <pipeline:value>
            <xsl:value-of select="$x-connection"/>
          </pipeline:value>
        </pipeline:parameter>
      </pipeline:transformer>
    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
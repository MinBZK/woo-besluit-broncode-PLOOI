<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"  
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:webapp="http://www.armatiek.com/xslweb/functions/webapp"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:i18n="https://koop.overheid.nl/namespaces/i18n"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:param name="config:development-mode" as="xs:boolean"/>
  
  <!--
  <xsl:variable name="i18n-initialized" select="webapp:get-attribute('i18n-initialized')" as="xs:boolean?"/>
  -->
  
  <!-- Next template must be overriden in importing stylesheet: -->
  <!--
  <xsl:template name="i18n:tmx-doc" as="document-node()">
    <xsl:document/>
  </xsl:template>  
   
  <xsl:template name="i18n:i18n-initialize">
    <xsl:if test="$config:development-mode or (not($i18n-initialized = true()))">
      <xsl:sequence select="log:log('INFO', 'Loading TMX file ...')"/>
      <xsl:variable name="tmx-doc" as="document-node()">
        <xsl:call-template name="i18n:tmx-doc"/>
      </xsl:variable>
      <xsl:sequence select="
        (for $tuv in $tmx-doc/tmx/body/tu/tuv return webapp:set-attribute($tuv/@xml:lang || '-' || $tuv/../@tuid, $tuv/seg/node()), 
         webapp:set-attribute('i18n-initialized', true()))"/>
    </xsl:if>
  </xsl:template>
  -->
  
  <xsl:template match="i18n:translation">
    <!--
    <xsl:call-template name="i18n:i18n-initialize"/>
    -->
    <xsl:variable name="value" select="webapp:get-attribute(@lang || '-' || @id)" as="node()*"/>
    <xsl:sequence select="if ($value) then $value else '-- i18n value not specified --'"/>
  </xsl:template>

  <xsl:function name="i18n:trans">
    <xsl:param name="lang" as="xs:string"/>
    <xsl:param name="id" as="xs:string"/>
    <!--
    <xsl:call-template name="i18n:i18n-initialize"/>
    -->
    <xsl:variable name="value" select="webapp:get-attribute($lang || '-' || $id)" as="node()*"/>
    <xsl:sequence select="if ($value) then $value else '-- i18n value not specified --'"/>
  </xsl:function>
  
</xsl:stylesheet>
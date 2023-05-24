<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:map="http://www.w3.org/2005/xpath-functions/map"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:functx="http://www.functx.com"
  xmlns:local="urn:local"
  exclude-result-prefixes="#all"
  version="3.0">
    
  <xsl:mode on-no-match="shallow-copy"/>
  <xsl:mode name="xss-encode" on-no-match="shallow-copy"/>
  
  <xsl:variable name="event-names" as="map(xs:string, xs:string)">
    <xsl:map>
      <xsl:map-entry key="'onload'" select="''"/>	      
      <xsl:map-entry key="'onunload'" select="''"/>	
      <xsl:map-entry key="'onchange'" select="''"/>	
      <xsl:map-entry key="'onfocus'" select="''"/>	      
      <xsl:map-entry key="'oninput'" select="''"/>
      <xsl:map-entry key="'onselect'" select="''"/>
      <xsl:map-entry key="'onsubmit'" select="''"/>
      <xsl:map-entry key="'onkeydown'" select="''"/>
      <xsl:map-entry key="'onkeypress'" select="''"/>
      <xsl:map-entry key="'onkeyup'" select="''"/>
      <xsl:map-entry key="'onclick'" select="''"/>
      <xsl:map-entry key="'ondblclick'" select="''"/>
    </xsl:map>
  </xsl:variable>
   
  <xsl:template match="body">
    <!-- Start encoding -->
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:call-template name="start-encode"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="xss-encode">
    <xsl:call-template name="start-encode"/>
  </xsl:template>
  
  <xsl:template name="start-encode">
    <xsl:choose>
      <xsl:when test="req:get-attribute('xslweb.xssfiltering')">
        <!-- Start encoding -->
        <xsl:apply-templates select="@*|node()" mode="xss-encode"/>  
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="@*|node()"/> 
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="xss-no-encode" mode="#all">
    <!-- Stop encoding -->
    <xsl:apply-templates mode="#default"/>
  </xsl:template>
  
  <!-- Leave Javascript untouched, unsafe data within javascript block and attributes must already be encoded in webapp specific stylesheets: -->
  <xsl:template match="script|@*[map:contains($event-names, lower-case(local-name()))]" priority="2" mode="xss-encode">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="text()" mode="xss-encode">
    <xsl:value-of select="'[[%ht' || . || '%]]'"/>
  </xsl:template>
  
  <xsl:template match="@*[not(local-name() = ('href','src','action')) and not(starts-with(local-name(), 'data-'))]" priority="2" mode="xss-encode"> <!-- Note: unsafe data in href attributes is already url encoded -->
    <xsl:attribute name="{local-name()}">
      <xsl:value-of select="'[[%ht' || . || '%]]'"/>
    </xsl:attribute>
  </xsl:template>
  
  <!--
  <xsl:template match="@href|@src|@action" priority="2" mode="xss-encode">
    <xsl:attribute name="{local-name()}">
      <xsl:value-of select="'[[%ur' || . || '%]]'"/>
    </xsl:attribute>
  </xsl:template>
  -->
 
</xsl:stylesheet>
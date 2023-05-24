<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"  
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:i18n="https://koop.overheid.nl/namespaces/i18n"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:import href="page.xsl"/>
  
  <xsl:param name="config:error-code" as="xs:string"/>
     
  <xsl:template name="title">Platform open overheidsinformatie</xsl:template>
  
  <xsl:template name="status" as="element(status)">
    <xsl:choose>
      <xsl:when test="$config:error-code = '404'">
        <status code="404" message="Page not found"/>        
      </xsl:when>
      <xsl:otherwise>
        <status code="200" message=""/> 
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:function name="cl:get-selected-nav-id" as="xs:string">
    <xsl:text>home</xsl:text>
  </xsl:function>
  
  <xsl:template name="breadcrumb" as="element(cl:breadcrumb)">
    <cl:breadcrumb>
      <cl:title>U bent hier:</cl:title>
      <cl:level>
        <cl:path>
          <xsl:value-of select="$context-path"/>
        </cl:path>
        <cl:label>Home</cl:label>
      </cl:level>
    </cl:breadcrumb>
  </xsl:template>
  
  <xsl:template name="main">
    <div class="container row">
      <div class="columns columns--sidebar-left">
        <div class="column">
          <xsl:choose>
            <xsl:when test="$config:error-code = '404'">
              <h2>Pagina niet gevonden</h2>   
            </xsl:when>
          </xsl:choose>
        </div>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>

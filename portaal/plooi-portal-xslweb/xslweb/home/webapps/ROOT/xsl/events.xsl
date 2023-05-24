<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:webapp="http://www.armatiek.com/xslweb/functions/webapp"
  xmlns:event="http://www.armatiek.com/xslweb/event" 
  xmlns:dlogger="http://www.armatiek.nl/xslweb/functions/dlogger"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:import href="../../cb-common/xsl/common/dlogger.xsl"/>
  
  <xsl:template match="event:webapp-open">
    <xsl:sequence select="dlogger:init()"/>
  </xsl:template>
  
  <xsl:template match="event:webapp-close"/>
    
  <xsl:template match="event:webapp-reload"/>

</xsl:stylesheet>
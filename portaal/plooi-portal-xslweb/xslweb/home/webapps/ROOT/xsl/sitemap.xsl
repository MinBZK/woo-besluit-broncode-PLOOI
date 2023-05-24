<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet   
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"    
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:res="http://www.armatiek.com/xslweb/resource-serializer"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="/">
    <resp:response status="200">
      <resp:body>
        <res:resource-serializer path="webapps/plooi/static/sitemap.xml" content-type="application/xml"/>
      </resp:body>
    </resp:response>          
  </xsl:template>
  
</xsl:stylesheet>
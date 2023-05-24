<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  exclude-result-prefixes="#all"
  version="2.0">
  
  <xsl:variable name="query-params" select="/*/req:parameters/req:parameter" as="element()*"/>
  
  <xsl:template match="/">
    <xsl:variable name="x-connection" select="$query-params[@name = 'x-connection']/req:value[1]" as="xs:string?"/>
    <resp:response status="200">
      <resp:headers>
        <resp:header name="Content-Type">application/xml;charset=UTF-8</resp:header>
      </resp:headers>
      <resp:body>
        <diagnostics>
          <xsl:choose>
            <xsl:when test="not($x-connection)">
              <diagnostic>
                <uri>info:srw/diagnostic/1/7</uri>
                <details>x-connection</details>
                <message>Mandatory parameter not supplied</message>
              </diagnostic>
            </xsl:when>
            <xsl:otherwise>
              <diagnostic>
                <uri>info:srw/diagnostic/1/6</uri>
                <details>x-connection</details>
                <message>Unsupported parameter value</message>
              </diagnostic>
            </xsl:otherwise>
          </xsl:choose>
        </diagnostics>
      </resp:body>
    </resp:response>
  </xsl:template>
  
</xsl:stylesheet>
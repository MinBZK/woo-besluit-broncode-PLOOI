<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"  
  xmlns:http="http://expath.org/ns/http-client"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:err="http://www.w3.org/2005/xqt-errors"
  xmlns:frbr="https://koop.overheid.nl/namespaces/frbr"
  xmlns:file="http://expath.org/ns/file"
  xmlns:xw="http://www.armatiek.com/xslweb/functions"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:include href="../../../../common/xsl/lib/xslweb/xslweb.xsl"/>
  
  <xsl:function name="frbr:get-document" as="document-node()?">
    <xsl:param name="url" as="xs:string"/>
    <xsl:param name="username" as="xs:string?"/>
    <xsl:param name="password" as="xs:string?"/>
    <xsl:param name="auth-method" as="xs:string?"/>
    <xsl:try>
      <xsl:variable name="get-request" as="element(http:request)">
        <http:request 
          method="GET" 
          href="{$url}"
          timeout="10"
          override-media-type="text/xml">
          <xsl:if test="$username and $password and $auth-method">
            <xsl:attribute name="username" select="$username"/>
            <xsl:attribute name="password" select="$password"/>
            <xsl:attribute name="auth-method" select="$auth-method"/>
            <xsl:attribute name="send-authorization">true</xsl:attribute>
          </xsl:if>
        </http:request>
      </xsl:variable>
      <xsl:variable name="response" select="http:send-request($get-request)"/> 
      <xsl:choose>
        <xsl:when test="$response[1]/xs:integer(@status) ne 200">
          <xsl:document>
            <error status="{$response[1]/xs:integer(@status)}">
              <xsl:value-of select="$response[1]/@message"/>
            </error>
          </xsl:document>
        </xsl:when>
        <xsl:otherwise>
          <xsl:sequence select="$response[2]"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:catch>
        <xsl:document>
          <xsl:choose>
            <xsl:when test="local-name-from-QName($err:code) = 'HC006'">
              <xsl:variable name="msg" select="'Timeout communicating with backend server (' || $url || ')'"/>
              <error status="600">
                <xsl:value-of select="$msg"/>
              </error>
              <xsl:sequence select="log:log('ERROR', $msg)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="msg" select="string-join($err:description, ', ') || ' (' || string-join($err:code, ', ') || ', line: ' || string-join($err:line-number, ', ') || ', column: ' || string-join($err:column-number, ', ') || ', url:' || $url || ')'" as="xs:string"/>
              <error status="500">
                <xsl:value-of select="$msg"/>
              </error>
              <xsl:sequence select="log:log('ERROR', $msg)"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:document>
      </xsl:catch>
    </xsl:try>
  </xsl:function>
  
  <xsl:function name="frbr:get-document" as="document-node()?">
    <xsl:param name="url" as="xs:string"/>
    <xsl:sequence select="frbr:get-document($url,(),(),())"/>
  </xsl:function>

  <xsl:function name="frbr:get-document-filesystem" as="document-node()">
    <xsl:param name="path" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="file:exists($path)">
        <xsl:sequence select="document(xw:path-to-file-uri($path))"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:document>
          <error status="404">File not found</error>
        </xsl:document>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>
    
</xsl:stylesheet>
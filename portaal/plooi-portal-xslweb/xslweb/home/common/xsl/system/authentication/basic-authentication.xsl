<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"  
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:session="http://www.armatiek.com/xslweb/session"  
  xmlns:auth="http://www.armatiek.com/xslweb/auth"
  xmlns:base64="http://www.armatiek.com/xslweb/functions/base64"  
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:err="http://expath.org/ns/error"
  exclude-result-prefixes="#all"
  version="2.0">
  
  <xsl:variable name="session:attr-name-userprofile" as="xs:string">xslweb-userprofile</xsl:variable>
  
  <xsl:function name="auth:logout" as="xs:boolean?">
    <xsl:sequence select="session:set-attribute($session:attr-name-userprofile)"/>
  </xsl:function>
    
  <xsl:function name="auth:credentials" as="xs:string*">    
    <xsl:param name="request" as="document-node()"/>        
    <xsl:variable name="authorization-header" select="$request/*/req:headers/req:header[lower-case(@name) = 'authorization']/text()" as="xs:string?"/>    
    <xsl:sequence select="if ($authorization-header) then tokenize(base64:decode(substring-after(normalize-space($authorization-header), ' ')), ':') else ()"/> 
  </xsl:function>
  
  <xsl:template match="/req:request[auth:must-authenticate(/)]" priority="9">
    <xsl:variable name="user-profile" select="session:get-attribute($session:attr-name-userprofile)" as="element()?"/>
    <xsl:choose>
      <xsl:when test="$user-profile">        
        <xsl:next-match/>
      </xsl:when>
      <xsl:otherwise>        
        <xsl:variable name="credentials" select="auth:credentials(/)" as="xs:string*"/>        
        <xsl:choose>                    
          <xsl:when test="(count($credentials) = 2)">
            <xsl:variable name="user-profile" select="auth:login($credentials[1], $credentials[2])" as="element()?"/>
            <xsl:choose>
              <xsl:when test="$user-profile">
                <xsl:value-of select="session:set-attribute($session:attr-name-userprofile, $user-profile)"/>                
                <xsl:next-match/>    
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="auth:unauthorized-response"/>
              </xsl:otherwise>
            </xsl:choose>                                    
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="auth:unauthorized-response"/>                   
          </xsl:otherwise>
        </xsl:choose>        
      </xsl:otherwise>      
    </xsl:choose>                
  </xsl:template>
  
  <xsl:template name="auth:unauthorized-response" as="element()">    
    <resp:response status="401"> <!-- Unauthorized -->
      <resp:headers>
        <resp:header name="Content-Type">text/html; charset=utf-8</resp:header>
        <resp:header name="X-Content-Type-Options">nosniff</resp:header>
        <resp:header name="Strict-Transport-Security">max-age=1000; includeSubDomains; preload</resp:header>
        <resp:header name="Content-Security-Policy"> upgrade-insecure-requests</resp:header>
        <!--        <resp:header name="Content-Security-Policy">default-src self; frame-ancestors self;</resp:header>
                       This header prevents the download of images and other layout related items.

                      Using CSP, you can nail down what your site should include and what not. But it is hard and can break your site
                      Using the Content-Security-Policy-Report-Only mode browsers only log resources that would be blocked
                      to the console instead of blocking them. This reporting mechanism gives you a way to check and adjust your ruleset.
                      <resp:header name="Content-Security-Policy-Report-Only"> default-src 'self'; ... report-uri https://reporting URI</resp:header>
                   -->
        <resp:header name="Cache-Control">max-age=30, public, immutable</resp:header>
        <resp:header name="Referrer-Policy">same-origin</resp:header>
        <resp:header name="X-Frame-Options">SAMEORIGIN</resp:header>
        <resp:header name="WWW-Authenticate">
          <xsl:value-of select="concat('Basic realm=', '&quot;', auth:get-realm(), '&quot;')"/>
        </resp:header>                   
      </resp:headers>
      <resp:body/>                      
    </resp:response>
  </xsl:template>
  
</xsl:stylesheet>
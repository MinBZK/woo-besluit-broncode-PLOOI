<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:dlogger-impl="http://www.armatiek.nl/functions/dlogger"
  xmlns:dlogger="http://www.armatiek.nl/xslweb/functions/dlogger"  
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:variable name="dlogger-active" select="system-property('dlogger') = 'true'" static="true" as="xs:boolean"/>
  <xsl:include href="../../../DLogger-common/xsl/DLogger.xsl" use-when="$dlogger-active"/>
  
  <xsl:function name="dlogger:init" as="empty-sequence()">
    <xsl:sequence select="dlogger-impl:init()" use-when="$dlogger-active"/>
  </xsl:function> 
  
  <xsl:function name="dlogger:init" as="empty-sequence()">
    <xsl:param name="clear" as="xs:boolean"/>
    <xsl:sequence select="dlogger-impl:init($clear)" use-when="$dlogger-active"/>
  </xsl:function>
  
  <xsl:function name="dlogger:save" as="empty-sequence()">
    <xsl:param name="label" as="xs:string"/>
    <xsl:param name="contents" as="item()*"/>
    <xsl:sequence select="dlogger-impl:save($label, $contents)" use-when="$dlogger-active"/> 
  </xsl:function>
  
  <xsl:function name="dlogger:save" as="empty-sequence()" use-when="$dlogger-active">
    <xsl:param name="label" as="xs:string"/>
    <xsl:param name="contents" as="item()*"/>
    <xsl:param name="type" as="xs:string?"/>
    <xsl:sequence select="dlogger-impl:save($label, $contents, $type)" use-when="$dlogger-active"/> 
  </xsl:function>
  
  <xsl:function name="dlogger:comment" as="comment()?">
    <xsl:param name="label" as="xs:string"/>
    <xsl:sequence select="dlogger-impl:comment($label)" use-when="$dlogger-active"/>
  </xsl:function>  
  
</xsl:stylesheet>
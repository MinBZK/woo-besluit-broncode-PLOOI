<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:testing-impl="http://www.armatiek.nl/functions/testing"
  xmlns:testing="http://www.armatiek.nl/xslweb/functions/testing"  
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:variable name="testing-active" select="system-property('testing') = 'true'" static="true" as="xs:boolean"/>
  <xsl:include href="../../../common/xsl/content-tests/text-compare.xsl" use-when="$testing-active"/>
  
  <xsl:function name="testing:do-text-compare" as="element(compare)?">
    <xsl:param name="html-root" as="element()"/>
    <xsl:param name="xml-root" as="element()"/>
    <xsl:param name="xml-url" as="xs:string"/>
    <xsl:sequence select="testing-impl:do-text-compare($html-root,$xml-root,$xml-url)" use-when="$testing-active"/>
  </xsl:function> 
  
</xsl:stylesheet>
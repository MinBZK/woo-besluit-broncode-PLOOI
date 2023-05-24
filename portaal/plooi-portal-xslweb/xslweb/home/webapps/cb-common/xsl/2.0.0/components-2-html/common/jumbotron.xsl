<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
    
  <xsl:template match="cl:jumbotron">
    <div class="jumbotron{if (cl:modifier) then ' jumbotron--' || cl:modifier else ()}">
      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
        <xsl:attribute name="id" select="cl:id"/>
      </xsl:if>
      <div class="jumbotron__content{if (cl:centered) then ' jumbotron__content--centered' else ()}{if (cl:size) then (' jumbotron__content--centered-' || cl:size) else ()}{if (cl:content-centered) then ' jumbotron__content--aligncenter' else ()}">
        <xsl:if test="cl:backlink">
          <p class="jumbotron__backlink">
            <a href="{cl:backlink-href}" class="icon--backlink"><xsl:sequence select="cl:backlink/cl:title"></xsl:sequence></a>
          </p>
        </xsl:if>
        <div class="jumbotron__heading {if (cl:heading-centered) then 'jumbotron__heading--centered' else ()}">
          <xsl:if test="cl:sub-heading">
            <p class="jumbotron__subheader">
              <xsl:apply-templates select="cl:sub-heading/node()" mode="copy"/>
            </p>
          </xsl:if>
          <xsl:if test="cl:heading">
            <xsl:element name="{if (cl:h) then cl:h else 'h1'}">
              <xsl:attribute name="class" select="'jumbotron__header ' || xs:string(if (cl:hstyle) then cl:hstyle else ())"/>
              <xsl:apply-templates select="cl:heading/node()" mode="copy"/>
            </xsl:element>
          </xsl:if>
          <xsl:if test="cl:intro">
            <p class="jumbotron__intro">
              <xsl:apply-templates select="cl:intro/node()" mode="copy"/>
            </p>
            <xsl:apply-templates select="cl:text/node()" mode="copy"/>
          </xsl:if>
        </div>
        <xsl:apply-templates select="cl:partial-block/node()" mode="copy"/>
      </div>
    </div>    
  </xsl:template>
  
</xsl:stylesheet>
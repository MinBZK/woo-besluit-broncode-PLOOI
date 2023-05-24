<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:list">
    <xsl:variable name="l" as="element(div)">
      <div>
        <xsl:if test="cl:max-items">
          <xsl:attribute name="data-decorator">showmoreless</xsl:attribute>
          <xsl:attribute name="data-config">{"amount": "<xsl:value-of select='cl:max-items'/>"}</xsl:attribute>
        </xsl:if>
        <xsl:if test="cl:heading">
          <xsl:element name="{if (cl:heading-type) then cl:heading-type else 'h4'}">
            <xsl:attribute name="class">facet--heading</xsl:attribute>
            <xsl:value-of select="cl:heading"/>
          </xsl:element>
        </xsl:if>
        <xsl:element name="{if (cl:ordered-list = 'true') then 'ol' else 'ul'}">
          <xsl:attribute name="class" select="if (cl:custom = 'true') then 'list-custom ' else 'list ' || cl:modifier"/>
          <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
            <xsl:attribute name="id" select="cl:id"/>
          </xsl:if>
          <xsl:choose>
            <xsl:when test="cl:ordered-list = 'true'">
              <xsl:apply-templates select="cl:items/cl:item" mode="list-ordered-list">
                <xsl:with-param name="custom" select="cl:custom" as="xs:string?" tunnel="yes"/>
              </xsl:apply-templates>    
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="cl:items/cl:item" mode="list-unordered-list">
                <xsl:with-param name="custom" select="cl:custom" as="xs:string?" tunnel="yes"/>
              </xsl:apply-templates>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>
      </div>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="cl:max-items">
        <div data-decorator="showmoreless">
          <xsl:attribute name="data-config">{"amount": "<xsl:value-of select="cl:max-items"/>"}</xsl:attribute>
          <xsl:sequence select="$l"/>
        </div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:sequence select="$l"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="cl:items/cl:item" mode="list-ordered-list">
    <xsl:param name="custom" as="xs:string?" tunnel="yes"/>
    <li>
      <xsl:attribute name="class" select="if ($custom = 'true') then 'list-custom__item' else 'list__item'"/>
      <xsl:apply-templates select="node()" mode="copy"/>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:items/cl:item/cl:sub-items" mode="list-ordered-list">
    <xsl:param name="ordered-list" as="xs:string?" tunnel="yes"/>
    <xsl:param name="custom" as="xs:string?" tunnel="yes"/>
    <ol>
      <xsl:apply-templates select="cl:sub-item"/>
    </ol>
  </xsl:template>
  
  <xsl:template match="cl:items/cl:item/cl:sub-items/cl:sub-item" mode="list-ordered-list">
    <xsl:param name="custom" as="xs:string?" tunnel="yes"/>
    <li>
      <xsl:attribute name="class" select="if ($custom = 'true') then 'list-custom__item' else 'list__item'"/>
      <xsl:apply-templates select="node()" mode="copy"/>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:items/cl:item" mode="list-unordered-list">
    <xsl:param name="custom" as="xs:string?" tunnel="yes"/>
    <li>
      <xsl:attribute name="class" select="if ($custom = 'true') then 'list-custom__item' else 'list__item'"/>
      <xsl:if test="cl:color">
        <span class="{cl:color}"></span>  
      </xsl:if>
      <xsl:apply-templates select="node()" mode="copy"/>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:items/cl:item/cl:sub-items" mode="list-unordered-list">
    <xsl:param name="custom" as="xs:string?" tunnel="yes"/>
    <ul>
      <xsl:attribute name="class" select="if ($custom = 'true') then 'list-custom' else ()"/>
      <xsl:apply-templates select="cl:sub-item"/>
    </ul>    
  </xsl:template>
  
  <xsl:template match="cl:items/cl:item/cl:sub-items/cl:sub-item" mode="list-unordered-list">
    <xsl:param name="custom" as="xs:string?" tunnel="yes"/>
    <li>
      <xsl:apply-templates select="node()" mode="copy"/>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
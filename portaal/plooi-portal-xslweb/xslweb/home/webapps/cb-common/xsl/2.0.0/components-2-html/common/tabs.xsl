<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:tabs[cl:tab]">
    <div class="tabs {cl:modifier}" data-decorator="init-tabs">
      <ul class="tabs__list">
        <xsl:apply-templates select="cl:tab" mode="tabs-list"/>
      </ul>
      <div class="tabs__panels">
        <xsl:apply-templates select="cl:tab" mode="tabs-panels"/>
      </div>
    </div>    
  </xsl:template>
  
  <xsl:template match="cl:tab" mode="tabs-list">
    <xsl:variable name="id" select="(cl:id, generate-id())[1]" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="cl:cta = 'true'">
        <li>
          <button class="button {cl:modifier}" href="{cl:action}" role="button">
            <xsl:value-of select="cl:text"/>
          </button>
        </li>    
      </xsl:when>
      <xsl:otherwise>
        <li role="presentation">
          <a href="#panel-{$id}" data-handler="open-panel" role="tab" id="tab-{$id}" aria-controls="panel-{$id}">
            <xsl:value-of select="cl:label"/>
          </a>
        </li>    
      </xsl:otherwise>
    </xsl:choose>    
  </xsl:template>
  
  <xsl:template match="cl:tab" mode="tabs-panels">
    <xsl:variable name="id" select="(cl:id, generate-id())[1]" as="xs:string"/>
    <div id="panel-{$id}" role="tabpanel" aria-labelledby="tab-{$id}">
      <xsl:apply-templates select="cl:content/node()" mode="copy"/>
      <xsl:if test="cl:has-buttons = 'true'">
        <button type="button" data-handler="open-previous-panel" class="tabs__prev button button--secondary">
          <xsl:value-of select="cl:text-previous"/>
        </button>
        <button type="button" data-handler="open-next-panel" class="tabs__next button button--secondary">
          <xsl:value-of select="cl:text-next"/>
        </button>
      </xsl:if>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
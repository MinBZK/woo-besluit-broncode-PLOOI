<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:result-list">
    <div class="result--list { cl:modifier }">
      <ul>
        <xsl:apply-templates mode="result-list"/>
      </ul>
    </div>
  </xsl:template>
  
  <xsl:template match="cl:modifier|cl:theme-name|cl:title-header|cl:subtitle|cl:title-href|cl:subtitle-href|cl:label|cl:metadata-dates|cl:title-icon" mode="result-list"/>
    
  <xsl:template match="cl:context/cl:items/cl:item" mode="result-list">
    <li>
      <xsl:apply-templates mode="#current"/>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:header-img" mode="result-list">
    <p>
      <img src="{.}" />
    </p>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:label[empty(cl:label-type)]" mode="result-list">
    <p class="result--label">
      <i>
        <xsl:value-of select="."/>
      </i>
    </p>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:label[cl:label-type]" mode="result-list">
    <p class="result--label">
      <i>
        <span class="label label--{cl:label-type}">
          <xsl:value-of select="cl:label-value"/>
        </span>
      </i>
    </p>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:theme" mode="result-list">
    <span class="icon icon--{.}">
      <xsl:value-of select="../cl:theme-name"/><!-- TODO cl:theme-name niet meer beschikbaar in 1.35 (verwijderd ergens tussen 1.25 en 1.35) -->
    </span>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:title" mode="result-list">
    <xsl:choose>
      <xsl:when test="../cl:title-header">
        <xsl:element name="{../cl:title-header}">
          <xsl:attribute name="class">result--title <xsl:if test="../cl:title-icon = 'true'">icon icon--large icon--<xsl:value-of select="../cl:theme"/></xsl:if></xsl:attribute>
          <xsl:choose><!-- 1.9.38 toegevoegd -->
            <xsl:when test="../cl:title-href">
              <a href="{../cl:title-href}">
                <xsl:apply-templates select="node()"/>
              </a>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="node()"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <a href="{cl:href}" class="result--title">
          <xsl:value-of select="."/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="../cl:subtitle"><!-- upgrade 1.9.40 - 1.9.44 -->
      <p>
        <a href="{../cl:subtitle-href}" class="result--subtitle">
          <xsl:apply-templates select="../cl:subtitle/node()"/>
        </a>
      </p>
    </xsl:if>
    <xsl:if test="../cl:publication-details"><!-- upgrade 1.9.40 - 1.9.44 -->
      <xsl:apply-templates select="../cl:publication-details"/>
    </xsl:if>
    <xsl:if test="../cl:metadata-dates">
      <ul class="list--metadata">
        <xsl:for-each select="../cl:metadata-dates/cl:metadata-date">
          <li>
            <xsl:apply-templates select="cl:value/node()"/>
          </li>
        </xsl:for-each>
      </ul>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:text" mode="result-list">
    <p>
      <xsl:apply-templates/>
    </p>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:address" mode="result-list"><!-- toegevoegd in 1.25 - 1.35 -->
    <address>
      <xsl:apply-templates/>
    </address>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:owner" mode="result-list">
    <p>Organisatie: <xsl:value-of select="."/></p><br />
    <xsl:if test="cl:definition-list">
      <dl class="dl dl--horizontal dl--{cl:modifier}">
        <xsl:apply-templates select="cl:items/cl:item"/>
      </dl>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:cvdrmetas" mode="result-list">
    <dl class="dl dl--horizontal dl--condensed">
      <xsl:apply-templates select="cl:cvdrmeta" mode="#current"/>
    </dl>
  </xsl:template>
  
  <xsl:template match="cl:cvdrmetas/cl:cvdrmeta" mode="result-list">
    <dt>
      <xsl:value-of select="cl:label"/>
    </dt>
    <dd>
      <xsl:value-of select="cl:value"/>
    </dd>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:owner/cl:definition-list/cl:items/cl:item" mode="result-list">
    <dt>
      <xsl:value-of select="cl:title"/>
    </dt>
    <dd>
      <xsl:value-of select="cl:description"/>
    </dd>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item[cl:theme]/cl:metadatas" mode="result-list">
    <p><!-- upgrade 1.9.40 - 1.9.44 -->
      Onderwerpen:
      <xsl:apply-templates mode="#current"/>
    </p>
  </xsl:template>

  <xsl:template match="cl:context/cl:items/cl:item[cl:theme]/cl:metadatas/cl:metadata" mode="result-list">
    <a href="{cl:link}">
      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
        <xsl:attribute name="id" select="cl:id"/>
      </xsl:if>
      <xsl:apply-templates select="cl:value/node()"/>
    </a>
    <xsl:if test="not(position() = last())">,</xsl:if>
  </xsl:template>

  <xsl:template match="cl:context/cl:items/cl:item[not(cl:theme)]/cl:metadatas" mode="result-list">
    <ul class="list--metadata">
      <xsl:apply-templates mode="#current"/>
    </ul>
  </xsl:template>

  <xsl:template match="cl:context/cl:items/cl:item[not(cl:theme)]/cl:metadatas/cl:metadata" mode="result-list">
    <li>
      <xsl:apply-templates select="cl:value/node()"/>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item[cl:theme]/cl:downloads" mode="result-list">
    <ul class="list--metadata">
      <xsl:apply-templates mode="#current"/>
    </ul>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item[cl:theme]/cl:downloads/cl:download" mode="result-list">
    <span class="label label--{cl:type}">
      <xsl:value-of select="cl:type"/>
    </span>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item[not(cl:theme)]/cl:downloads" mode="result-list">
    <ul class="result--actions">
      <xsl:apply-templates mode="#current"/>
    </ul>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item[not(cl:theme)]/cl:downloads/cl:download" mode="result-list">
    <li>
      <a href="{cl:link}" class="icon icon--download">
        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
          <xsl:attribute name="id" select="cl:id"/>
        </xsl:if>
        <xsl:if test="cl:prelabel = 'true'"><!-- upgrade 1.9.40 - 1.9.44 -->
          <span>Download de</span> <xsl:value-of select="string-join((cl:type, cl:size), ' ')"/>
        </xsl:if>
      </a>
    </li>
  </xsl:template>
  
  <!-- TODO: apply-templates op collapsible definitie toevoegen -->
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:buttons" mode="result-list">
    <ul class="list list--inline list--narrow">
      <xsl:apply-templates mode="#current"/>
    </ul>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:buttons/cl:button" mode="result-list">
    <li class="list__item">
      <a href="{cl:link}" class="button button--{cl:type}">
        <xsl:if test="cl:modal = 'true'">
          <xsl:attribute name="data-decorator">init-modal</xsl:attribute>
          <xsl:attribute name="data-handler">open-modal</xsl:attribute>
          <xsl:attribute name="data-config">'{"function":"kpmService", "action":"push", "data":"data"}'</xsl:attribute>
          <xsl:attribute name="data-modal" select="cl:action"/>
        </xsl:if> 
        <xsl:value-of select="cl:label"/>
      </a>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:buttons/cl:share" mode="result-list">
    <li class="list__item d-pull-right">
      <a href="{cl:link}" class="button button--tertiary button--share">
        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
          <xsl:attribute name="id" select="cl:id"/>
        </xsl:if>
        <xsl:text>Delen</xsl:text>
      </a>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:linklist" mode="result-list">
    <h3 class="h4">
      <xsl:value-of select="if (cl:label) then (cl:label) else if (cl:linklist-heading) then cl:linklist-heading else 'Bekijk document direct via'"/>
    </h3>
    <div data-decorator="showmoreless">
      <xsl:attribute name="data-config">{"amount":"2"}</xsl:attribute>
      <ul class="list list--linked">
        <xsl:apply-templates select="cl:items/cl:item" mode="#current"/>
      </ul>
    </div>
  </xsl:template>
  
  <xsl:template match="cl:context/cl:items/cl:item/cl:linklist/cl:items/cl:item" mode="result-list"><!-- aanpassing niet relevant, in 1.25 - 1.35 -->
    <li>
      <a href="{cl:link}">
        <xsl:choose>
          <xsl:when test="cl:label">
            <xsl:apply-templates select="cl:label/node()"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="cl:link"/>
          </xsl:otherwise>
        </xsl:choose>
      </a>  
      <xsl:value-of select="' '"/>
      <xsl:apply-templates select="cl:text/node()"/>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
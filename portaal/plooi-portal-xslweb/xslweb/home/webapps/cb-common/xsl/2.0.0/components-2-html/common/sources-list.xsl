<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library" exclude-result-prefixes="#all"
  version="3.0">

  <xsl:template match="cl:sources-list">
    <ul id="{cl:id}" class="list--sources">
      <xsl:apply-templates select="cl:items/cl:item" mode="sources-list"/>
    </ul>
  </xsl:template>

  <xsl:template match="cl:items/cl:item" mode="sources-list">
    <li>
      <div class="list--source__information {cl:icon}">
        <xsl:choose>
          <xsl:when test="cl:name-href">
            <a href="{cl:name-href}"><!-- upgrade 1.9.38 - 1.9.40 -->
              <xsl:if test="cl:name-id">
                <xsl:attribute name="id" select="cl:name-id"/>
              </xsl:if>
              <xsl:value-of select="cl:name"/>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="cl:name"/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:choose>
           <xsl:when test="cl:date"><!-- 1.25 naar 1.35: toegevoegd -->
             <div class="list--sources__item__date">
               <xsl:value-of select="cl:date/cl:timestamp"/>
               <xsl:apply-templates select="cl:date/cl:tooltip" mode="#current"/><!-- {{ render "@tooltip" date/tooltip merge=true }} -->
             </div>
           </xsl:when>
          <xsl:when test="cl:filesize">
            <span class="list--sources__item__filesize">
              <xsl:value-of select="cl:filesize"/>
            </span>
          </xsl:when>
        </xsl:choose>    
        <xsl:apply-templates select="cl:files/cl:file" mode="sources-list"/>
      </div>
      <!--
      <xsl:apply-templates select="cl:button" mode="sources-list"/>
      -->
      
      <xsl:choose>
        <xsl:when test="cl:button-context/cl:action">
          <a href="{cl:button-context/cl:action}" class="button {cl:button-context/cl:modifier}">
            <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
              <xsl:attribute name="id" select="cl:id"/>
            </xsl:if>
            <xsl:value-of select="cl:button-context/cl:text"/>
            <span class="visually-hidden">
              <xsl:value-of select="'van abonnement:' || cl:name"/>
            </span>
          </a>
        </xsl:when>
        <xsl:when test="cl:button-context/cl:default-actions"><!-- upgrade 1.9.40 - 1.9.44 -->
          <div class="list--source__defaultactions">
            <xsl:for-each select="cl:button-context/cl:default-actions/cl:action">
              <xsl:choose>
                <xsl:when test="cl:button-icon">
                  <xsl:value-of select="cl:button-icon"/>
                </xsl:when>
                <xsl:otherwise>
                  <a href="{cl:action}" class="button {cl:modifier}">
                    <xsl:if test="cl:id">
                      <xsl:attribute name="id" select="cl:id"/>
                    </xsl:if>
                    <xsl:value-of select="cl:text"/>
                    <span class="visually-hidden">
                      <xsl:value-of select="'van abonnement:' || cl:name"/>
                    </span>
                  </a>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
          </div>
        </xsl:when>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="cl:actions-one">
          <div class="list--sources__actions" data-decorator="init-form-conditionals">
            <!-- r1.9.19 (TM) Ik vermoed dat ik het verkeerd doe. Er staat:
              data-config='{"questionIdTag": "#{{idprefix}}"}'
            en ik kan de "#" niet plaatsen - ik neem aan dat er hier een functie uitgewerkt moet worden
            zie hieronder ook opmerking over 'id="{{idprefix}}1"'-->
            <xsl:attribute name="data-config">{"questionIdTag": "<xsl:value-of select="cl:idprefix"/>"}</xsl:attribute>
            <!-- r1.9.19 (TM) in sources-list.handlebars staat hier id="{{idprefix}}1", ik weet niet wat "1" hier is -->
            <div id="{cl:idprefix[1]}" class="js-form-conditionals__citem"><!-- 1.25 naar 1.35: aangepast -->
              <xsl:value-of select="cl:actions-one-label"/>
              <xsl:apply-templates select="cl:actions-one" mode="sources-list"/>
            </div>
            <div id="{cl:idprefix[2]}" class="js-form-conditionals__citem"><!-- 1.25 naar 1.35: aangepast -->
              <em>
                <xsl:value-of select="cl:actions-two-label"/>
              </em>
              <xsl:apply-templates select="cl:actions-two" mode="sources-list"/>
            </div>
            <div id="{cl:idprefix[3]}" class="js-form-conditionals__citem"><!-- 1.25 naar 1.35: aangepast -->
              <xsl:value-of select="cl:actions-three-label"/>
              <xsl:apply-templates select="cl:alternative-actions" mode="sources-list"/>
            </div>
          </div>
        </xsl:when>
        <!-- r1.9.19 (TM) in sources-list.handlebars staat hier een {{else}} zonder specificatie, misschien volgt die nog
        vandaar xsl:choose-->
        <xsl:otherwise/>
      </xsl:choose>
      
      <xsl:if test="cl:metadata"><!-- 1.25 naar 1.35: toegevoegd -->
        <div class="list--source__metadata">
          <dl class="dl dl--horizontal">
            <xsl:for-each select="cl:metadata/cl:items/cl:item">
              <dt><xsl:apply-templates select="cl:dt"/></dt>
              <dd><xsl:apply-templates select="cl:dd"/></dd>
            </xsl:for-each>
          </dl>
        </div>
      </xsl:if>
      
      <xsl:if test="cl:more-info"><!-- upgrade 1.9.38 - 1.9.40 -->
        <div class="collapsible collapsible--small" data-decorator="init-collapsible" id="c{if (cl:moreinfo-id) then cl:moreinfo-id else 'c1'}">
          <div class="collapsible__header">
            <a href="#{if (cl:moreinfo-id) then cl:moreinfo-id else 'c1'}" 
               aria-controls="collapsible-content-{if (cl:collapsible-content-id) then cl:collapsible-content-id else '1'}" 
               aria-expanded="true" data-handler="toggle-collapsible" data-text="show/hide">
              <xsl:text>toon informatie</xsl:text>
            </a>
          </div>
          <div class="collapsible__content" id="collapsible-content-{if (cl:collapsible-content-id) then cl:collapsible-content-id else '1'}">
            <div>
              <h3>
                <xsl:value-of select="cl:title"/>
              </h3>
              <div class="well well--linkContainer">
                <xsl:value-of select="cl:link"/>
              </div>
              <xsl:if test="cl:table-data">
                <table>
                  <xsl:apply-templates select="cl:row" mode="sources-list"/>
                </table>
              </xsl:if>
            </div>
          </div>
        </div>
      </xsl:if>
    </li>
  </xsl:template>

  <xsl:template match="cl:actions-one" mode="sources-list"><!-- version 1.35 -> 1.37 aangepast: <a> versus <button> -->
    <xsl:variable name="icon-only-classes" select="if (cl:icon-only) then 'button-icon button-icon--plain button-icon--bin' else ('button ' || cl:button-context/cl:modifier)"/>
    <xsl:call-template name="actions-one-and-two">
      <xsl:with-param name="classes" select="$icon-only-classes"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="cl:actions-two" mode="sources-list"><!-- version 1.35 -> 1.37 aangepast: <a> versus <button> -->
    <xsl:variable name="icon-only-classes" select="'button ' || cl:button-context/cl:modifier"/>
    <xsl:call-template name="actions-one-and-two">
      <xsl:with-param name="classes" select="$icon-only-classes"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="cl:alternative-actions" mode="sources-list"><!-- version 1.35 -> 1.37 aangepast: <a> versus <button> -->
    <xsl:variable name="icon-only-classes" select="'button ' || cl:button-context/cl:modifier"/>
    <xsl:call-template name="actions-one-and-two">
      <xsl:with-param name="classes" select="$icon-only-classes"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="actions-one-and-two">
    <xsl:param name="classes"/>
    <xsl:variable name="linked-to-classes" select="if (cl:linkedto) then ' js-button-next' else ''"/>
    <xsl:choose>
      <xsl:when test="cl:href">
        <a href="{cl:href}" class="{$classes}{$linked-to-classes}">
          <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
            <xsl:attribute name="id" select="cl:id"/>
          </xsl:if>
          <xsl:call-template name="actions-link"/>
          <span class="visually-hidden">
            <xsl:value-of select="cl:label"/>
          </span>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <button class="button {$classes}{$linked-to-classes}">
          <xsl:call-template name="actions-link"/>
          <xsl:value-of select="cl:label"/>
        </button>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="actions-link">
    <xsl:if test="cl:button-type">
      <xsl:attribute name="type" select="cl:button-type"/>
    </xsl:if>
    <xsl:if test="cl:linkedto">
      <xsl:attribute name="data-linkedto" select="cl:linkedto"/>
      <xsl:attribute name="data-triggeresponds" select="cl:trigger-responds"/>
      <xsl:attribute name="data-hideself" select="cl:hideself"/>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="cl:files/cl:file" mode="sources-list">
    <xsl:text>&#160;</xsl:text>
    <span class="label label--{cl:type}">
      <xsl:value-of select="cl:type"/>
    </span>
  </xsl:template>

  <xsl:template match="cl:table-data/cl:row" mode="sources-list">
    <tr>
      <th>
        <xsl:value-of select="cl:type"/>
      </th>
      <td data-before="{cl:type}">
        <xsl:value-of select="cl:value"/>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>

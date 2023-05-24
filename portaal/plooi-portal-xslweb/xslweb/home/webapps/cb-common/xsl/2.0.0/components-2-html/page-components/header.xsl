<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:template match="cl:header">
    <xsl:apply-templates select="cl:skipLinks"/><!-- 1.25 naar 1.35: verplaatst -->
    <xsl:variable name="collapse-other-sides" select="cl:collapse-other-sites = 'true'" as="xs:boolean"/>
    <header class="header">
      <div class="header__start">
        <div class="container">
          <xsl:if test="cl:nav = 'true'">
            <button type="button" class="hidden-desktop button button--icon-hamburger" data-handler="toggle-nav" aria-controls="nav" aria-expanded="false">
              <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
                <xsl:attribute name="id" select="cl:id"/>
              </xsl:if>
              <xsl:value-of select="cl:hamburgerButtonText"/>
            </button>
          </xsl:if>
          <xsl:apply-templates select="cl:logo"/>
          <xsl:apply-templates select="cl:metaLinks"/>
          <xsl:if test="cl:language">
            <div class="header__meta">
              <a href="{cl:language/cl:link}" class="link-iconed link-iconed--publication">
                <xsl:if test="cl:language/cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
                  <xsl:attribute name="id" select="cl:language/cl:id"/>
                </xsl:if>
                <xsl:value-of select="cl:language/cl:label"/>
              </a>
            </div>
          </xsl:if>
        </div>
      </div>
      <xsl:if test="cl:nav = 'true'">
        <nav class="header__nav header__nav--closed" id="nav">
          <h2 class="visually-hidden">Primaire navigatie</h2>
          <div class="container">
            <ul class="header__primary-nav list list--unstyled">
              <li class="hidden-mobile">
                <a href="#other-sites" data-handler="toggle-other-sites" data-decorator="init-toggle-other-sites">
                  <xsl:if test="$collapse-other-sides">
                    <xsl:attribute name="aria-controls">other-sites</xsl:attribute>
                    <xsl:attribute name="aria-expanded">false</xsl:attribute>
                  </xsl:if>
                  <span class="visually-hidden">Andere sites binnen </span>Overheid.nl
                </a>
              </li>
              <xsl:apply-templates select="cl:navLinks/cl:link"/>
            </ul>
            <xsl:if test="cl:headerMore/cl:item">
              <a href="#other-sites" class="hidden-desktop" data-handler="toggle-other-sites" data-decorator="init-toggle-other-sites">
                <xsl:if test="$collapse-other-sides">
                  <xsl:attribute name="aria-controls">other-sites</xsl:attribute>
                  <xsl:attribute name="aria-expanded">false</xsl:attribute>
                </xsl:if>
                <span class="visually-hidden">Andere sites binnen </span>Overheid.nl
              </a>
            </xsl:if>
          </div>
        </nav>
      </xsl:if>
    </header>
    <xsl:if test="cl:headerMore/cl:item">
      <xsl:apply-templates select="cl:headerMore">
        <xsl:with-param name="collapse-other-sides" select="$collapse-other-sides" tunnel="yes"/>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>

  <xsl:template match="cl:skipLinks">
    <div class="skiplinks container"><!-- 1.25 naar 1.35: aangepast -->
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="cl:skipLinks/cl:link">
    <a href="{cl:url}">
      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
        <xsl:attribute name="id" select="cl:id"/>
      </xsl:if>
      <xsl:value-of select="cl:text"/>
    </a>
  </xsl:template>

  <xsl:template match="cl:metaLinks"><!-- 1.25 naar 1.35: aangepast -->
    <div class="header__meta">
      <xsl:choose>
        <xsl:when test="cl:profile">
          <xsl:apply-templates select="cl:profile"/><!-- TODO wat is dat? -->
        </xsl:when>
        <xsl:when test="cl:nonLink">
          <span class="{cl:class}">
            <xsl:value-of select="cl:text"/>
          </span>
        </xsl:when>
        <xsl:otherwise>
          <a href="{cl:url}" class="{cl:class}">
            <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
              <xsl:attribute name="id" select="cl:id"/>
            </xsl:if>
            <xsl:if test="cl:urlTarget">
              <xsl:attribute name="target" select="cl:urlTarget"/>
            </xsl:if>
            <xsl:value-of select="cl:text"/>
          </a>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="cl:metaLinks/cl:link">
    <a href="{cl:url}" class="{cl:class}">
      <xsl:value-of select="cl:text"/>
    </a>
  </xsl:template>

  <xsl:template match="cl:navLinks/cl:link[cl:modifier]">
    <li>
      <a class="button {cl:modifier}" href="{cl:action}" role="button">
        <xsl:value-of select="cl:text"/>
      </a>
    </li>
  </xsl:template>

  <xsl:template match="cl:navLinks/cl:link[not(cl:modifier)]">
    <li>
      <a href="{cl:url}" class="{cl:className}">
        <xsl:value-of select="cl:text"/>
      </a>
    </li>
  </xsl:template>

  <xsl:template match="cl:headerMore">
    <xsl:param name="collapse-other-sides" as="xs:boolean" tunnel="yes"/>
    <xsl:if test="../cl:nav = 'true'">
      <div class="header__more{if ($collapse-other-sides) then ' header__more--closed' else ()}" id="other-sites">
        <xsl:if test="$collapse-other-sides">
          <xsl:attribute name="hidden">true</xsl:attribute>
        </xsl:if>
        <div class="container columns">
          <xsl:apply-templates/>
        </div>
      </div>
    </xsl:if>
  </xsl:template>

  <xsl:template match="cl:headerMore/cl:item">
    <div>
      <h2>
        <xsl:value-of select="cl:title"/>
      </h2>
      <p>
        <xsl:value-of select="cl:description"/>
      </p>
      <xsl:if test="cl:link">
        <ul class="list list--linked">
          <xsl:apply-templates select="cl:link"/>
        </ul>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template match="cl:headerMore/cl:item/cl:link">
    <li>
      <a href="{cl:url}">
        <xsl:value-of select="cl:text"/>
      </a>
    </li>
  </xsl:template>

</xsl:stylesheet>
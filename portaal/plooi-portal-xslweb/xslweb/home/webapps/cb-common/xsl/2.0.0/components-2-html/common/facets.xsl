<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:facets">
    <div>
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="cl:facets/cl:header">
    <h2>
      <xsl:value-of select="."/>
    </h2>
    <!-- TODO
    <div class="question-explanation">
      <a href="#question-explanation-1" class="question-explanation__link"
        id="question-explanation-link-1" data-handler="toggle-explanation">Voornaam</a>
      <div class="question-explanation__content" id="question-explanation-1"
        data-decorator="hide-self" hidden="true">
        <p>U moet dit <a href="#">formulier</a> volledig en naar waarheid invullen.</p>
        <a href="#question-explanation-link-1" class="question-explanation__close"
          data-handler="close-explanation"> Sluiten </a>
      </div>
    </div>
    -->
  </xsl:template>
  
  <xsl:template match="cl:facets/cl:facet">
    <div>
      <xsl:if test="cl:max-show">
        <xsl:attribute name="data-decorator">showmoreless</xsl:attribute>
        <xsl:attribute name="data-config">{&quot;amount&quot;: &quot;<xsl:value-of select="cl:max-show"/>&quot;}</xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="cl:label"/>
      <ul class="list list--facet">
        <xsl:apply-templates select="cl:term"/>
        <!-- TODO?
        <li class="link link - -down">Toon meer</li>
        -->
      </ul>
    </div>
  </xsl:template>
  
  <xsl:template match="cl:facets/cl:facet/cl:label">
    <h4 class="facet--heading">
      <xsl:value-of select="."/>
    </h4>
  </xsl:template>
  
  <xsl:template match="cl:facets/cl:facet/cl:term">
    <li class="list__item">
      <a href="{cl:path}">
        <xsl:value-of select="cl:label || ' (' || cl:count || ')'"/>
      </a>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
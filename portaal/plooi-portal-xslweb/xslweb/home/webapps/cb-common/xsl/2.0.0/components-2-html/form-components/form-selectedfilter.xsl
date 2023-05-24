<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:selectedfilter">
    <!-- NB. Wijzigen mbt max-show/ammount etc. doorgevoerd op basis van mail van Ruurd, niet op basis van CB. De wijzigingen
    zitten waarschijnlijk pas in 1.9.23 -->
    <div>
      <xsl:choose>
        <xsl:when test="cl:max-show">
          <xsl:attribute name="data-decorator">showmoreless</xsl:attribute>
          <xsl:attribute name="data-config">{ "amount": <xsl:value-of select="cl:max-show"/> }</xsl:attribute> 
          <ul class="list list--unstyled">
            <xsl:apply-templates select="cl:item" mode="selectedfilter"/>
          </ul>    
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="cl:item" mode="selectedfilter"/>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>
  
  <xsl:template match="cl:selectedfilter/cl:item[../cl:max-show]" mode="selectedfilter" priority="2.0">
    <li>
      <xsl:next-match/>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:selectedfilter/cl:item" mode="selectedfilter">
    <div>
      <xsl:attribute name="class">
        <xsl:choose>
          <xsl:when test="cl:modifier">
            <xsl:value-of select="'form__selectedfilter form__selectedfilter--' || cl:modifier"/>
          </xsl:when>
          <xsl:otherwise>form__selectedfilter</xsl:otherwise>
        </xsl:choose>  
      </xsl:attribute>
      <xsl:value-of select="cl:label"/>
      <xsl:if test="cl:value">
        <xsl:choose>
          <xsl:when test="cl:label">
            <span>
              <xsl:value-of select="cl:value"/>
            </span>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="cl:value"/>
          </xsl:otherwise>
        </xsl:choose>  
      </xsl:if>
      <xsl:if test="not(cl:no-remove = 'true')"><!-- upgrade 1.9.40 - 1.9.44 -->
        <!-- als no-remove is opgegeven dan wordt de remove link niet getoond --> 
        <a href="{cl:link}" class="form__selectedfilter__remove">
          <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
            <xsl:attribute name="id" select="cl:id"/>
          </xsl:if>
          <span class="visually-hidden">
            <xsl:value-of select="if (cl:label-remove) then cl:label-remove else 'verwijder'"/>
          </span>
        </a>
      </xsl:if>
    </div>  
  </xsl:template>
  
</xsl:stylesheet>
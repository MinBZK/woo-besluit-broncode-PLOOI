<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:utils="https://koop.overheid.nl/namespaces/utils"
  xmlns:map="http://www.w3.org/2005/xpath-functions/map"
  xmlns:dlogger="http://www.armatiek.nl/xslweb/functions/dlogger"
  exclude-result-prefixes="#all"
  expand-text="yes"
  version="3.0">
  
  <xsl:template match="cl:sort">
    <div class="sort {cl:modifier}">
      <xsl:variable name="output">
        <xsl:value-of select="cl:label"/>
        <ul class="sort__options">
          <xsl:apply-templates select="cl:options/cl:option" mode="sort"/>
        </ul>  
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="cl:columns = 'true'"> <!-- NB. sinds versie 1.9.17 eigenlijk niet meer optioneel, als aantal resultaten per pagina is geimplementeerd dan doorvoeren -->
          <div class="columns">
            <div class="column column-d-8"><!-- 1.25 naar 1.35: aangepast -->
              <xsl:if test="cl:options/cl:option">
                <xsl:sequence select="$output"/>  
              </xsl:if>
            </div>
            <div class="column column-d-4 d-align-right">
              <xsl:for-each select="cl:button-download">
                <a href="{cl:link}" class="icon icon--download" role="button"><!-- 1.25 naar 1.35: aangepast -->
                  <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
                    <xsl:attribute name="id" select="cl:id"/>
                  </xsl:if>
                  <xsl:value-of select="cl:label"/>
                </a>  
              </xsl:for-each>
              <xsl:for-each select="cl:amount-per-page-filter"><!-- singleton -->
                <xsl:value-of select="cl:label"/>
              
                <xsl:variable name="url" select="cl:reset-url(cl:base-url)"/>
                <ul class="sort__options">
                  <li>
                    <a href="{$url}&amp;count=10" class="{if (cl:amount = '10') then 'is-active' else ''}" aria-pressed="{if (cl:amount = '10') then 'true' else 'false'}" role="button"><!-- 1.25 naar 1.35: aangepast -->
                      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
                        <xsl:attribute name="id" select="cl:id"/>
                      </xsl:if>
                      <span class="visually-hidden">Toon </span>10<span class="visually-hidden"> resultaten per pagina</span>
                    </a>
                  </li>
                  <li>
                    <a href="{$url}&amp;count=20" class="{if (cl:amount = '20') then 'is-active' else ''}" aria-pressed="{if (cl:amount = '20') then 'true' else 'false'}" role="button"><!-- 1.25 naar 1.35: aangepast -->
                      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
                        <xsl:attribute name="id" select="cl:id"/>
                      </xsl:if>
                      <span class="visually-hidden">Toon </span>20<span class="visually-hidden"> resultaten per pagina</span>
                    </a>
                  </li>
                  <li>
                    <a href="{$url}&amp;count=50" class="{if (cl:amount = '50') then 'is-active' else ''}" aria-pressed="{if (cl:amount = '50') then 'true' else 'false'}" role="button"><!-- 1.25 naar 1.35: aangepast -->
                      <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
                        <xsl:attribute name="id" select="cl:id"/>
                      </xsl:if>
                      <span class="visually-hidden">Toon </span>50<span class="visually-hidden"> resultaten per pagina</span>
                    </a>
                  </li>
                </ul>
              </xsl:for-each>  
            </div>
          </div>
        </xsl:when>
        <xsl:otherwise>
          <xsl:if test="cl:options/cl:option">
            <xsl:sequence select="$output"/>  
          </xsl:if>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>
  
  <xsl:template match="cl:options/cl:option" mode="sort">
    <li>
      <xsl:variable name="url" select="cl:reset-url(cl:link)"/>
      <a href="{$url}" class="{if (cl:class) then cl:class else 'sort--ascending'} {cl:active}" aria-pressed="{if (cl:active = 'is-active') then 'true' else 'false'}">
        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
          <xsl:attribute name="id" select="cl:id"/>
        </xsl:if>
        <span class="visually-hidden">Toon </span><xsl:value-of select="cl:label"/><span class="visually-hidden"> resultaten per pagina</span><!-- 1.25 naar 1.35: aangepast, was eerder nodig -->
      </a>
    </li>
  </xsl:template>
  
  <xsl:function name="cl:reset-url" as="xs:string">
    <xsl:param name="url" as="xs:string"/>
    <xsl:variable name="uri-parts" select="cl:analyze-uri($url)"/>
    <xsl:variable name="del-params" select="map:merge((map:entry('page', '*'),map:entry('count', '*')))" as="map(xs:string, xs:string)"/>
    <xsl:variable name="add-params" select="map:merge((map:entry('page', '1')))" as="map(xs:string, xs:string)"/>
    <xsl:value-of select="utils:replace-in-url($uri-parts/self::req:parameter,$uri-parts/self::path,$add-params,$del-params,())"/>
  </xsl:function>
  
  <xsl:function name="cl:analyze-uri" as="element()*">
    <xsl:param name="url" as="xs:string"/>
    <xsl:analyze-string select="$url" regex="^(.*?)(\?(.*?))?$">
      <xsl:matching-substring>
        <path>{regex-group(1)}</path>
        <parms>{regex-group(3)}</parms>
        <xsl:for-each select="tokenize(regex-group(3),'&amp;')[normalize-space(.)]">
          <xsl:variable name="nv" select="tokenize(.,'=')"/>
          <req:parameter name="{$nv[1]}">
            <req:value>{utils:decode-uri($nv[2])}</req:value><!-- #115061 (moet decoden) -->
          </req:parameter>
        </xsl:for-each>
      </xsl:matching-substring>
    </xsl:analyze-string>  
  </xsl:function>
  
</xsl:stylesheet>
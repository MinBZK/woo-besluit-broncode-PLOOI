<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY laquo "&#171;">
  <!ENTITY raquo "&#187;">
]>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:pagination">
    <div class="pagination">
      <div class="pagination__index">
        <ul>
          <xsl:apply-templates select="*"/>
        </ul>
      </div>
      <!-- TODO
      {{#if pageFilter}}
      <div class="pagination__filter">
        <div class="form__element">
          <label for="select - -1">{{ filterLabel }}</label>
          <select id="select - -1">
            {{#each filterOptions}}
            <option value="{{this}}">{{this}}</option>
            {{/each}}
          </select>
        </div>
      </div>
      {{/if}}
      -->
    </div>
  </xsl:template>
  
  <xsl:template match="cl:pagination/cl:previous-page">
    <li class="prev">
      <a href="{cl:path}">
        <span class="">&laquo;</span>
      </a>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:pagination/cl:page|cl:pagination/cl:last-page">
    <li>
      <a href="{cl:path}">
        <xsl:value-of select="cl:label"/>
      </a>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:pagination/cl:active-page">
    <li class="active">
      <xsl:value-of select="cl:label"/>
    </li>
  </xsl:template>
  
  <xsl:template match="cl:pagination/cl:ellipsis">
    <li>...</li>
  </xsl:template>
  
  <xsl:template match="cl:pagination/cl:next-page">
    <li class="next">
      <a href="{cl:path}">
        <span class="">&raquo;</span>
      </a>
    </li>
  </xsl:template>
  
</xsl:stylesheet>
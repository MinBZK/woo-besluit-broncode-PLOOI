<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:tooltip">
    <span class="tooltip {cl:modifier}" data-decorator="tooltip">
      <button type="button" aria-label="{cl:label}" aria-describedby="tt{if (cl:id) then cl:id else generate-id()}" aria-expanded="false" class="tooltip__trigger">
        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
          <xsl:attribute name="id" select="cl:id"/>
        </xsl:if>
        <xsl:value-of select="cl:label"/>
      </button>
      <span role="tooltip" id="tt{if (cl:id) then cl:id else generate-id()}" class="tooltip__content">
        <xsl:value-of select="cl:help-text"/>
      </span>
    </span>
  </xsl:template>
  
</xsl:stylesheet>
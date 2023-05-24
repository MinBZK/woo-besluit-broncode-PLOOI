<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  expand-text="yes"
  version="3.0">
  
  <xsl:template match="cl:copydata">
    <div class="copydata " data-decorator="init-copydata" data-config="{{
      &quot;triggerLabel&quot;: &quot;{cl:triggerLabel}&quot;,
      &quot;triggerCopiedlabel&quot;: &quot;{cl:triggerLabelCopied}&quot;,
      &quot;triggerClass&quot;: &quot;{cl:triggerClassModifier}&quot;
      }}">
      <p class="copydata__datafield js-copydata__datafield">
        <!-- De spec (component-library/components/detail/copydata.html) zegt dit, dus de XSLT-vertaling is een long shot:
        {{ url }}
        {{!-\- <a href="{{url}}">{{ link }}</a> -\-}}
        -->
        <xsl:choose>
          <xsl:when test="cl:link">
            <a href="{cl:url}"><xsl:copy-of select="cl:link/node()"/></a>
          </xsl:when>
          <xsl:otherwise><xsl:copy-of select="cl:url/node()"/></xsl:otherwise>
          <!-- TODO kan de url misschien markup bevatten, dus bijv. <a href=...>? Dat verklaart het rare {{ url }} uit de spec. -->
        </xsl:choose>
      </p>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  expand-text="yes"
  version="3.0">

  <!-- toegevoegd tbv CVDR debug weergave; niet in Hulsscher design -->

  <xsl:template match="cl:alert">
    <div class="alert {cl:modifier} {if (cl:fixed eq 'true') then 'show' else ''}" role="alert">
      <xsl:if test="cl:heading">
        <h2 class="alert__heading"><xsl:copy-of select="cl:heading/node()"/></h2>
      </xsl:if>
      <div class="alert__inner">
        <xsl:copy-of select="cl:text/node()" copy-namespaces="no"/>

        <xsl:if test="cl:fixed eq 'true'">
          <button class="alert__remove" data-handler="close-alert"><span class="visually-hidden">Sluiten</span></button>
        </xsl:if>
      </div>
    </div>
  </xsl:template>

</xsl:stylesheet>
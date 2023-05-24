<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:date-selection">
    <div data-decorator="init-form-conditionals">
      <div id="citem-1" role="region" class="js-form-conditionals__citem">
        <div class="form__row">
          <xsl:apply-templates select="cl:select-custom"/><!-- {{ render "@select-custom" dateselectionType merge=true }} -->
        </div>
      </div>
      <div id="citem-2" role="region" class="js-form-conditionals__citem">
        <xsl:if test="cl:additional-select"><!-- toegevoegd 1.9.35 -> 1.9.38 -->
          <div class="form__row">
            <cl:select-custom>
              <xsl:apply-templates select="cl:additional-select/node()"/>
            </cl:select-custom>
          </div>
        </xsl:if>
        <div class="form__row">
          <xsl:apply-templates select="cl:datepicker[1]"/>
        </div>
      </div>
      <div id="citem-3" role="region" class="js-form-conditionals__citem">
        <xsl:if test="cl:additional-select"><!-- toegevoegd 1.9.35 -> 1.9.38 -->
          <div class="form__row">
            <cl:select-custom>
              <xsl:apply-templates select="cl:additional-select/node()"/>
            </cl:select-custom>
          </div>
        </xsl:if>
        <xsl:apply-templates select="cl:datepicker[2]">
          <xsl:with-param name="narrow" select="cl:layout = 'narrow'"/>
        </xsl:apply-templates>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>

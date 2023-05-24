<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <!-- Let op, nog niet als component in 2.00 van blauwe team --> 
  
  <xsl:template match="cl:question-explanation">
    <div class="question-explanation">
      <a href="#question-explanation-{cl:id}" class="question-explanation__link" id="question-explanation-link-{cl:id}" data-handler="toggle-explanation">
        <xsl:value-of select="cl:text"/>
      </a>
      <div class="question-explanation__content" id="question-explanation-{cl:id}" data-decorator="hide-self">
        <xsl:apply-templates select="cl:help-text"/>
        <a href="#question-explanation-link-{cl:id}" class="question-explanation__close" data-handler="close-explanation">
          Sluiten
        </a>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
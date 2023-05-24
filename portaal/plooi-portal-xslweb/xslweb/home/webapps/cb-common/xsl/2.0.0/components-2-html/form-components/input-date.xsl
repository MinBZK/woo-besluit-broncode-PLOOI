<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:input-date">
    <div class="form__element">
      <label for="input--{cl:name}">
        <xsl:value-of select="cl:label"/>
      </label>
      <xsl:if test="cl:field-instruction">
        <small>
          <xsl:value-of select="cl:field-instruction"/>
        </small>
      </xsl:if>
      <input type="date" class="input {cl:modifier}" id="input--{cl:name}" placeholder="{cl:place-holder}" />
    </div>
  </xsl:template>
  
</xsl:stylesheet>
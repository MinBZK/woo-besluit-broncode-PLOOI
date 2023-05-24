<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:template match="cl:modal">
    <xsl:if test="cl:open-button">
      <button type="button" data-handler="open-modal" data-modal="{if (cl:modal-id) then cl:modal-id else 'modal-' || generate-id()}" class="button {cl:button-modifier}">
        <xsl:value-of select="cl:open-button"/>
      </button>
    </xsl:if>
    <div id="{if (cl:modal-id) then cl:modal-id else 'modal-' || generate-id()}" class="modal {cl:modifier}{if (cl:init-closed = 'true') then ' modal--off-screen' else ()}" role="alert">
      <xsl:if test="cl:init-closed = 'true'">
        <xsl:attribute name="hidden">hidden</xsl:attribute>
      </xsl:if>
      <div class="modal__inner">
        <div class="modal__content">
          <xsl:apply-templates select="cl:content/node()" mode="copy"/>
        </div>
        <button type="button" data-handler="close-modal" class="modal__close">
          <span class="visually-hidden">
            <xsl:value-of select="cl:close-button"/>
          </span>
        </button>
      </div>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
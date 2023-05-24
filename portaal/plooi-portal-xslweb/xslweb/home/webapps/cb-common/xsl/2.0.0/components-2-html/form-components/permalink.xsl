<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <!-- AL: 
    - nog niet als component opgenomen in CB van blauwe team 1.9.26
    - cl:copydata embedded, dus deze component roept andere component aan.
  -->
  
  <xsl:template match="cl:permalink">
      <div id="modal-{cl:id}" class="modal modal--off-screen" hidden="hidden" role="alert" tabindex="0">
        <div class="modal__inner">
          <div class="modal__content">
            <h2>
              <xsl:value-of select="cl:modal-title"/><!-- e.g. Permanente link -->
            </h2>     
            <h3>Kopieer de link naar uw clipboard </h3>
            <div class="row">
              <xsl:apply-templates select="cl:copydata"/>
            </div>
            <xsl:if test="cl:linktool-url">
              <h3>Verfijn in Linktool </h3>
              <p>Ga naar de
                <a href="{cl:linktool-url}"
                  id="aLinktoolurl" rel="external" target="_blank">Linktool</a>
              </p>
            </xsl:if>
          </div>
          <button type="button" data-handler="close-modal" class="modal__close">
            <span class="visually-hidden">sluiten</span>
          </button>
        </div>
      </div>
    
  </xsl:template>
  
<xsl:template match="cl:permalinks">
    <div id="modal-{cl:id}" class="modal modal--off-screen" hidden="hidden" role="alert" tabindex="0">
      <div class="modal__inner">
        <div class="modal__content">
          <h2>
            <xsl:value-of select="cl:modal-title"/><!-- e.g. Permanente link -->
          </h2>
          <xsl:apply-templates select="cl:permalink"/>
        </div>
        <button type="button" data-handler="close-modal" class="modal__close">
          <span class="visually-hidden">sluiten</span>
        </button>
      </div>
    </div>
    
  </xsl:template>

  <xsl:template match="cl:permalinks/cl:permalink" priority="2">
    <h3><xsl:value-of select="cl:modal-subtitle"/></h3>
    <div class="row">
      <xsl:apply-templates select="cl:copydata"/>
    </div>
  </xsl:template>
  
</xsl:stylesheet>
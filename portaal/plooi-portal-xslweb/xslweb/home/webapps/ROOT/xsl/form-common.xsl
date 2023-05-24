<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:idx="https://koop.overheid.nl/namespaces/index"
  xmlns:i18n="https://koop.overheid.nl/namespaces/i18n"
  xmlns:facet="http://docs.oasis-open.org/ns/search-ws/facetedResults"
  xmlns:search="https://koop.overheid.nl/namespaces/search"
  xmlns:functx="http://www.functx.com"
  xmlns:local="urn:local"
  expand-text="true"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:import href="periodes.xsl"/>

  <xsl:variable name="dollar" select="'$'" as="xs:string"/>

  <xsl:template name="fieldset-datum-beschikbaar">
    <div data-decorator="init-form-conditionals">
      <fieldset id="citem-1" class="js-form-conditionals__citem">
        <legend>
          <xsl:text>Datum beschikbaar</xsl:text>
          <!--
               <cl:tooltip>
               <cl:label>Tooltip</cl:label>
               <cl:help-text>... Tooltip tekst ...</cl:help-text>
               </cl:tooltip>
          -->
        </legend>
        <div class="form__row form__row--medium">
          <xsl:variable name="datumrange" select="$query-params[@name = 'datumrange']/req:value" as="xs:string?"/>
          <xsl:variable name="gekozen-periode" select="$query-params[@name = ('datumBeschikbaarVanaf', 'datumBeschikbaarTot')]/req:value"/>
          <cl:input-radio>
            <cl:name>datumrange</cl:name>
            <cl:options>
              <cl:option>
                <cl:text>Alle</cl:text>
                <cl:value/>
                <xsl:if test="not(normalize-space($datumrange) or $gekozen-periode) or ($datumrange = 'alle')">
                  <cl:checked>true</cl:checked>
                </xsl:if>
              </cl:option>
              <xsl:for-each select="$periodes">
                <cl:option>
                  <cl:text>{label}</cl:text>
                  <cl:value>{id}</cl:value>
                  <xsl:if test="$datumrange = id">
                    <cl:checked>true</cl:checked>
                  </xsl:if>
                </cl:option>
              </xsl:for-each>
              <cl:option>
                <cl:linkedto>2</cl:linkedto>
                <cl:text>Zelf een periode kiezen</cl:text>
                <cl:value/>
                <xsl:if test="$gekozen-periode">
                  <cl:checked>true</cl:checked>
                </xsl:if>
              </cl:option>
            </cl:options>
          </cl:input-radio>
        </div>
      </fieldset>

      <fieldset id="citem-2" class="js-form-conditionals__citem">
        <div>
          <label>Startdatum</label>
          <xsl:variable name="id" as="xs:string">datumbeschikbaarvanaf</xsl:variable>
          <xsl:variable name="form-field" select="idx:get-form-field($id)" as="xs:string"/>
          <cl:datepicker>
            <cl:id>
              <xsl:value-of select="$id"/>
            </cl:id>
            <cl:name>
              <xsl:value-of select="$form-field"/>
            </cl:name>
            <cl:value>
              <xsl:value-of select="$query-params[@name = $form-field]/req:value"/>
            </cl:value>
            <cl:place-holder>Bijv: 14-02-2015</cl:place-holder>
          </cl:datepicker>
        </div>
        <div class="form__row">
          <label>Einddatum</label>
          <xsl:variable name="id" as="xs:string">datumbeschikbaartot</xsl:variable>
          <xsl:variable name="form-field" select="idx:get-form-field($id)" as="xs:string"/>
          <cl:datepicker>
            <cl:id>
              <xsl:value-of select="$id"/>
            </cl:id>
            <cl:name>
              <xsl:value-of select="$form-field"/>
            </cl:name>
            <cl:value>
              <xsl:value-of select="$query-params[@name = $form-field]/req:value"/>
            </cl:value>
            <cl:place-holder>Bijv: 14-02-2015</cl:place-holder>
          </cl:datepicker>
        </div>
      </fieldset>
    </div>
  </xsl:template>

  <xsl:template name="fieldset-informatiesoort">
    <xsl:param name="modifier" as="xs:string?"/>
    <xsl:param name="facet-mode" as="xs:boolean?"/>
    <xsl:param name="terms" as="element(facet:term)*"/>
    <xsl:param name="trigger-submit" select="false()" as="xs:boolean"/>
    <fieldset>
      <legend>
        <xsl:text>Documentsoort </xsl:text>
        <!--
             <cl:tooltip>
             <cl:label>Tooltip</cl:label>
             <cl:help-text>... Tooltip tekst ...</cl:help-text>
             </cl:tooltip>
        -->
      </legend>
      <xsl:variable name="form-field" select="idx:get-form-field('informatiesoort')" as="xs:string"/>
      <xsl:variable name="selected-informatiesoorten" select="$query-params[@name = $form-field]/req:value" as="xs:string*"/>
      <cl:subselection>
        <cl:id>informatiesoort</cl:id>
        <cl:name>
          <xsl:value-of select="$form-field"/>
        </cl:name>
        <cl:modal-title>Selecteer documentsoort(en)</cl:modal-title>
        <cl:link-text>Selecteer documentsoort(en)</cl:link-text>
        <cl:link-text-active>Selecteer documentsoort(en)</cl:link-text-active>
        <cl:type>span</cl:type>
        <xsl:choose>
          <xsl:when test="$facet-mode">
            <cl:label-close-bottom>Opnieuw zoeken</cl:label-close-bottom>
            <cl:label-close-top>Opnieuw zoeken</cl:label-close-top>
            <cl:button-submit>true</cl:button-submit>
            <cl:hide-close>true</cl:hide-close>
          </xsl:when>
          <xsl:otherwise>
            <cl:label-close-bottom>Sluiten</cl:label-close-bottom>
            <cl:label-close-top>Sluiten</cl:label-close-top>
          </xsl:otherwise>
        </xsl:choose>
        <cl:search-filter>true</cl:search-filter>
        <cl:place-holder>Type hier uw documentsoort</cl:place-holder>
        <cl:multi-level>true</cl:multi-level>
        <xsl:if test="$trigger-submit">
          <cl:trigger-submit>true</cl:trigger-submit>
        </xsl:if>
        <cl:options>
          <xsl:variable name="valuelists" select="search:get-valuelists($valuelist-label-type)" as="document-node()*"/>
          <xsl:apply-templates select="$valuelists/*/waarden/*[not(ends-with(code, '_onbekend'))]">
            <xsl:sort select="@sort"/>
            <xsl:with-param name="facet-mode" select="$facet-mode" as="xs:boolean?"/>
            <xsl:with-param name="selected-items" select="$selected-informatiesoorten" as="xs:string*"/>
            <xsl:with-param name="terms" select="$terms" as="element(facet:term)*"/>
          </xsl:apply-templates>
        </cl:options>
        <cl:max-show>4</cl:max-show>
      </cl:subselection>
    </fieldset>
  </xsl:template>

  <xsl:template name="fieldset-thema">
    <xsl:param name="modifier" as="xs:string?"/>
    <xsl:param name="facet-mode" as="xs:boolean?"/>
    <xsl:param name="terms" as="element(facet:term)*"/>
    <xsl:param name="trigger-submit" select="false()" as="xs:boolean"/>
    <fieldset>
      <legend>
        <xsl:text>Thema</xsl:text>
        <!--
             <cl:tooltip>
             <cl:label>Tooltip</cl:label>
             <cl:help-text>... Tooltip tekst ...</cl:help-text>
             </cl:tooltip>
        -->
      </legend>
      <xsl:variable name="form-field" select="idx:get-form-field('thema')" as="xs:string"/>
      <xsl:variable name="selected-themas" select="$query-params[@name = $form-field]/req:value" as="xs:string*"/>
      <cl:subselection>
        <cl:id>thema</cl:id>
        <cl:name>
          <xsl:value-of select="$form-field"/>
        </cl:name>
        <cl:modal-title>Selecteer thema(s)</cl:modal-title>
        <cl:link-text>Selecteer thema(s)</cl:link-text>
        <cl:link-text-active>Selecteer thema(s)</cl:link-text-active>
        <cl:type>span</cl:type>
        <xsl:choose>
          <xsl:when test="$facet-mode">
            <cl:label-close-bottom>Opnieuw zoeken</cl:label-close-bottom>
            <cl:label-close-top>Opnieuw zoeken</cl:label-close-top>
            <cl:button-submit>true</cl:button-submit>
            <cl:hide-close>true</cl:hide-close>
          </xsl:when>
          <xsl:otherwise>
            <cl:label-close-bottom>Sluiten</cl:label-close-bottom>
            <cl:label-close-top>Sluiten</cl:label-close-top>
          </xsl:otherwise>
        </xsl:choose>
        <cl:search-filter>true</cl:search-filter>
        <cl:place-holder>Type hier uw thema</cl:place-holder>
        <cl:multi-level>true</cl:multi-level>
        <xsl:if test="$trigger-submit">
          <cl:trigger-submit>true</cl:trigger-submit>
        </xsl:if>
        <cl:options>
          <xsl:variable name="valuelists" select="search:get-valuelists($valuelist-label-thema)" as="document-node()*"/>
          <xsl:apply-templates select="$valuelists/*/waarden/*[not(ends-with(code, '_onbekend'))]">
            <xsl:sort select="@sort"/>
            <xsl:with-param name="facet-mode" select="$facet-mode" as="xs:boolean?"/>
            <xsl:with-param name="selected-items" select="$selected-themas" as="xs:string*"/>
            <xsl:with-param name="terms" select="$terms" as="element(facet:term)*"/>
          </xsl:apply-templates>
        </cl:options>
        <cl:max-show>4</cl:max-show>
      </cl:subselection>
    </fieldset>
  </xsl:template>

  <xsl:template name="fieldset-organisatie">
    <xsl:param name="modifier" as="xs:string?"/>
    <xsl:param name="facet-mode" as="xs:boolean?"/>
    <xsl:param name="terms" as="element(facet:term)*"/>
    <xsl:param name="trigger-submit" select="false()" as="xs:boolean"/>
    <fieldset>
      <legend>
        <xsl:text>Organisatie</xsl:text>
        <!--
             <cl:tooltip>
             <cl:label>Tooltip</cl:label>
             <cl:help-text>... Tooltip tekst ...</cl:help-text>
             </cl:tooltip>
        -->
      </legend>
      <xsl:variable name="form-field" select="idx:get-form-field('organisatie')" as="xs:string"/>
      <xsl:variable name="selected-organisaties" select="$query-params[@name = $form-field]/req:value" as="xs:string*"/>
      <cl:subselection>
        <cl:id>organisatie</cl:id>
        <cl:name>
          <xsl:value-of select="$form-field"/>
        </cl:name>
        <cl:modal-title>Selecteer organisatie(s)</cl:modal-title>
        <cl:link-text>Selecteer organisatie(s)</cl:link-text>
        <cl:link-text-active>Selecteer organisatie(s)</cl:link-text-active>
        <cl:type>span</cl:type>
        <xsl:choose>
          <xsl:when test="$facet-mode">
            <cl:label-close-bottom>Opnieuw zoeken</cl:label-close-bottom>
            <cl:label-close-top>Opnieuw zoeken</cl:label-close-top>
            <cl:button-submit>true</cl:button-submit>
            <cl:hide-close>true</cl:hide-close>
          </xsl:when>
          <xsl:otherwise>
            <cl:label-close-bottom>Sluiten</cl:label-close-bottom>
            <cl:label-close-top>Sluiten</cl:label-close-top>
          </xsl:otherwise>
        </xsl:choose>
        <cl:search-filter>true</cl:search-filter>
        <cl:place-holder>Type hier uw organisatie</cl:place-holder>
        <cl:multi-level>false</cl:multi-level>
        <xsl:if test="$trigger-submit">
          <cl:trigger-submit>true</cl:trigger-submit>
        </xsl:if>
        <cl:options>
          <xsl:variable name="valuelists" select="search:get-valuelists($valuelist-labels-organisation)" as="document-node()*"/>
          <xsl:apply-templates select="$valuelists/*/waarden/*[not(ends-with(code, '_onbekend'))]">
            <xsl:sort select="@sort"/>
            <xsl:with-param name="facet-mode" select="$facet-mode" as="xs:boolean?"/>
            <xsl:with-param name="selected-items" select="$selected-organisaties" as="xs:string*"/>
            <xsl:with-param name="terms" select="$terms" as="element(facet:term)*"/>
          </xsl:apply-templates>
        </cl:options>
        <cl:max-show>4</cl:max-show>
      </cl:subselection>
    </fieldset>
  </xsl:template>

  <xsl:template match="sectie | waarde[waarden]">
    <xsl:param name="facet-mode" as="xs:boolean?"/>
    <xsl:param name="selected-items" as="xs:string*"/>
    <xsl:param name="terms" as="element(facet:term)*"/>
    <cl:group>
      <cl:title>
        <xsl:value-of select="functx:capitalize-first(omschrijving)"/>
      </cl:title>
      <cl:options>
        <xsl:apply-templates select="waarden/*[not(ends-with(code, '_onbekend'))]">
          <xsl:sort select="@sort"/>
          <xsl:with-param name="facet-mode" select="$facet-mode" as="xs:boolean?"/>
          <xsl:with-param name="selected-items" select="$selected-items" as="xs:string*"/>
          <xsl:with-param name="terms" select="$terms" as="element(facet:term)*"/>
        </xsl:apply-templates>
      </cl:options>
    </cl:group>
  </xsl:template>

  <xsl:template match="waarde">
    <xsl:param name="facet-mode" as="xs:boolean?"/>
    <xsl:param name="selected-items" as="xs:string*"/>
    <xsl:param name="terms" as="element(facet:term)*"/>

    <xsl:variable name="code" select="code" as="xs:string*"/>
    <xsl:variable name="all-codes" select="($code, for $c in waarden//waarde/code return xs:string($c))" as="xs:string*"/>
    <xsl:variable name="sum-facet-count" select="sum(for $count in $terms[facet:actualTerm = $all-codes]/facet:count return xs:integer($count))" as="xs:integer"/>
    <xsl:if test="not($facet-mode) and $sum-facet-count gt 0">
      <cl:option>
        <cl:id>
          <xsl:value-of select="generate-id()"/>
        </cl:id>
        <cl:value>
          <xsl:value-of select="code"/>
        </cl:value>
        <cl:data-value>
          <xsl:value-of select="functx:capitalize-first(omschrijving)"/>
        </cl:data-value>
        <cl:title>
          <xsl:value-of select="functx:capitalize-first(omschrijving) || xs:string(if ($terms and not(.//waarde)) then (' (' || local:format-number($sum-facet-count) || ')') else ())"/>
        </cl:title>
        <xsl:if test="code = $selected-items">
          <cl:checked>true</cl:checked>
        </xsl:if>
        <xsl:if test="waarden">
          <cl:options>
            <xsl:apply-templates select="waarden/*[not(ends-with(code, '_onbekend'))]">
              <xsl:sort select="@sort"/>
              <xsl:with-param name="facet-mode" select="$facet-mode" as="xs:boolean?"/>
              <xsl:with-param name="selected-items" select="$selected-items" as="xs:string*"/>
              <xsl:with-param name="terms" select="$terms" as="element(facet:term)*"/>
            </xsl:apply-templates>
          </cl:options>
        </xsl:if>
      </cl:option>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
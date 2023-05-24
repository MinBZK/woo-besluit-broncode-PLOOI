<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:template match="cl:subselection"><!-- 1.25 naar 1.35: aangepast, flinke ingreep, niet zeker of dit compleet is --><!-- TODO testen!! -->
    <div class="subselection {if (cl:modifier) then ('subselection--' || cl:modifier) else ''}" data-decorator="init-form-subselection {if (cl:button-resetform) then 'init-formreset' else ''}">
      <xsl:attribute name="data-config">{ "type": "<xsl:value-of select="cl:type"/>"<xsl:if test="cl:link-text-active">, "triggerOnChangeText": "<xsl:value-of select="cl:link-text-active"/>"</xsl:if><xsl:if test="cl:max-show">, "maxShow": "<xsl:value-of select="cl:max-show"/>"</xsl:if><xsl:if test="cl:trigger-submit">, "triggerSubmit": true</xsl:if> }</xsl:attribute>
      <div class="subselection__summary {if (cl:required) then 'required' else ''}"/>
      <div id="modal-{cl:id}" class="modal modal--fixedpane modal--off-screen" hidden="hidden" role="alert">
        <div class="modal__inner">
          <div class="modal__content">
            <h2>
              <xsl:value-of select="cl:modal-title"/>
            </h2>
            <xsl:variable name="ul" as="element()*">
              <xsl:variable name="output" as="element()*">
                <p class="form__label">
                  <xsl:value-of select="cl:options-label"/>
                </p>
                <xsl:choose>
                  <xsl:when test="cl:options/(cl:option|cl:group)">
                    <xsl:apply-templates select="cl:options">
                      <xsl:with-param name="radio" select="cl:radio" as="xs:string?" tunnel="yes"/>
                      <xsl:with-param name="name" select="cl:name" as="xs:string" tunnel="yes"/>
                    </xsl:apply-templates>
                  </xsl:when>
                  <xsl:otherwise>
                    <p>
                      <xsl:value-of select="cl:no-options-label"/>
                    </p>
                  </xsl:otherwise>
                </xsl:choose>

                <p class="form__label">
                  <xsl:value-of select="cl:options2-label"/>
                </p>
                <xsl:if test="cl:options2">
                  <xsl:apply-templates select="cl:options2">
                    <xsl:with-param name="radio" select="cl:radio" as="xs:string?" tunnel="yes"/>
                    <xsl:with-param name="name" select="cl:name" as="xs:string" tunnel="yes"/>
                  </xsl:apply-templates>
                </xsl:if>
              </xsl:variable>
              <xsl:choose>
                <xsl:when test="cl:radio = 'true'">
                  <div class="" data-decorator="init-formreset">
                    <p>
                      <a href="#" class="icon icon--refresh formreset-resetlink">
                        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
                          <xsl:attribute name="id" select="cl:id"/>
                        </xsl:if>
                        <xsl:value-of select="if (cl:label-reset-choice) then cl:label-reset-choice else 'Reset keuze'"/>
                      </a>
                    </p>
                    <xsl:sequence select="$output"/>
                  </div>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:sequence select="$output"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <xsl:choose>
              <xsl:when test="cl:search-filter">
                <div class="" data-decorator="init-filter-results">
                  <div class="form__row form__row--medium">
                    <label class="form__label form__label--accent visually-hidden" for="filter-id--{cl:id}">
                       <xsl:value-of select="cl:label"/>
                    </label>
                    <div class="relative">
                      <input type="text" aria-controls="resultfilter-{cl:id}" name="filter-id--{cl:id}" id="filter-id--{cl:id}" class="js-filterresults__input input-text input-text--searchmedium" data-decorator="toggle-remove-visibility">
                        <xsl:attribute name="placeholder">
                          <xsl:choose>
                            <xsl:when test="cl:place-holder">
                              <xsl:value-of select="cl:place-holder"/>
                            </xsl:when>
                            <xsl:otherwise>Typ hier uw organisatie</xsl:otherwise>
                          </xsl:choose>
                        </xsl:attribute>
                      </input>
                      <a href="#" class="input-text__remove invisible" data-handler="empty-input">
                        <xsl:if test="cl:remove-id"><!-- upgrade 1.9.38 - 1.9.40 -->
                          <xsl:attribute name="id" select="cl:remove-id"/>
                        </xsl:if>
                        <span class="visually-hidden">Wis waarde</span>
                      </a>
                    </div>
                  </div>
                  <xsl:if test="cl:controls">
                    <div class="form__row">
                      <button class="button js-filterresults__btn-selectall" data-label="{cl:controls/cl:select-all-label}">
                        <xsl:if test="cl:id"><!-- upgrade 1.9.38 - 1.9.40 -->
                          <xsl:attribute name="id" select="cl:id"/>
                        </xsl:if>
                        <xsl:value-of select="cl:controls/cl:select-all-label"/>
                      </button>
                    </div>
                  </xsl:if>
                  <xsl:if test="cl:select-all-button">
                    <button class="button js-filterresults__btn-selectall" data-label="{cl:select-all-button}">
                      <xsl:value-of select="cl:select-all-button"/>
                    </button>
                  </xsl:if>
                  <fieldset class="js-filterresults__results" role="region" id="resultfilter-{cl:id}" aria-live="polite">
                    <xsl:sequence select="$ul"/>
                  </fieldset>
                  <legend class="visually-hidden">
                    <xsl:value-of select="cl:modal-title"/>
                  </legend>
                </div>
              </xsl:when>
              <xsl:when test="cl:select-all">
                <!-- TODO bij select-all worden alle verdragen aan-/uitgevinkt als je het item met "alles" verandert. Zouden we ze niet beter kunnen disablen? -->
                <div>
                  <xsl:if test="cl:select-all/(@init, 'true')[1] eq 'true'">
                    <xsl:attribute name="data-decorator">init-selectall</xsl:attribute>
                  </xsl:if>
                  <div class="input-checkbox u-margin--m">
                    <input class="checkbox__input js-checkbox-master" type="checkbox" id="{cl:select-all-id}" name="{cl:name}" value="{cl:value-if-all}"/>
                    <label class="checkbox__label" for="{cl:select-all-id}">
                      <!-- TODO cl:select-all and cl:select-all-prompt could be one and the same parameter, with a better name. -->
                      <strong><xsl:apply-templates select="cl:select-all-prompt/node()"/></strong>
                    </label>
                  </div>
                  <xsl:sequence select="$ul"/>
                </div>
              </xsl:when>
              <xsl:when test="cl:select-all-button">
                <button class="button js-filterresults__btn-selectall" data-label="{cl:select-all-button}">
                  <xsl:value-of select="cl:select-all-button"/>
                </button>
                <xsl:sequence select="$ul"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:sequence select="$ul"/>
              </xsl:otherwise>
            </xsl:choose>
            <div class="modal__buttonpane">
              <xsl:if test="cl:button-reset = 'true'">
                <button class="icon icon--refresh formreset-resetlink">Reset formulier</button>
              </xsl:if>
              <xsl:choose>
                <xsl:when test="cl:button-submit = 'true'">
                  <button type="submit" class="button button--primary">
                    <xsl:value-of select="if (cl:label-close-bottom) then cl:label-close-bottom else 'Kiezen'"/><!-- TODO is dit de {{buttonSubmitLabel}}? -->
                  </button>
                </xsl:when>
                <xsl:otherwise>
                  <button type="button" data-handler="close-modal" class="button button--primary">
                      <xsl:value-of select="if (cl:label-close-bottom) then cl:label-close-bottom else 'Kiezen'"/>
                  </button>
                </xsl:otherwise>
              </xsl:choose>
            </div>
            <xsl:if test="not(cl:hide-close = 'true')">
              <button type="{if (cl:submit-on-close = 'true') then 'submit' else 'button'}" data-handler="close-modal" class="modal__close">
                <xsl:value-of select="if (cl:label-close-top) then cl:label-close-top else 'Sluit modaal'"/>
              </button>
            </xsl:if>
          </div>
        </div>
      </div>
      <button type="button" id="id-{cl:id}" data-handler="open-modal" data-modal="modal-{cl:id}" class="subselection__trigger">
        <xsl:value-of select="cl:link-text"/>
      </button>
    </div>
  </xsl:template>

  <xsl:template match="cl:options|cl:options2">
    <xsl:variable name="cl:subselection-multilevel" select="ancestor::cl:subselection/cl:multi-level = 'true'"/><!-- i.t.t. handlbars, niet testen op sibling options. -->
    <xsl:choose>
      <xsl:when test="$cl:subselection-multilevel">
        <fieldset class="js-filterresults__resultmother">
          <xsl:choose>
            <xsl:when test="cl:check-all">
              <legend class="visually-hidden"><xsl:value-of select="cl:title"/></legend>
              <div data-decorator="init-selectall">
                <div class="input-checkbox">
                  <input class="checkbox__input js-checkbox-master" type="checkbox" id="{cl:id}" name="selectAll" value="Alle iets"
                  />
                  <label class="checkbox__label u-nomargin" for="{cl:id}">
                    <strong><xsl:value-of select="cl:title"/></strong>
                  </label>
                  <ul class="list--unstyled list--subselection">
                    <xsl:apply-templates select="cl:option|cl:group" mode="subselection"/>
                  </ul>
                </div>
              </div>
            </xsl:when>
            <xsl:otherwise>
              <legend><xsl:value-of select="cl:title"/></legend>
              <ul class="list--unstyled list--subselection">
                <xsl:apply-templates select="cl:option|cl:group" mode="subselection"/>
              </ul>
            </xsl:otherwise>
          </xsl:choose>
        </fieldset>
      </xsl:when>
      <xsl:otherwise>
        <ul class="list--unstyled">
          <xsl:apply-templates select="cl:option|cl:group" mode="subselection"/>
        </ul>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="cl:group" mode="subselection">
    <xsl:param name="radio" as="xs:string?" tunnel="yes"/>
    <xsl:variable name="input-type" select="if ($radio = 'true') then 'radio' else 'checkbox'" as="xs:string"/>
    <li>
      <strong>
        <xsl:value-of select="cl:title"/>
      </strong>
      <xsl:apply-templates select="cl:options|cl:options2"/>
    </li>
  </xsl:template>

  <xsl:template match="cl:option" mode="subselection">
    <xsl:param name="radio" as="xs:string?" tunnel="yes"/>
    <xsl:param name="name" as="xs:string" tunnel="yes"/>
    <xsl:variable name="input-type" select="if ($radio = 'true') then 'radio' else 'checkbox'" as="xs:string"/>
    <li class=" js-filterresults__result">
      <div class="input-{$input-type}">
        <input class="{$input-type}__input" type="{$input-type}" id="option-{cl:id}" name="{$name}" value="{cl:value}">
          <xsl:if test="cl:data-value">
            <xsl:attribute name="data-value" select="string(cl:data-value)"/>
          </xsl:if>
          <xsl:if test="cl:disabled = 'true'">
            <xsl:attribute name="disabled">disabled</xsl:attribute>
          </xsl:if>
          <xsl:if test="cl:checked = 'true'">
            <xsl:attribute name="checked">checked</xsl:attribute>
          </xsl:if>
          <xsl:if test="cl:status = 'true'">
            <xsl:attribute name="checked">checked</xsl:attribute>
          </xsl:if>
          <xsl:if test="cl:linkedto">
            <xsl:attribute name="data-linkedto" select="cl:linkedto"/>
          </xsl:if>
        </input>
        <label class="{$input-type}__label" for="option-{cl:id}">
          <xsl:value-of select="cl:title"/>
        </label>
      </div>
      <xsl:apply-templates select="cl:options|cl:options2"/>
    </li>
  </xsl:template>

</xsl:stylesheet>
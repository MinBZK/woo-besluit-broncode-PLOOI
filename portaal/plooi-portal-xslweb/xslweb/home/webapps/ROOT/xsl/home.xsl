<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:req="http://www.armatiek.com/xslweb/request"
        xmlns:resp="http://www.armatiek.com/xslweb/response"
        xmlns:config="http://www.armatiek.com/xslweb/configuration"
        xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
        xmlns:search="https://koop.overheid.nl/namespaces/search"
        xmlns:sru="http://docs.oasis-open.org/ns/search-ws/sruResponse"
        xmlns:facet="http://docs.oasis-open.org/ns/search-ws/facetedResults"
        xmlns:idx="https://koop.overheid.nl/namespaces/index"
        xmlns:functx="http://www.functx.com"
        xmlns:local="urn:local"
        exclude-result-prefixes="#all"
        version="3.0">

  <xsl:import href="page.xsl"/>

  <!-- Required overrides: -->
  <xsl:template name="title">Overheid.nl | Overheidsdocumenten</xsl:template>

  <xsl:function name="cl:get-selected-nav-id" as="xs:string">
    <xsl:text>home</xsl:text>
  </xsl:function>

  <xsl:template name="breadcrumb" as="element(cl:breadcrumb)">
    <cl:breadcrumb>
      <cl:title>U bent hier:</cl:title>
      <cl:level>
        <cl:label>Home</cl:label>
      </cl:level>
    </cl:breadcrumb>
  </xsl:template>

  <xsl:template name="main">
    <div class="container" role="main" id="content">
      <div class="row row--spacer">
        <cl:jumbotron>
          <cl:heading>Openbare overheidsdocumenten</cl:heading>
          <cl:sub-heading/>
          <cl:intro>Wegwijzer naar <xsl:value-of select="xs:string(local:format-number(search:get-number-of-documents()))"/> actief openbaar gemaakte documenten</cl:intro>
          <cl:centered>true</cl:centered>
          <cl:heading-centered>true</cl:heading-centered>
          <cl:partial-block>
            <form id="searchform" method="GET" action="{$context-path || '/zoekresultaten'}" class="form form__row form__row--medium">
              <fieldset>
                <legend class="visually-hidden">Doorzoek de overheidsdocumenten</legend>
                <div class="form__row">
                  <cl:input-text>
                    <cl:modifier>input-text--searchmedium</cl:modifier>
                    <cl:name>
                      <xsl:value-of select="idx:get-form-field('text')"/>
                    </cl:name>
                    <cl:id>text</cl:id>
                    <cl:field-label>Zoekwoord</cl:field-label>
                    <cl:field-label-visible>false</cl:field-label-visible>
                    <cl:field-place-holder>Vul hier uw zoekwoord(en) in</cl:field-place-holder>
                    <cl:remove>true</cl:remove>
                    <cl:label-clear>Wis waarde</cl:label-clear>
                    <cl:autofocus>true</cl:autofocus>
                  </cl:input-text>
                </div>
              </fieldset>
              <div class="">
                <div class="columns">
                  <div class="u-order--d2 align-right form__row">
                    <button type="submit" class="form__button button button--primary button--large">Zoek documenten</button>
                  </div>
                  <div class="u-order--d1 u-nomargin">
                    <a href="{$context-path || '/uitgebreid-zoeken'}" class="cta">Uitgebreid zoeken</a><br/>
                  </div>
                </div>
              </div>
            </form>
          </cl:partial-block>
        </cl:jumbotron>
      </div>
      <div class="row row--xlarge">
        <div class="columns columns--gutter-med">
          <div class="column">
            <cl:block>
              <!--              <cl:highlight/>-->
              <cl:heading>Platform open overheidsinformatie</cl:heading>
              <cl:content>
                <p>Het platform open overheidsinformatie maakt overheidsinformatie op één centrale plaats toegankelijk. De documenten in dit portaal worden actief openbaar gemaakt onder de Wet open overheid (Woo).</p>
                <p>Een beperkte verzameling documenten is nu opgenomen op het platform. Dagelijks worden nieuwe documenten toegevoegd. Doel is om uiteindelijk alle overheidsdocumenten op één plek vindbaar te maken.</p>

                <h4>Welke documenten vindt u nu in het platform open overheidsinformatie?</h4>
                <p>U vindt documenten van overheidsorganisaties zoals gemeenten, provincies, waterschappen en de rijksoverheid. Denk daarbij aan:</p>
                <cl:list>
                  <cl:items>
                    <cl:item>Beleidsnota's van departementen waarin beleid wordt beschreven </cl:item>
                    <cl:item>Woo-verzoeken</cl:item>
                    <cl:item>Beslisnota's, documenten waarin ambtenaren alle afwegingen onder elkaar zetten om tot een beleidskeuze te komen</cl:item>
                    <cl:item>Agenda’s en verslagen vergaderingen zoals de Ministerraad</cl:item>
                    <cl:item>Contactgegevens van overheidsinstanties</cl:item>
                    <cl:item>Kamerstukken</cl:item>
                  </cl:items>
                </cl:list>
              </cl:content>
            </cl:block>


          </div>
          <div class="column">
            <cl:block>
              <cl:flexed/>
              <cl:heading>Niet gevonden wat u zocht?</cl:heading>
              <cl:content>
                <p>Er zijn nog andere portalen waar u ook documenten van de overheid kunt vinden. Dat kan via de volgende portalen:</p>
                <cl:list>
                  <cl:ordered-list>false</cl:ordered-list>
                  <cl:modifier>list--linked</cl:modifier>
                  <cl:items>
                    <cl:item><a href="https://www.officielebekendmakingen.nl/" target="_blank">Officiële bekendmakingen</a></cl:item>
                    <cl:item><a href="https://zoek.officielebekendmakingen.nl/uitgebreidzoeken/parlementair" target="_blank">Parlementaire documenten</a></cl:item>
                    <cl:item><a href="https://wetten.overheid.nl/" target="_blank">Landelijke wet- en regelgeving</a></cl:item>
                    <cl:item><a href="https://www.overheid.nl/lokale_wet_en_regelgeving" target="_blank">Lokale regelgeving</a></cl:item>
                    <cl:item><a href="https://almanak.overheid.nl/" target="_blank">Contactgegevens van overheidsorganisaties</a></cl:item>
                    <cl:item><a href="https://data.overheid.nl/" target="_blank">Dataregister van de Nederlandse overheid</a></cl:item>
                  </cl:items>
                </cl:list>
                <h4>Hulp nodig bij het zoeken?</h4>
                <p>Dan verwijzen wij u graag door naar <a href="https://www.overheid.nl/help/plooi" target="_blank">deze pagina</a></p>
              </cl:content>
            </cl:block>

          </div>
        </div>
      </div>
    </div>
  </xsl:template>

</xsl:stylesheet>

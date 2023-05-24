<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:cl="https://koop.overheid.nl/namespaces/component-library"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log" 
  xmlns:i18n="https://koop.overheid.nl/namespaces/i18n"
  exclude-result-prefixes="#all"
  version="3.0">
  
  <xsl:mode on-no-match="shallow-copy"/>
  
  <xsl:import href="../../common/i18n.xsl"/>
  <xsl:import href="../../common/utils.xsl"/>
  
  <xsl:include href="page-components/header.xsl"/>
  <xsl:include href="page-components/footer.xsl"/>
  <xsl:include href="page-components/breadcrumb.xsl"/>
  <xsl:include href="page-components/pagination.xsl"/>
  <xsl:include href="common/alert.xsl"/>
  <xsl:include href="common/block.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/figure.xsl"/>
  <xsl:include href="common/picture.xsl"/>
  <xsl:include href="common/logo.xsl"/>
  <xsl:include href="common/list.xsl"/>
  <xsl:include href="common/resultlist.xsl"/>
  <xsl:include href="common/nav.xsl"/>
  <xsl:include href="common/facets.xsl"/>
  <xsl:include href="common/sort.xsl"/>
  <xsl:include href="common/sources-list.xsl"/>
  <xsl:include href="common/steps.xsl"/>
  <xsl:include href="common/collapsible.xsl"/>
  <xsl:include href="common/to-top.xsl"/>
  <xsl:include href="common/pageactions.xsl"/>
  <xsl:include href="common/copydata.xsl"/>
  <xsl:include href="common/jumbotron.xsl"/>
  <xsl:include href="common/question-explanation.xsl"/>
  <xsl:include href="common/video.xsl"/>
  <xsl:include href="common/tabs.xsl"/>
  <xsl:include href="common/facetlinkgroup.xsl"/>
  <xsl:include href="common/modal.xsl"/>
  <xsl:include href="common/link.xsl"/>
  <xsl:include href="common/selectedfilterbar.xsl"/>
  <xsl:include href="form-components/date-selection.xsl"/>
  <xsl:include href="form-components/datepicker.xsl"/>
  <xsl:include href="form-components/input-text.xsl"/>
  <xsl:include href="form-components/input-checkbox.xsl"/>
  <xsl:include href="form-components/input-date.xsl"/>
  <xsl:include href="form-components/input-radio.xsl"/>
  <xsl:include href="form-components/input-submit.xsl"/>
  <xsl:include href="form-components/input-group.xsl"/>
  <xsl:include href="form-components/select-custom.xsl"/>
  <xsl:include href="form-components/form-selectedfilter.xsl"/>
  <xsl:include href="form-components/subselection.xsl"/><!-- BUG 106121 Hierarchie toegevoegd -->
  <xsl:include href="form-components/permalink.xsl"/>
  <xsl:include href="form-components/tooltip.xsl"/>
  <xsl:include href="form-components/selectedfilters.xsl"/>
  
  <xsl:template match="cl:*">
    <xsl:apply-templates/>
  </xsl:template>
  
  <!--
  <xsl:template name="i18n:tmx-doc" as="document-node()">
    <xsl:sequence select="document('vertaaltabel_tmx.xml')"/>
  </xsl:template>
  -->
  
  <xsl:template match="cl:*" mode="copy">
    <xsl:apply-templates select="."/>
  </xsl:template>
  
  <xsl:template match="@*|node()" mode="copy">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
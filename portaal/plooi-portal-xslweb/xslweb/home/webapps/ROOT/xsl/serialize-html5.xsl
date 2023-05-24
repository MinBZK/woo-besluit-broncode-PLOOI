<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:output method="html" version="5.0"/>

  <xsl:mode on-no-match="shallow-copy"/>

  <xsl:param name="config:piwik-code" as="xs:string?"/>

  <!-- Add Piwik code to output: -->
  <xsl:template match="head">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <xsl:if test="$config:piwik-code">
        <xsl:comment>Start Piwik PRO</xsl:comment>
        <script type="text/javascript">
          (function(window, document, dataLayerName, id) {
          function stgCreateCookie(a,b,c){var d="";if(c){var e=new Date;e.setTime(e.getTime()+24*c*60*60*1e3),d="; expires="+e.toUTCString()}document.cookie=a+"="+b+d+"; path=/"}
          var isStgDebug=(window.location.href.match("stg_debug")||document.cookie.match("stg_debug"))&amp;&amp;!window.location.href.match("stg_disable_debug");stgCreateCookie("stg_debug",isStgDebug?1:"",isStgDebug?14:-1);
          var qP=[];dataLayerName!=="dataLayer"&amp;&amp;qP.push("data_layer_name="+dataLayerName),isStgDebug&amp;&amp;qP.push("stg_debug");var qPString=qP.length&gt;0?("?"+qP.join("&amp;")):"";
          document.write('SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS'+id+'.sync.js' + qPString + '"&gt;&lt;/' + 'script&gt;');
          })(window, document, 'dataLayer', '<xsl:value-of select="$config:piwik-code"/>');
        </script>
        <xsl:comment>End Piwik PRO</xsl:comment>
      </xsl:if>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="body">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:if test="$config:piwik-code">
        <xsl:comment>Start Piwik PRO</xsl:comment>
        <script type="text/javascript">
          (function(window, document, dataLayerName, id) {
          window[dataLayerName]=window[dataLayerName]||[],window[dataLayerName].push({start:(new Date).getTime(),event:"stg.start"});var scripts=document.getElementsByTagName('script')[0],tags=document.createElement('script');
          function stgCreateCookie(a,b,c){var d="";if(c){var e=new Date;e.setTime(e.getTime()+24*c*60*60*1e3),d="; expires="+e.toUTCString()}document.cookie=a+"="+b+d+"; path=/"}
          var isStgDebug=(window.location.href.match("stg_debug")||document.cookie.match("stg_debug"))&amp;&amp;!window.location.href.match("stg_disable_debug");stgCreateCookie("stg_debug",isStgDebug?1:"",isStgDebug?14:-1);
          var qP=[];dataLayerName!=="dataLayer"&amp;&amp;qP.push("data_layer_name="+dataLayerName),isStgDebug&amp;&amp;qP.push("stg_debug");var qPString=qP.length&gt;0?("?"+qP.join("&amp;")):"";
          tags.async=!0,tags.src="SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"+id+".js"+qPString,scripts.parentNode.insertBefore(tags,scripts);
          !function(a,n,i){a[n]=a[n]||{};for(var c=0;c&lt;i.length;c++)!function(i){a[n][i]=a[n][i]||{},a[n][i].api=a[n][i].api||function(){var a=[].slice.call(arguments,0);"string"==typeof a[0]&amp;&amp;window[dataLayerName].push({event:n+"."+i+":"+a[0],parameters:[].slice.call(arguments,1)})}}(i[c])}(window,"ppms",["tm","cm"]);
          })(window, document, 'dataLayer', '<xsl:value-of select="$config:piwik-code"/>');
        </script>
        <xsl:comment>End Piwik PRO</xsl:comment>
      </xsl:if>
      <xsl:apply-templates select="node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
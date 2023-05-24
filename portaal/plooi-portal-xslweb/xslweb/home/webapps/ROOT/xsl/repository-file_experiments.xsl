<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:req="http://www.armatiek.com/xslweb/request"
  xmlns:resp="http://www.armatiek.com/xslweb/response"
  xmlns:config="http://www.armatiek.com/xslweb/configuration"
  xmlns:log="http://www.armatiek.com/xslweb/functions/log"
  xmlns:res="http://www.armatiek.com/xslweb/resource-serializer"
  xmlns:ext="http://zoekservice.overheid.nl/extensions"
  xmlns:file="http://expath.org/ns/file"
  xmlns:http="http://expath.org/ns/http-client"
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:param name="config:development-mode" as="xs:boolean"/>
  <xsl:param name="config:plooi-repos-endpoint" as="xs:string"/>
  <!--  <xsl:param name="config:plooi-repos-path" as="xs:string"/>-->

  <xsl:param name="config:plooi-repos-path" as="xs:string"/>
  <xsl:variable name="ds" select="file:dir-separator()" as="xs:string"/>

  <xsl:template match="/*">
    <resp:response status="200">
      <resp:body>
        <xsl:variable name="work" select="tokenize(req:path, '/')[3]" as="xs:string"/>
<!--  TODO this assumes there's a pdf document. Could also be a zip. Figure it out from the versies? -->
        <xsl:variable name="url" select="$config:plooi-repos-endpoint || '/' || $work || '/pdf'" as="xs:string"/>

        <xsl:if test="$config:development-mode">
          <xsl:sequence select="log:log('INFO', 'repository-url: ' || $url)"/>
        </xsl:if>
<!-- Stuff below used for experimenting with binary-serializer-->

<!--        <xsl:variable name="pdf-request" as="element(http:request)">-->
<!--          <http:request-->
<!--                  method="GET"-->
<!--                  href="{$url}"-->
<!--                  send-authorization="false">-->
<!--          </http:request>-->
<!--        </xsl:variable>-->
<!-- - error handling -->
<!-- - put the filename in the content-disposition and set the content-type right  -->
<!-- - what if we have zip files?      -->
<!-- - so now the whole file contents is read in memory? Doesn't seem like a good idea  -->
<!--        <xsl:variable name="pdf-contents" select="http:send-request($pdf-request[1])" as="item()*"/>-->
<!--        <res:binary-serialize>-->
<!--          <xsl:value-of select="xs:base64Binary($pdf-contents[2])"/>-->
<!--        </res:binary-serialize>-->

        <xsl:variable name="hashed-dir" select="ext:hashed-directory($work)" as="xs:string"/>
        <xsl:if test="$config:development-mode">
          <xsl:sequence select="log:log('INFO', 'repository-file: ' || $hashed-dir || substring-after(req:path, '/repository'))"/>
        </xsl:if>
        <xsl:sequence select="log:log('INFO', 'config:plooi-repos-path: ' || $config:plooi-repos-path)"/>
        <res:resource-serializer
                path="{translate($config:plooi-repos-path || $hashed-dir || substring-after(req:path, '/repository'), '/', $ds)}"
                content-disposition-filename=""
        />
      </resp:body>
    </resp:response>
  </xsl:template>

</xsl:stylesheet>

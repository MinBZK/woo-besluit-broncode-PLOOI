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
  exclude-result-prefixes="#all"
  version="3.0">

  <xsl:param name="config:development-mode" as="xs:boolean"/>
  <xsl:param name="config:plooi-repos-path" as="xs:string"/>

  <xsl:variable name="ds" select="file:dir-separator()" as="xs:string"/>

  <xsl:template match="/*">
    <resp:response status="200">
      <resp:body>
        <xsl:variable name="work" select="tokenize(req:path, '/')[3]" as="xs:string"/>
        <xsl:variable name="hashed-dir" select="ext:hashed-directory($work)" as="xs:string"/>
        <xsl:if test="$config:development-mode">
          <xsl:sequence select="log:log('INFO', 'repository-file: ' || $hashed-dir || substring-after(req:path, '/repository'))"/>
        </xsl:if>
        <res:resource-serializer
                path="{translate($config:plooi-repos-path || $hashed-dir || substring-after(req:path, '/repository'), '/', $ds)}"
                content-disposition-filename=""
        />
      </resp:body>
    </resp:response>
  </xsl:template>

</xsl:stylesheet>

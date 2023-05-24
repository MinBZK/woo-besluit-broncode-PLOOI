<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

    xmlns:config="http://www.armatiek.com/xslweb/configuration"
    xmlns:req="http://www.armatiek.com/xslweb/request"
    xmlns:resp="http://www.armatiek.com/xslweb/response"
    xmlns:local="urn:local"
    xmlns:http="http://expath.org/ns/http-client"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:log="http://www.armatiek.com/xslweb/functions/log"
    version="3.0">

    <xsl:param name="config:sru-beta-uri">moet-je-meegeven</xsl:param>

    <xsl:variable name="query-params" select="/*/req:parameters/req:parameter" as="element()*"/>
    <xsl:variable name="query-params-ser" select="string-join(
        for $p in $query-params return
        for $v in $p/req:value return
        $p/@name || '=' || encode-for-uri($v)
    ,'&amp;')"/>

    <xsl:variable name="forward-url" select="$config:sru-beta-uri || /*/req:context-path || /*/req:webapp-path || /*/req:path || '?' || $query-params-ser"/>

    <xsl:template match="/">
        <xsl:sequence select="log:log('INFO','SRU POST forward: ' || $forward-url)"></xsl:sequence>
        <xsl:sequence select="local:perform-request($forward-url)"/>
    </xsl:template>

    <xsl:function name="local:perform-request" as="document-node()*">
        <xsl:param name="url" as="xs:string"/>
        <xsl:try>
            <xsl:variable name="req" as="element(http:request)">
                <http:request method="GET" href="{$url}"/>
            </xsl:variable>
            <xsl:variable name="response" select="http:send-request($req)"/>
            <xsl:choose>
                <xsl:when test="xs:integer($response[1]/@status) ne 200">
                    <xsl:sequence select="error(xs:QName('local:error'), 'Unexpected HTTP status ' || $response[1]/@status)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="$response[2]/*">
                        <xsl:document>
                            <xsl:sequence select="."/>
                        </xsl:document>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:catch>
                <!-- niks -->
            </xsl:catch>
        </xsl:try>
    </xsl:function>

</xsl:stylesheet>
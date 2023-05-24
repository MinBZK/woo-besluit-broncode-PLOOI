<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"  
    xmlns:config="http://www.armatiek.com/xslweb/configuration"
    xmlns:session="http://www.armatiek.com/xslweb/session" 
    xmlns:err="http://www.w3.org/2005/xqt-errors"
    
    xmlns:testing="http://www.armatiek.nl/functions/testing"  
    
    exclude-result-prefixes="#all"
    version="3.0"
    expand-text="true">
    
    <!-- 
        Text compare uitvoeren.
        
        In developent omgeving kun je de functie aanroepen: testing:do-text-compare($html-root-element, $xml-root-element).
        In bulk is het context document de HTML5 output, en in sessie attribuut "xml-content-url" staat de URL van de XML waar de HTML5 vanuit is gegenereerd.

        Beide bronnen worden gestripped van alle markup, space-normalized, en vergeleken.
        Voor de HTML geldt: tekst bevindt zich in een element <div id=""inhoud">.
        Voor de XML geldt: tekst bevindt zich in een element met de qualified name zoals meegegeven in sessie attrribuut "xml-body-qname".
        
        Deze stylesheet levert de tekst true of false op:
        true: gelijk
        false: verschillend.
    -->
    <xsl:variable name="testing:chunk-size" select="125"/>
        
    <xsl:function name="testing:do-text-compare" as="element(compare)">
        <xsl:param name="html-root" as="element()"/>
        <xsl:param name="xml-root" as="element()"/>
        <xsl:param name="xml-url" as="xs:string"/>
        <compare>
            <xsl:try>
                <xsl:variable name="html-text" select="testing:strip-html-text($html-root)"/>
                <xsl:variable name="xml-text" select="testing:strip-xml-text($xml-root)"/>
                <xsl:choose>
                    <xsl:when test="empty($html-text)">
                        <xsl:attribute name="status">1</xsl:attribute>
                        <xsl:attribute name="explain">NO HTML</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="empty($xml-text)">
                        <xsl:attribute name="status">2</xsl:attribute>
                        <xsl:attribute name="explain">NO XML</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="empty($html-root)">
                        <xsl:attribute name="status">3</xsl:attribute>
                        <xsl:attribute name="explain">NO HTML ROOT</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="empty($xml-root)">
                        <xsl:attribute name="status">4</xsl:attribute>
                        <xsl:attribute name="explain">NO XML ROOT</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$html-text eq $xml-text">
                        <xsl:attribute name="status">0</xsl:attribute>
                        <xsl:attribute name="explain">SAME</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="status">5</xsl:attribute>
                        <xsl:attribute name="explain">DIFFERENT</xsl:attribute>
                        <xsl:attribute name="html-text" select="$html-text"/>
                        <xsl:attribute name="xml-text" select="$xml-text"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:catch>
                    <xsl:attribute name="status">9</xsl:attribute>
                    <xsl:attribute name="explain">ERROR {$xml-url || ' ' || $err:description || ' (' || $err:code || ' line: ' || $err:line-number || ', column: ' || $err:column-number || ')'}</xsl:attribute>
                </xsl:catch>
            </xsl:try>
        </compare>
    </xsl:function>
    
    <xsl:function name="testing:strip-html-text" as="xs:string">
        <xsl:param name="root" as="element()"/>
        <xsl:variable name="texts">
            <xsl:apply-templates select="$root" mode="testing:strip-html-text"/>  
        </xsl:variable>
        <xsl:value-of select="testing:normalize-space($texts)"/>
    </xsl:function>
    
    <xsl:function name="testing:strip-xml-text" as="xs:string">
        <xsl:param name="root" as="element()"/>
        <xsl:variable name="texts">
            <xsl:apply-templates select="$root" mode="testing:strip-xml-text"/>  
        </xsl:variable>
        <xsl:value-of select="testing:normalize-space($texts)"/>
    </xsl:function>
    
    <xsl:template match="*" mode="testing:strip-html-text testing:strip-xml-text">
        <xsl:apply-templates mode="#current"/>
        <xsl:text> </xsl:text>
    </xsl:template>
    
    <xsl:template match="text()" mode="testing:strip-html-text testing:strip-xml-text">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <!-- 
        Normaliseer whitespace; linefeed na vaste hoeveelheid karakters, om de zaak makkelijker te kunnen vergelijken. nonbreaking space omzetten naar space. 
    -->
    <xsl:function name="testing:normalize-space" as="xs:string">
        <xsl:param name="texts"/>
        <!-- TODO dit kleine stukje code centraliseren in een includefile of pipeline-stap? -->
        <xsl:value-of select="string-join(testing:chunks(replace(string-join($texts,' '), '(\s|&#160;)+', ' ')),'&#10;')"/>
    </xsl:function>
    
    <xsl:function name="testing:chunks" as="xs:string*">
        <xsl:param name="text" as="xs:string"/>
        <xsl:variable name="loops" select="ceiling(string-length($text) div $testing:chunk-size) cast as xs:integer" as="xs:integer"/>
        <xsl:sequence select="for $pos in (0 to $loops) return substring($text,($pos * $testing:chunk-size) + 1,$testing:chunk-size)"/>
    </xsl:function>
    
</xsl:stylesheet>
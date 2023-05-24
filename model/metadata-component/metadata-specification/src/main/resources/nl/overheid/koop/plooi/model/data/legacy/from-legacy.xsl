<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:plooi="http://standaarden.overheid.nl/plooi/terms/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:overheid="http://standaarden.overheid.nl/owms/terms/" xmlns:xhtml="http://www.w3.org/1999/xhtml" version="3.0">

    <xsl:output method="text" indent="no" omit-xml-declaration="yes" />
    <xsl:strip-space elements="*" />

    <xsl:variable name="spaces">
        <xsl:text>                |</xsl:text>
    </xsl:variable>

    <xsl:template match="//plooi">
        <xsl:call-template name="object">
            <xsl:with-param name="first">true</xsl:with-param>
            <xsl:with-param name="of">
                <xsl:call-template name="object">
                    <xsl:with-param name="key">document</xsl:with-param>
                    <xsl:with-param name="first">true</xsl:with-param>
                    <xsl:with-param name="indent">2</xsl:with-param>
                    <xsl:with-param name="of">
                        <xsl:apply-templates select="meta/owmskern/identifier" mode="keystrvalue">
                            <xsl:with-param name="key">pid</xsl:with-param>
                            <xsl:with-param name="prefix">"https://open.overheid.nl/documenten/</xsl:with-param>
                            <xsl:with-param name="first">true</xsl:with-param>
                        </xsl:apply-templates>
                        <xsl:apply-templates select="meta/owmsmantel/source" mode="keystrvalue">
                            <xsl:with-param name="key">weblocatie</xsl:with-param>
                        </xsl:apply-templates>
                        <xsl:apply-templates select="meta/owmsmantel/issued" mode="keystrvalue">
                            <xsl:with-param name="key">creatiedatum</xsl:with-param>
                        </xsl:apply-templates>
                        <xsl:apply-templates select="meta/owmsmantel/available" />
                        <xsl:apply-templates select="meta/plooiipm/verantwoordelijke" mode="resource">
                            <xsl:with-param name="key">verantwoordelijke</xsl:with-param>
                        </xsl:apply-templates>
                        <!-- Kern type and authority are ignored -->
                        <xsl:apply-templates select="meta/owmsmantel/publisher" mode="resource">
                            <xsl:with-param name="key">publisher</xsl:with-param>
                        </xsl:apply-templates>
                        <xsl:apply-templates select="meta/owmskern/creator" mode="resource">
                            <xsl:with-param name="key">opsteller</xsl:with-param>
                        </xsl:apply-templates>
                        <xsl:apply-templates select="meta/owmskern/language" mode="resource">
                            <xsl:with-param name="key">language</xsl:with-param>
                            <xsl:with-param name="id">"http://publications.europa.eu/resource/authority/language/NLD"</xsl:with-param>
                            <xsl:with-param name="label">Nederlands</xsl:with-param>
                        </xsl:apply-templates>
                        <xsl:call-template name="list">
                            <xsl:with-param name="key">onderwerpen</xsl:with-param>
                            <xsl:with-param name="of">
                                <xsl:apply-templates select="meta/owmsmantel/subject" />
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="list">
                            <xsl:with-param name="key">omschrijvingen</xsl:with-param>
                            <xsl:with-param name="of">
                                <xsl:apply-templates select="meta/owmsmantel/description" />
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="object">
                            <xsl:with-param name="key">titelcollectie</xsl:with-param>
                            <xsl:with-param name="of">
                                <xsl:apply-templates select="meta/owmskern/title" mode="keystrvalue">
                                    <xsl:with-param name="key">officieleTitel</xsl:with-param>
                                    <xsl:with-param name="first">true</xsl:with-param>
                                    <xsl:with-param name="indent">6</xsl:with-param>
                                </xsl:apply-templates>
                                <xsl:call-template name="list">
                                    <xsl:with-param name="key">alternatieveTitels</xsl:with-param>
                                    <xsl:with-param name="of">
                                        <xsl:apply-templates select="meta/owmsmantel/alternative" />
                                    </xsl:with-param>
                                    <xsl:with-param name="indent">6</xsl:with-param>
                                </xsl:call-template>
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:call-template name="object">
                            <xsl:with-param name="key">classificatiecollectie</xsl:with-param>
                            <xsl:with-param name="of">
                                <xsl:call-template name="list">
                                    <xsl:with-param name="key">documentsoorten</xsl:with-param>
                                    <xsl:with-param name="of">
                                        <xsl:apply-templates select="meta/plooiipm/informatiecategorie" mode="resource">
                                            <xsl:with-param name="first">true</xsl:with-param>
                                            <xsl:with-param name="indent">6</xsl:with-param>
                                        </xsl:apply-templates>
                                    </xsl:with-param>
                                    <xsl:with-param name="first">true</xsl:with-param>
                                    <xsl:with-param name="indent">6</xsl:with-param>
                                </xsl:call-template>
                                <xsl:call-template name="list">
                                    <xsl:with-param name="key">themas</xsl:with-param>
                                    <xsl:with-param name="of">
                                        <xsl:apply-templates select="meta/plooiipm/topthema" mode="resource">
                                            <xsl:with-param name="first">true</xsl:with-param>
                                            <xsl:with-param name="indent">6</xsl:with-param>
                                        </xsl:apply-templates>
                                    </xsl:with-param>
                                    <xsl:with-param name="indent">6</xsl:with-param>
                                </xsl:call-template>
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:apply-templates select="meta/owmsmantel/isPartOf" mode="resource">
                            <xsl:with-param name="key">isPartOf</xsl:with-param>
                        </xsl:apply-templates>
                        <xsl:call-template name="list">
                            <xsl:with-param name="key">hasParts</xsl:with-param>
                            <xsl:with-param name="of">
                                <xsl:apply-templates select="meta/owmsmantel/hasPart" mode="resource">
                                    <xsl:with-param name="first">true</xsl:with-param>
                                </xsl:apply-templates>
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:if test="meta/plooiipm/extrametadata">
                            <xsl:call-template name="list">
                                <xsl:with-param name="key">extraMetadata</xsl:with-param>
                                <xsl:with-param name="of">
                                    <xsl:call-template name="object">
                                        <xsl:with-param name="first">true</xsl:with-param>
                                        <xsl:with-param name="of">
                                            <xsl:if test="contains(meta/plooiipm/extrametadata[1]/@name,'.')">
                                                <xsl:call-template name="keyvalue">
                                                    <xsl:with-param name="key">prefix</xsl:with-param>
                                                    <xsl:with-param name="of">
                                                        <xsl:call-template name="substring-before-last-dot">
                                                            <xsl:with-param name="text" select="meta/plooiipm/extrametadata[1]/@name" />
                                                        </xsl:call-template>
                                                    </xsl:with-param>
                                                    <xsl:with-param name="first">true</xsl:with-param>
                                                    <xsl:with-param name="prefix">"</xsl:with-param>
                                                    <xsl:with-param name="suffix">"</xsl:with-param>
                                                    <xsl:with-param name="indent">6</xsl:with-param>
                                                </xsl:call-template>
                                            </xsl:if>
                                            <xsl:call-template name="list">
                                                <xsl:with-param name="key">velden</xsl:with-param>
                                                <xsl:with-param name="of">
                                                    <xsl:apply-templates select="meta/plooiipm/extrametadata" />
                                                </xsl:with-param>
                                                <xsl:with-param name="first" select="not(contains(meta/plooiipm/extrametadata[1]/@name,'.'))" />
                                                <xsl:with-param name="indent">6</xsl:with-param>
                                            </xsl:call-template>
                                        </xsl:with-param>
                                    </xsl:call-template>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:if>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="object">
                    <xsl:with-param name="key">plooiIntern</xsl:with-param>
                    <xsl:with-param name="of">
                        <xsl:apply-templates select="meta/plooiipm/identifier" mode="keystrvalue">
                            <xsl:with-param name="key">dcnId</xsl:with-param>
                            <xsl:with-param name="first">true</xsl:with-param>
                        </xsl:apply-templates>
                        <xsl:apply-templates select="meta/plooiipm/aanbieder" mode="keystrvalue">
                            <xsl:with-param name="key">aanbieder</xsl:with-param>
                            <xsl:with-param name="first">
                                <xsl:choose>
                                    <xsl:when test="meta/plooiipm/identifier">false</xsl:when>
                                    <xsl:otherwise>true</xsl:otherwise>
                                </xsl:choose>
                            </xsl:with-param>
                        </xsl:apply-templates>
                        <xsl:call-template name="list">
                            <xsl:with-param name="key">extId</xsl:with-param>
                            <xsl:with-param name="of">
                                <xsl:apply-templates select="meta/othermeta/bronmeta/identifier" />
                                <xsl:if test="meta/owmsmantel/isPartOf">
                                    <xsl:call-template name="keyvalue">
                                        <xsl:with-param name="of" select="body/documenten/document[@published='true']/bestandsnaam" />
                                        <xsl:with-param name="prefix">"</xsl:with-param>
                                        <xsl:with-param name="suffix">"</xsl:with-param>
                                    </xsl:call-template>
                                </xsl:if>
                            </xsl:with-param>
                        </xsl:call-template>
                        <xsl:apply-templates select="meta/othermeta/bronmeta/source-label" mode="keystrvalue">
                            <xsl:with-param name="key">sourceLabel</xsl:with-param>
                        </xsl:apply-templates>
                    </xsl:with-param>
                    <xsl:with-param name="indent">2</xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="list">
                    <xsl:with-param name="key">documentrelaties</xsl:with-param>
                    <xsl:with-param name="of">
                        <xsl:apply-templates select="meta/owmsmantel/relation" />
                        <xsl:apply-templates select="body/documenten/document" mode="relatie" />
                        <xsl:apply-templates select="meta/owmsmantel/isPartOf" mode="relatie" />
                    </xsl:with-param>
                    <xsl:with-param name="indent">2</xsl:with-param>
                </xsl:call-template>
                <xsl:if test="body/tekst">
                    <xsl:call-template name="object">
                        <xsl:with-param name="key">body</xsl:with-param>
                        <xsl:with-param name="of">
                            <xsl:if test="body/tekst">
                                <xsl:call-template name="list">
                                    <xsl:with-param name="key">tekst</xsl:with-param>
                                    <xsl:with-param name="first">true</xsl:with-param>
                                    <xsl:with-param name="of">
                                        <xsl:text>"</xsl:text>
                                        <xsl:apply-templates select="body/tekst/*|body/tekst/text()" mode="copy-xml" />
                                        <xsl:text>"</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </xsl:if>
                        </xsl:with-param>
                        <xsl:with-param name="indent">2</xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
                <xsl:if test="body/documenten/document">
                    <xsl:call-template name="list">
                        <xsl:with-param name="key">versies</xsl:with-param>
                        <xsl:with-param name="of">
                            <xsl:call-template name="object">
                                <xsl:with-param name="of">
                                    <xsl:call-template name="keyvalue">
                                        <xsl:with-param name="key">nummer</xsl:with-param>
                                        <xsl:with-param name="of">1</xsl:with-param>
                                        <xsl:with-param name="first">true</xsl:with-param>
                                    </xsl:call-template>
                                    <xsl:call-template name="keyvalue">
                                        <xsl:with-param name="key">oorzaak</xsl:with-param>
                                        <xsl:with-param name="of">"aanlevering"</xsl:with-param>
                                    </xsl:call-template>
                                    <xsl:call-template name="list">
                                        <xsl:with-param name="key">bestanden</xsl:with-param>
                                        <xsl:with-param name="of">
                                            <xsl:apply-templates select="body/documenten/document" mode="versie" />
                                        </xsl:with-param>
                                    </xsl:call-template>
                                </xsl:with-param>
                                <xsl:with-param name="first">true</xsl:with-param>
                                <xsl:with-param name="indent">2</xsl:with-param>
                            </xsl:call-template>
                        </xsl:with-param>
                        <xsl:with-param name="indent">2</xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:with-param>
            <xsl:with-param name="indent">0</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="available">
        <xsl:call-template name="object">
            <xsl:with-param name="key">geldigheid</xsl:with-param>
            <xsl:with-param name="of">
                <xsl:apply-templates select="start" mode="keystrvalue">
                    <xsl:with-param name="key">begindatum</xsl:with-param>
                    <xsl:with-param name="indent">6</xsl:with-param>
                    <xsl:with-param name="first">true</xsl:with-param>
                </xsl:apply-templates>
                <xsl:apply-templates select="end" mode="keystrvalue">
                    <xsl:with-param name="key">einddatum</xsl:with-param>
                    <xsl:with-param name="indent">6</xsl:with-param>
                </xsl:apply-templates>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="relation">
        <xsl:call-template name="object">
            <xsl:with-param name="of">
                <xsl:apply-templates select="@resourceIdentifier" mode="keystrvalue">
                    <xsl:with-param name="key">relation</xsl:with-param>
                    <xsl:with-param name="first">true</xsl:with-param>
                </xsl:apply-templates>
                <xsl:apply-templates select="." mode="keystrvalue">
                    <xsl:with-param name="key">role</xsl:with-param>
                    <xsl:with-param name="value">https://identifier.overheid.nl/plooi/def/thes/documentrelatie/gerelateerd</xsl:with-param>
                </xsl:apply-templates>
            </xsl:with-param>
            <xsl:with-param name="first">true</xsl:with-param>
            <xsl:with-param name="indent">2</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="extrametadata">
        <xsl:call-template name="object">
            <xsl:with-param name="first" select="not(preceding-sibling::extrametadata)" />
            <xsl:with-param name="of">
                <xsl:call-template name="keyvalue">
                    <xsl:with-param name="key">key</xsl:with-param>
                    <xsl:with-param name="prefix">"</xsl:with-param>
                    <xsl:with-param name="suffix">"</xsl:with-param>
                    <xsl:with-param name="first">true</xsl:with-param>
                    <xsl:with-param name="indent">8</xsl:with-param>
                    <xsl:with-param name="of">
                        <xsl:call-template name="substring-after-last-dot">
                            <xsl:with-param name="text" select="@name" />
                        </xsl:call-template>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="list">
                    <xsl:with-param name="key">values</xsl:with-param>
                    <xsl:with-param name="of">
                        <xsl:call-template name="keyvalue">
                            <xsl:with-param name="of" select="." />
                            <xsl:with-param name="prefix">"</xsl:with-param>
                            <xsl:with-param name="suffix">"</xsl:with-param>
                            <xsl:with-param name="first">true</xsl:with-param>
                        </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="indent">8</xsl:with-param>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="indent">6</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="description">
        <xsl:call-template name="keyvalue">
            <xsl:with-param name="of" select="normalize-space()" />
            <xsl:with-param name="prefix">"</xsl:with-param>
            <xsl:with-param name="suffix">"</xsl:with-param>
            <xsl:with-param name="first" select="not(preceding-sibling::description)" />
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="subject">
        <xsl:call-template name="keyvalue">
            <xsl:with-param name="of" select="." />
            <xsl:with-param name="prefix">"</xsl:with-param>
            <xsl:with-param name="suffix">"</xsl:with-param>
            <xsl:with-param name="first" select="not(preceding-sibling::subject)" />
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="alternative">
        <xsl:call-template name="keyvalue">
            <xsl:with-param name="of" select="." />
            <xsl:with-param name="prefix">"</xsl:with-param>
            <xsl:with-param name="suffix">"</xsl:with-param>
            <xsl:with-param name="first" select="not(preceding-sibling::alternative)" />
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="identifier">
        <xsl:call-template name="keyvalue">
            <xsl:with-param name="of" select="." />
            <xsl:with-param name="prefix">"</xsl:with-param>
            <xsl:with-param name="suffix">"</xsl:with-param>
            <xsl:with-param name="first" select="not(preceding-sibling::identifier)" />
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="*" mode="copy-xml">
        <!-- Dunno why, but simple copy and copy-of don't work' -->
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="name()" />
        <xsl:apply-templates select="@*" mode="copy-xml" />
        <xsl:text>&gt;</xsl:text>
        <xsl:apply-templates select="*|text()" mode="copy-xml" />
        <xsl:text>&lt;/</xsl:text>
        <xsl:value-of select="name()" />
        <xsl:text>&gt; </xsl:text>
    </xsl:template>
    <xsl:template match="@*" mode="copy-xml">
        <xsl:value-of select="concat(' ', name(), '=\&quot;', current(), '\&quot;')" />
    </xsl:template>
    <xsl:template match="text()" mode="copy-xml">
        <xsl:call-template name="escape-string">
            <xsl:with-param name="text" select="normalize-space()" />
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="document" mode="versie">
        <xsl:if test="not(ref)">
            <xsl:call-template name="object">
                <xsl:with-param name="first" select="not(count(preceding-sibling::document) > count(preceding-sibling::document/ref))" />
                <xsl:with-param name="of">
                    <xsl:call-template name="keyvalue">
                        <xsl:with-param name="key">gepubliceerd</xsl:with-param>
                        <xsl:with-param name="first">true</xsl:with-param>
                        <xsl:with-param name="of" select="@published" />
                        <xsl:with-param name="indent">6</xsl:with-param>
                    </xsl:call-template>
                    <xsl:apply-templates select="manifestatie-label" mode="keystrvalue">
                        <xsl:with-param name="key">label</xsl:with-param>
                        <xsl:with-param name="indent">6</xsl:with-param>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="url" mode="keystrvalue">
                        <xsl:with-param name="indent">6</xsl:with-param>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="bestandsnaam" mode="keystrvalue">
                        <xsl:with-param name="normalize" select="false()" />
                        <xsl:with-param name="indent">6</xsl:with-param>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="titel" mode="keystrvalue">
                        <xsl:with-param name="indent">6</xsl:with-param>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="timestamp" mode="keystrvalue">
                        <xsl:with-param name="key">mutatiedatumtijd</xsl:with-param>
                        <xsl:with-param name="indent">6</xsl:with-param>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="hash" mode="keystrvalue">
                        <xsl:with-param name="indent">6</xsl:with-param>
                    </xsl:apply-templates>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="document" mode="relatie">
        <xsl:if test="ref">
            <xsl:call-template name="object">
                <xsl:with-param name="of">
                    <xsl:apply-templates select="ref" mode="keystrvalue">
                        <xsl:with-param name="key">relation</xsl:with-param>
                        <xsl:with-param name="first">true</xsl:with-param>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="." mode="keystrvalue">
                        <xsl:with-param name="key">role</xsl:with-param>
                        <xsl:with-param name="value">https://identifier.overheid.nl/plooi/def/thes/documentrelatie/onderdeel</xsl:with-param>
                    </xsl:apply-templates>
                    <xsl:choose>
                        <xsl:when test="titel">
                            <xsl:apply-templates select="titel" mode="keystrvalue">
                                <xsl:with-param name="key">titel</xsl:with-param>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="bestandsnaam" mode="keystrvalue">
                                <xsl:with-param name="key">titel</xsl:with-param>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="first" select="not(preceding-sibling::*/ref)" />
                <xsl:with-param name="indent">2</xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="isPartOf" mode="relatie">
        <xsl:call-template name="object">
            <xsl:with-param name="of">
                <xsl:apply-templates select="." mode="keystrvalue">
                    <xsl:with-param name="key">relation</xsl:with-param>
                    <xsl:with-param name="first">true</xsl:with-param>
                </xsl:apply-templates>
                <xsl:apply-templates select="." mode="keystrvalue">
                    <xsl:with-param name="key">role</xsl:with-param>
                    <xsl:with-param name="value">https://identifier.overheid.nl/plooi/def/thes/documentrelatie/bundel</xsl:with-param>
                </xsl:apply-templates>
            </xsl:with-param>
            <xsl:with-param name="first">true</xsl:with-param>
            <xsl:with-param name="indent">2</xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="@*|*" mode="resource">
        <xsl:param name="key" />
        <xsl:param name="id" />
        <xsl:param name="label" />
        <xsl:param name="first" />
        <xsl:param name="indent" select="4" />
        <xsl:call-template name="object">
            <xsl:with-param name="of">
                <xsl:apply-templates select="@scheme" mode="keystrvalue">
                    <xsl:with-param name="key">type</xsl:with-param>
                    <xsl:with-param name="first">true</xsl:with-param>
                    <xsl:with-param name="indent" select="number($indent)+2" />
                </xsl:apply-templates>
                <xsl:choose>
                    <xsl:when test="$id">
                        <xsl:call-template name="keyvalue">
                            <xsl:with-param name="of" select="$id" />
                            <xsl:with-param name="key">id</xsl:with-param>
                            <xsl:with-param name="first">
                                <xsl:choose>
                                    <xsl:when test="@scheme">false</xsl:when>
                                    <xsl:otherwise>true</xsl:otherwise>
                                </xsl:choose>
                            </xsl:with-param>
                            <xsl:with-param name="indent" select="number($indent)+2" />
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="@resourceIdentifier" mode="keystrvalue">
                            <xsl:with-param name="key">id</xsl:with-param>
                            <xsl:with-param name="first">
                                <xsl:choose>
                                    <xsl:when test="@scheme">false</xsl:when>
                                    <xsl:otherwise>true</xsl:otherwise>
                                </xsl:choose>
                            </xsl:with-param>
                            <xsl:with-param name="indent" select="number($indent)+2" />
                        </xsl:apply-templates>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:apply-templates select="." mode="keystrvalue">
                    <xsl:with-param name="key">label</xsl:with-param>
                    <xsl:with-param name="value" select="$label" />
                    <xsl:with-param name="first">
                        <xsl:choose>
                            <xsl:when test="$id">false</xsl:when>
                            <xsl:when test="@resourceIdentifier">false</xsl:when>
                            <xsl:when test="@scheme">false</xsl:when>
                            <xsl:otherwise>true</xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="indent" select="number($indent)+2" />
                </xsl:apply-templates>
            </xsl:with-param>
            <xsl:with-param name="key" select="$key" />
            <xsl:with-param name="first" select="$first and name(preceding-sibling::*[1]) != name(current())" />
            <xsl:with-param name="indent" select="$indent" />
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="@*|*" mode="keystrvalue">
        <xsl:param name="key" />
        <xsl:param name="value" />
        <xsl:param name="prefix">"</xsl:param>
        <xsl:param name="suffix">"</xsl:param>
        <xsl:param name="first" />
        <xsl:param name="indent" select="4" />
        <xsl:param name="normalize" select="true()" />
        <xsl:call-template name="keyvalue">
            <xsl:with-param name="of">
                <xsl:choose>
                    <xsl:when test="$value">
                        <xsl:value-of select="$value" />
                    </xsl:when>
                    <xsl:when test="$normalize">
                        <xsl:call-template name="escape-string">
                            <xsl:with-param name="text" select="normalize-space()" />
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="." />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="key">
                <xsl:choose>
                    <xsl:when test="$key">
                        <xsl:value-of select="$key" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="name()" />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="prefix" select="$prefix" />
            <xsl:with-param name="first" select="$first" />
            <xsl:with-param name="indent" select="$indent" />
            <xsl:with-param name="prefix" select="$prefix" />
            <xsl:with-param name="suffix" select="$suffix" />
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="object">
        <xsl:param name="of" />
        <xsl:param name="key" />
        <xsl:param name="first" />
        <xsl:param name="indent" select="4" />
        <xsl:call-template name="keyvalue">
            <xsl:with-param name="of" select="$of" />
            <xsl:with-param name="key" select="$key" />
            <xsl:with-param name="first" select="$first" />
            <xsl:with-param name="indent" select="$indent" />
            <xsl:with-param name="prefix">{</xsl:with-param>
            <xsl:with-param name="suffix">
                <xsl:choose>
                    <xsl:when test="$of">
                        <xsl:text> &#10;</xsl:text>
                        <xsl:value-of select="substring($spaces, 1, number($indent))" />
                        <xsl:text>}</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> }</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="list">
        <xsl:param name="key" />
        <xsl:param name="of" />
        <xsl:param name="first" />
        <xsl:param name="indent" select="4" />
        <xsl:if test="$of">
            <xsl:call-template name="keyvalue">
                <xsl:with-param name="key" select="$key" />
                <xsl:with-param name="of" select="$of" />
                <xsl:with-param name="first" select="$first" />
                <xsl:with-param name="indent" select="$indent" />
                <xsl:with-param name="prefix">[ </xsl:with-param>
                <xsl:with-param name="suffix"> ]</xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template name="keyvalue">
        <xsl:param name="of" />
        <xsl:param name="key" />
        <xsl:param name="prefix" />
        <xsl:param name="suffix" />
        <xsl:param name="first" />
        <xsl:param name="indent" select="4" />
        <xsl:call-template name="key">
            <xsl:with-param name="key" select="$key" />
            <xsl:with-param name="first" select="$first" />
            <xsl:with-param name="indent" select="$indent" />
        </xsl:call-template>
        <xsl:value-of select="$prefix" />
        <xsl:value-of select="$of" />
        <xsl:value-of select="$suffix" />
    </xsl:template>

    <xsl:template name="key">
        <xsl:param name="key" />
        <xsl:param name="first" />
        <xsl:param name="indent" select="4" />
        <xsl:if test="string($first) != 'true'">, </xsl:if>
        <xsl:if test="$key">
            <xsl:text>&#10;</xsl:text>
            <xsl:value-of select="substring($spaces, 1, number($indent))" />
            <xsl:text>"</xsl:text>
            <xsl:value-of select="$key" />
            <xsl:text>" : </xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template name="escape-string">
        <xsl:param name="text" />
        <xsl:choose>
            <xsl:when test="contains($text,'&quot;')">
                <xsl:value-of select="substring-before($text,'&quot;')" />
                <xsl:text>\"</xsl:text>
                <xsl:call-template name="escape-string">
                    <xsl:with-param name="text" select="substring-after($text,'&quot;')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="substring-before-last-dot">
        <xsl:param name="text" />
        <xsl:if test="contains($text,'.')">
            <xsl:value-of select="substring-before($text,'.')" />
            <xsl:if test="contains(substring-after($text,'.'),'.')">
                <xsl:text>.</xsl:text>
            </xsl:if>
            <xsl:call-template name="substring-before-last-dot">
                <xsl:with-param name="text" select="substring-after($text,'.')" />
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    <xsl:template name="substring-after-last-dot">
        <xsl:param name="text" />
        <xsl:choose>
            <xsl:when test="contains($text,'.')">
                <xsl:call-template name="substring-after-last-dot">
                    <xsl:with-param name="text" select="substring-after($text,'.')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
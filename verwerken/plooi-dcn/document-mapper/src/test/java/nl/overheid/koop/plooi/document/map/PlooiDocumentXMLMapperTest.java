package nl.overheid.koop.plooi.document.map;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripTrailingSpaces;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.registration.model.Severity;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PlooiDocumentXMLMapperTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    public static final PlooiDocumentMapping PLOOI_MAPPER = ConfigurableDocumentMapper.configureWith(
            new PlooiXmlMapping())
            .mapOfficieleTitel(TextMapping.plain("//owmskern/title"))
            .mapLanguage(ResourceMapping.term("//owmskern/language"))
            .mapOpsteller(ResourceMapping.owms("//owmskern/creator"))
            .mapPublisher(ResourceMapping.owms("//owmskern/authority"))
            .mapWeblocatie("//owmsmantel/source/@resourceIdentifier")
            .mapAlternatieveTitel(TextMapping.plain("//owmsmantel/alternative"))
            .mapOmschrijving(TextMapping.plain("//owmsmantel/description"))
            .mapCreatiedatum("//owmsmantel/issued")
            .mapGeldigheidsstartdatum(DateMapping.withFormat("//owmsmantel/available/start", DateTimeFormatter.ISO_DATE))
            .mapOnderwerp("//owmsmantel/subject")
            .mapDocumentsoort(ResourceMapping.full(null, "//plooiipm/informatiecategorie", null, "//plooiipm/informatiecategorie/@resourceIdentifier"))
            .mapVerantwoordelijke(ResourceMapping.term("//plooiipm/verantwoordelijke"))
            .addThema(ResourceMapping.owms("//plooiipm/topthema"))
            .setAanbieder("plooi")
            .addExtraMetadata(ExtraMetadataMapping.temp("test.literal", "//plooiipm/extrametadata[@name='temp.test.literal']"))
            .addExtraMetadata(ExtraMetadataMapping.dynamic("//plooiipm/extrametadata[@name='temp.test.dynamic']", "@name", "."))
            .addBodyTekst(TextMapping.embeddedHTML("//body/tekst/div"))
            .mapper();

    @Test
    void populateWithEmptyMapper() throws IOException {
        PlooiEnvelope plooiDoc = ConfigurableDocumentMapper.configureWith(
                new PlooiXmlMapping())
                .mapper()
                .populate(resolve(getClass(), "plooidoc-kst.xml"), new PlooiEnvelope("tst", "tst"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "empty-plooidoc.json"),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi()))));
        assertTrue(plooiDoc.getDocumentText().isEmpty());
    }

    @Test
    void populateIllegalXML() throws IOException {
        PlooiEnvelope plooiDoc = PLOOI_MAPPER.populate(
                new ByteArrayInputStream(new String("ILLEGAL XML").getBytes(StandardCharsets.UTF_8)), new PlooiEnvelope("tst", "tst"));
        assertEquals(1, plooiDoc.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.CANT_PARSE.name(), plooiDoc.status().getDiagnoses().get(0).getCode());
        assertEquals(Severity.ERROR, plooiDoc.status().getDiagnoses().get(0).getSeverity());
        assertTrue(plooiDoc.getDocumentText().isEmpty());
    }

    @Test
    void populateSuccess() throws IOException {
        PlooiEnvelope plooiDoc = PLOOI_MAPPER.populate(resolve(getClass(), "plooidoc-kst.xml"), new PlooiEnvelope("tst", "kst-35842-4"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "plooidoc-kst.json"),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi()))));
        assertEquals("Document tekst more tekst", plooiDoc.getDocumentText());
    }

    @ParameterizedTest
    @CsvSource({
            "bodytekst_embeddedhtml-out.json, Title with link paragraph tekst, bodytekst_embeddedhtml-in.xml, /document/title, /document/embedded-content/*, true",
            "empty-plooidoc.json,             ,                                bodytekst_embeddedhtml-in.xml, dummy,           /document/nomatch-content/*,  true",
            "bodytekst_nestedhtml-out.json,   Title with link paragraph tekst, bodytekst_nestedhtml-in.xml,   dummy,           /document/nested-content/*,   false",
            "empty-plooidoc.json,             ,                                bodytekst_nestedhtml-in.xml,   dummy,           /document/nomatch-content/*,  false",
    })
    void bodyTekst(String expectFile, String expectText, String mapFile, String mapTitle, String mapBodyTekst, boolean parseEmbeddedHTML) throws IOException {
        PlooiEnvelope plooiDoc = ConfigurableDocumentMapper.configureWith(new PlooiXmlMapping())
                .mapOfficieleTitel(TextMapping.embeddedHTML(mapTitle))
                .addBodyTekst(parseEmbeddedHTML ? TextMapping.embeddedHTML(mapBodyTekst) : TextMapping.plain(mapBodyTekst), Map.of("h2", "h1"))
                .mapper()
                .populate(resolve(getClass(), mapFile), new PlooiEnvelope("tst", "tst"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), expectFile),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi()))));
        assertEquals(StringUtils.defaultIfBlank(expectText, ""), plooiDoc.getDocumentText().replaceAll("\\s+", " "));
    }

    @Test
    void innerText() throws IOException {
        PlooiEnvelope plooiDoc = ConfigurableDocumentMapper.configureWith(new PlooiXmlMapping())
                .addDocumentText("/document/nested-content/*")
                .mapper()
                .populate(
                        new File("src/test/resources/nl/overheid/koop/plooi/document/map/bodytekst_nestedhtml-in.xml"),
                        new PlooiEnvelope("tst", "tst"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "empty-plooidoc.json"),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi()))));
        assertEquals("Title with link paragraph tekst", plooiDoc.getDocumentText().replaceAll("\\s+", " "));
    }
}

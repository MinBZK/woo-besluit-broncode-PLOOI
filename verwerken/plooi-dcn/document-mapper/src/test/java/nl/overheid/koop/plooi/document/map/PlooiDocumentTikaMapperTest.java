package nl.overheid.koop.plooi.document.map;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.registration.model.Severity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PlooiDocumentTikaMapperTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    public static final PlooiDocumentMapping TIKA_MAPPER = ConfigurableDocumentMapper.configureWith(
            new PlooiTikaMapping())
            .mapOfficieleTitel(TextMapping.plain("dc:title"))
            // .mapLanguage("dc:language") // mapped from PDF
            // .mapLanguage("custom:Language") // mapped from ODT
            // .mapModified("dcterms:created")
            .mapDocumentsoort(ResourceMapping.term("dc:subject"))
            .addThema(ResourceMapping.term("cp:subject"))
            .addThema(ResourceMapping.term("pdf:docinfo:subject"))
            .setAanbieder("plooi")
            .addDocumentText(PlooiTikaMapping.DOCUMENT_TEXT)
            .mapper();

    @Test
    void populateWithEmptyMapper() throws IOException {
        PlooiEnvelope plooiDoc = ConfigurableDocumentMapper.configureWith(
                new PlooiTikaMapping()).mapper().populate(resolve(getClass(), "test.pdf"), new PlooiEnvelope("tst", "tst"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "empty-plooidoc.json"),
                stripWindowsCR(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi())));
        assertTrue(plooiDoc.getDocumentText().isEmpty());
    }

    @Test
    void populatePDF() throws IOException {
        PlooiEnvelope plooiDoc = TIKA_MAPPER.populate(resolve(getClass(), "test.pdf"), new PlooiEnvelope("tst", "tst"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "plooidoc-tika.json"),
                stripWindowsCR(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi())));
        assertEquals("This is great text", plooiDoc.getDocumentText());
    }

    @Test
    void populateODT() throws IOException {
        PlooiEnvelope plooiDoc = TIKA_MAPPER.populate(resolve(getClass(), "test.odt"), new PlooiEnvelope("tst", "tst"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "plooidoc-tika.json"),
                stripWindowsCR(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi())));
        assertEquals("This is great text", plooiDoc.getDocumentText());
    }

    @Test
    void populatePDF_broken_default_WARNING() throws IOException {
        PlooiEnvelope plooiDoc = TIKA_MAPPER.populate(resolve(getClass(), "test_broken.pdf"), new PlooiEnvelope("tst", "tst"));
        assertEquals(1, plooiDoc.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.CANT_PARSE.name(), plooiDoc.status().getDiagnoses().get(0).getCode());
        assertEquals(Severity.WARNING, plooiDoc.status().getDiagnoses().get(0).getSeverity());
        assertTrue(plooiDoc.getDocumentText().isEmpty());
    }

    @Test
    void populatePDF_broken_WARNING() throws IOException {
        PlooiEnvelope plooiDoc = ConfigurableDocumentMapper.configureWith(new PlooiTikaMapping().withParsingErrorSeverity(Severity.WARNING))
                .mapper()
                .populate(resolve(getClass(), "test_broken.pdf"), new PlooiEnvelope("tst", "tst"));
        assertEquals(1, plooiDoc.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.CANT_PARSE.name(), plooiDoc.status().getDiagnoses().get(0).getCode());
        assertEquals(Severity.WARNING, plooiDoc.status().getDiagnoses().get(0).getSeverity());
        assertTrue(plooiDoc.getDocumentText().isEmpty());
    }
}

package nl.overheid.koop.plooi.dcn.roo;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.document.normalize.PlooiDocumentValidator;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RooNormalizationTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @ParameterizedTest
    @CsvSource({
            "plooidoc-mezk.json, metadata_roo-mezk.xml",
            "plooidoc-gemfl.json, metadata_roo-gemfl.xml",
    })
    void map(String expectFile, String mapFile) throws IOException {
        var mapped = map(new PlooiEnvelope("roo", "dummy"), readFileAsString(getClass(), mapFile));
        assertTrue(mapped.status().getDiagnoses().isEmpty());
        assertEquals(
                readFileAsString(getClass(), expectFile),
                stripWindowsCR(PlooiBindings.plooiBinding().marshalToString(mapped.getPlooi())));
    }

    static PlooiEnvelope map(PlooiEnvelope plooi, String rooXml) throws IOException {
        try (InputStream rooStream = new ByteArrayInputStream(rooXml.getBytes(StandardCharsets.UTF_8))) {
            RooRoute.ROO_DIAGNOSTIC_FILTER.process(plooi);
            RooRoute.ROO_META_MAPPER.populate(rooStream, plooi);
            RooRoute.ROO_POSTMAP.process(plooi);
            new PlooiDocumentValidator().process(plooi);
            return plooi;
        }
    }
}

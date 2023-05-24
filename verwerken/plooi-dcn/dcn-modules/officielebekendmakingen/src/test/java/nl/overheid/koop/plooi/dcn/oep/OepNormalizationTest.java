package nl.overheid.koop.plooi.dcn.oep;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

class OepNormalizationTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @ParameterizedTest
    @CsvSource({
            "plooidoc-kst.json,       metadata_owms-kst.xml,     Thema:ALTLABEL Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL",
            "plooidoc-blg-kst.json,   metadata_owms-blg-kst.xml, Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL Thema:REQUIRED_DEFAULT",
            "plooidoc-ag.json,        metadata_owms-ag.xml,      Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL Thema:REQUIRED_DEFAULT",
            "plooidoc-h-ek.json,      metadata_owms-h-ek.xml,    Thema:ALTLABEL Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL",
            "plooidoc-h-tk.json,      metadata_owms-h-tk.xml,    Thema:ALTLABEL Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL",
            "plooidoc-ah.json,        metadata_owms-ah.xml,      Documentsoort:ALTLABEL Thema:ALTLABEL Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL",
            "plooidoc-kv-ek.json,     metadata_owms-kv-ek.xml,   Documentsoort:ALTLABEL Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL",
            "plooidoc-kv-tk.json,     metadata_owms-kv-tk.xml,   Documentsoort:ALTLABEL Thema:ALTLABEL Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL",
            "plooidoc-nds.json,       metadata_owms-nds.xml,     Thema:ALTLABEL Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL",
            "plooidoc-blg-nds.json,   metadata_owms-blg-nds.xml, Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL Thema:REQUIRED_DEFAULT",
            "plooidoc-stb.json,       metadata_owms-sbt.xml,     Thema:ALTLABEL",
            "plooidoc-stcrt.json,     metadata_owms-stcrt.xml,   Thema:ALTLABEL Opsteller:ALTLABEL Verantwoordelijke:ALTLABEL"
    })
    void map(String expectFile, String mapFile, String diagnostics) throws IOException {
        var mapped = map(new PlooiEnvelope("oep", "dummy"), readFileAsString(getClass(), mapFile));
        assertEquals(
                readFileAsString(getClass(), expectFile),
                stripWindowsCR(PlooiBindings.plooiBinding().marshalToString(mapped.getPlooi())));
        assertEquals(diagnostics, mapped.status().getDiagnosticSummary());
    }

    static PlooiEnvelope map(PlooiEnvelope plooi, String oepXml) throws IOException {
        try (InputStream oepStream = new ByteArrayInputStream(oepXml.getBytes(StandardCharsets.UTF_8))) {
            OepSruRoute.OEP_DIAGNOSTIC_FILTER.process(plooi);
            OepSruRoute.OEP_META_MAPPER.populate(oepStream, plooi);
            OepSruRoute.OEP_PREMAP.process(plooi);
            OepSruRoute.OEP_NORMALIZER.process(plooi);
            OepSruRoute.OEP_POSTMAP.process(plooi);
            new PlooiDocumentValidator().process(plooi);
            return plooi;
        }
    }
}

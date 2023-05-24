package nl.overheid.koop.plooi.dcn.ronl.archief;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripTrailingSpaces;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.ronl.RonlShared;
import nl.overheid.koop.plooi.document.normalize.BeslisnotaDetector;
import nl.overheid.koop.plooi.document.normalize.PlooiDocumentValidator;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RonlArchiefNormalizationTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @ParameterizedTest
    @CsvSource({
            "plooi-2011-15112_mapped.json, ,                                ronl-2011-15112.xml, plooicb-2011-15112, Thema:ALTLABEL Thema:ALTLABEL",
            "plooi-2011-581_mapped.json,   ,                                ronl-2011-581.xml,   plooicb-2011-581,   Thema:REQUIRED_DEFAULT",
            "plooi-2011-5647_mapped.json,  plooi-2011-5647_relations.json,  ronl-2011-5647.xml,  plooicb-2011-5647,  Thema:REQUIRED_DEFAULT",
            "plooi-2011-5648_mapped.json,  plooi-2011-5648_relations.json,  ronl-2011-5648.xml,  plooicb-2011-5648,  Thema:REQUIRED_DEFAULT",
            "plooi-2011-5649_mapped.json,  plooi-2011-5649_relations.json,  ronl-2011-5649.xml,  plooicb-2011-5649,  Thema:REQUIRED_DEFAULT",
    })
    void map(String expectPlooi, String expectRelations, String mapFile, String externalId, String diagnostics) throws IOException {
        var mapped = map(new PlooiEnvelope(RonlArchiefRoute.SOURCE_LABEL, externalId), readFileAsString(getClass(), mapFile));
        assertEquals(
                readFileAsString(getClass(), expectPlooi),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(mapped.getPlooi()))));
        assertEquals(
                StringUtils.isBlank(expectRelations) ? "{ }" : readFileAsString(getClass(), expectRelations),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(new Plooi().documentrelaties(mapped.getRelationsForStage())))));
        assertEquals(diagnostics, mapped.status().getDiagnosticSummary());
    }

    static PlooiEnvelope map(PlooiEnvelope plooi, String ronlXml) throws IOException {
        try (InputStream ronlStream = new ByteArrayInputStream(ronlXml.getBytes(StandardCharsets.UTF_8))) {
            RonlShared.RONL_DIAGNOSTIC_FILTER.process(plooi);
            RonlArchiefRoute.RONL_META_MAPPER.populate(ronlStream, plooi);
            BeslisnotaDetector.handleBeslisnota(plooi);
            RonlShared.RONL_NORMALIZER.process(plooi);
            RonlArchiefRoute.RONL_RELATIONAL.process(plooi);
            new PlooiDocumentValidator().process(plooi);
            return plooi;
        }
    }
}

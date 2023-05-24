package nl.overheid.koop.plooi.dcn.api.service;

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
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ApiNormalizationTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    void mapSingleFile() throws IOException {
        PlooiEnvelope mappedDoc = map(new PlooiEnvelope(DcnIdentifierUtil.PLOOI_API_SRC, "5afce6ec-a4ed-4c62-a6d5-168a59247678"),
                readFileAsString(getClass(), "test_api_plooi.json"));
        assertEquals(readFileAsString(getClass(), "plooi_api.json"), stripWindowsCR(PlooiBindings.plooiBinding().marshalToString(mappedDoc.getPlooi())));
        assertTrue(mappedDoc.status().getDiagnoses().isEmpty());
    }

    static PlooiEnvelope map(PlooiEnvelope plooi, String apiJson) throws IOException {
        try (InputStream ronlStream = new ByteArrayInputStream(apiJson.getBytes(StandardCharsets.UTF_8))) {
            ApiProcessRoute.API_DIAGNOSTIC_FILTER.process(plooi);
            ApiProcessRoute.API_META_MAPPER.populate(ronlStream, plooi);
            ApiProcessRoute.API_NORMALIZER.process(plooi);
            ApiProcessRoute.API_POSTMAP.process(plooi);
            new PlooiDocumentValidator().process(plooi);
            return plooi;
        }
    }
}

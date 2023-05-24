package nl.overheid.koop.plooi.dcn.aanleverloket;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripTrailingSpaces;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AanLoNormalizationTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    void testMapperSuccess() throws IOException {
        PlooiEnvelope plooiDoc = AanLoRoute.AANLO_META_MAPPER.populate(resolve(getClass(), "bijwerken_plooicb-2021-33.xml"),
                new PlooiEnvelope("tst", "bijwerken_plooicb-2021-33.xml"));
        AanLoRoute.AANLO_POSTMAPPING.process(plooiDoc);
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "bijwerken_plooicb-2021-33_mapped.json"),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi()))));
    }

    @Test
    void testNormalizerMappingOverrides() {
        PlooiEnvelope plooiDoc = new PlooiEnvelope("test", "test");
        plooiDoc.getClassificatiecollectie().addDocumentsoortenItem(new IdentifiedResource().label("adviesvoorstel"));
        AanLoRoute.AANLO_NORMALIZER.process(plooiDoc);
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/kern/c_d506b718", plooiDoc.getClassificatiecollectie().getDocumentsoorten().get(0).getId());
    }
}

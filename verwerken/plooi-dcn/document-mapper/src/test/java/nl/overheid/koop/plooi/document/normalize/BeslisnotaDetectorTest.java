package nl.overheid.koop.plooi.document.normalize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import org.junit.jupiter.api.Test;

class BeslisnotaDetectorTest {

    @Test
    void notBeslisnota() {
        var target = new PlooiEnvelope("test", "test");
        target.getTitelcollectie()
                .officieleTitel("Random title");
        target.getClassificatiecollectie()
                .addDocumentsoortenItem(new IdentifiedResource()
                        .id("https://identifier.overheid.nl/tooi/def/thes/kern/c_056a75e1")
                        .label("Kamerstuk"));
        BeslisnotaDetector.handleBeslisnota(target);
        assertNotEquals(BeslisnotaDetector.BESLISNOTA, target.getClassificatiecollectie().getDocumentsoorten().get(0).getLabel());
    }

    @Test
    void beslisnota() {
        var target = new PlooiEnvelope("test", "test");
        target.getTitelcollectie()
                .officieleTitel("Beslisnota over iets belangrijks");
        target.getClassificatiecollectie()
                .addDocumentsoortenItem(new IdentifiedResource()
                        .id("https://identifier.overheid.nl/tooi/def/thes/kern/c_056a75e1")
                        .label("Kamerstuk"));
        BeslisnotaDetector.handleBeslisnota(target);
        assertEquals(2, target.getClassificatiecollectie().getDocumentsoorten().size());
        assertEquals("Kamerstuk", target.getClassificatiecollectie().getDocumentsoorten().get(0).getLabel());
        assertEquals(BeslisnotaDetector.BESLISNOTA, target.getClassificatiecollectie().getDocumentsoorten().get(1).getLabel());
    }

    @Test
    void beslisnota_withoutDocumentsoort() {
        var target = new PlooiEnvelope("test", "test");
        target.getTitelcollectie()
                .officieleTitel("Beslisnota over iets belangrijks");
        BeslisnotaDetector.handleBeslisnota(target);
        assertEquals(1, target.getClassificatiecollectie().getDocumentsoorten().size());
        assertEquals(BeslisnotaDetector.BESLISNOTA, target.getClassificatiecollectie().getDocumentsoorten().get(0).getLabel());
    }
}

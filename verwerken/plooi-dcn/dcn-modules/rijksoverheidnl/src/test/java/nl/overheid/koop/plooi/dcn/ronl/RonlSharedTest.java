package nl.overheid.koop.plooi.dcn.ronl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.document.normalize.PlooiDocumentValueNormalizer;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import org.junit.jupiter.api.Test;

class RonlSharedTest {

    @Test
    void removeIgnoredMappings() {
        PlooiEnvelope plooiDoc = new PlooiEnvelope("test", "test");
        plooiDoc.getClassificatiecollectie()
                .addThemasItem(new IdentifiedResource().label("1e deelbesluit Wob-verzoek aanleg 2e Suezkanaal"));
        RonlShared.RONL_NORMALIZER.process(plooiDoc);
        // Mapping with uri "ignore" is emptied, so the validator removes it
        assertTrue(PlooiDocumentValueNormalizer.isEmpty(plooiDoc.getClassificatiecollectie().getThemas().get(0)));
    }
}

package nl.overheid.koop.plooi.document.normalize;

import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import org.apache.commons.lang3.StringUtils;

public final class BeslisnotaDetector implements EnvelopeProcessing<PlooiEnvelope> {

    static final String BESLISNOTA = "beslisnota";

    @Override
    public PlooiEnvelope process(PlooiEnvelope target) {
        return handleBeslisnota(target);
    }

    public static PlooiEnvelope handleBeslisnota(PlooiEnvelope target) {
        var titel = target.getTitelcollectie().getOfficieleTitel();
        if (!StringUtils.isBlank(titel) && titel.toLowerCase().contains(BESLISNOTA)) {
            target.getClassificatiecollectie().addDocumentsoortenItem(new IdentifiedResource().label(BESLISNOTA));
        }
        return target;
    }
}

package nl.overheid.koop.plooi.dcn.repository.store;

import nl.overheid.koop.plooi.dcn.component.types.ObjectProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.model.data.util.AggregatedVersion;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Read a documents manifest from the document repository. */
public class PlooiManifestReading implements ObjectProcessing<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PublicatieClient publicatieClient;

    public PlooiManifestReading(PublicatieClient publClient) {
        this.publicatieClient = publClient;
    }

    @Override
    public PlooiEnvelope process(String dcnId) {
        var manifest = this.publicatieClient.getManifest(dcnId);
        var target = new PlooiEnvelope(manifest.getPlooi());
        this.logger.debug("Read {}", target);
        if (!AggregatedVersion.isAllowed(manifest.getVersies())) {
            this.logger.debug("Skipping not allowed {}", target);
            target.status().discard();
        }
        return target;
    }
}

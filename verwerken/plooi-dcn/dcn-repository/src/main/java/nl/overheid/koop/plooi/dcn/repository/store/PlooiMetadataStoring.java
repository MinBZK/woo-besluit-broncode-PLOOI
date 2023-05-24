package nl.overheid.koop.plooi.dcn.repository.store;

import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.PublishingStateRepository;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Persists the provided {@link PlooiEnvelope} in the document repository, using the DCN identifier (not mapped). */
public class PlooiMetadataStoring implements EnvelopeProcessing<PlooiEnvelope> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PublicatieClient publicatieClient;
    private final PublishingStateRepository publishingStateRepository;

    public PlooiMetadataStoring(PublicatieClient updateClient, PublishingStateRepository publStateRepos) {
        this.publicatieClient = updateClient;
        this.publishingStateRepository = publStateRepos;
    }

    @Override
    public PlooiEnvelope process(PlooiEnvelope target) {
        if (!target.status().isDiscarded()) {
            this.logger.debug("Storing {}", target);
            var dcnId = target.getPlooiIntern().getDcnId();
            this.publicatieClient.postPlooi(dcnId, target.getPlooi());
            this.publicatieClient.postRelations(dcnId, target.getStage().toString(), target.getRelationsForStage());
            var documentText = target.getDocumentText();
            if (!documentText.isEmpty()) {
                this.publicatieClient.postText(dcnId, documentText);
            }
            this.publishingStateRepository.updateAndSave(dcnId, PublishingState.State.INPROGRESS);
        } else {
            this.logger.debug("Not storing {}, it's discarded", target);
        }
        return target;
    }
}

package nl.overheid.koop.dcn.publishingstate;

import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.ObjectProcessing;
import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.PublishingStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishingStateUpdateProcessing implements EnvelopeProcessing<Envelope>, ObjectProcessing<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PublishingStateRepository publishingStateRepository;
    private final PublishingState.State state;

    public PublishingStateUpdateProcessing(PublishingStateRepository publishingStateRepository, PublishingState.State state) {
        this.publishingStateRepository = publishingStateRepository;
        this.state = state;
    }

    @Override
    public Envelope process(Envelope target) {
        process(target.getPlooiIntern().getDcnId());
        return target;
    }

    @Override
    public Void process(String dcnId) {
        this.logger.debug("Update document {} to status OK", dcnId);
        this.publishingStateRepository.updateAndSave(dcnId, this.state);
        return null;
    }
}

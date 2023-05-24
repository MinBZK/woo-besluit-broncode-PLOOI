package nl.overheid.koop.plooi.dcn.repository.store;

import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.PublishingStateRepository;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import nl.overheid.koop.plooi.repository.client.ClientException;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Persists the provided {@link DeliveryEnvelope} in the document repository, using the DCN identifier (not mapped). */
public class PlooiDeliveryStoring implements EnvelopeProcessing<DeliveryEnvelope> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AanleverenClient aanleverenClient;
    private final PublicatieClient publicatieClient;
    private final PublishingStateRepository publishingStateRepository;

    public PlooiDeliveryStoring(AanleverenClient deliveryClient, PublicatieClient updateClient, PublishingStateRepository publStateRepos) {
        this.aanleverenClient = deliveryClient;
        this.publicatieClient = updateClient;
        this.publishingStateRepository = publStateRepos;
    }

    @Override
    public DeliveryEnvelope process(DeliveryEnvelope target) {
        if (target.status().isDiscarded()) {
            this.logger.debug("Not storing {}, it's discarded", target);
        } else {
            this.logger.debug("Storing {}", target);
            postRequest(target, createRequest(
                    target,
                    target.getPlooiIntern()),
                    this.publishingStateRepository.findById(target.getPlooiIntern().getDcnId()).filter(ps -> ps.getIndexed().confirmed()).isEmpty());
        }
        return target;
    }

    private AanleverenClient.Request createRequest(DeliveryEnvelope target, PlooiIntern plooiIntern) {
        var request = this.aanleverenClient.createRequest(
                target.getVersie().bestanden(target.getPlooiFiles().stream().map(PlooiFile::getFile).toList()),
                plooiIntern.getSourceLabel(),
                plooiIntern.getExtId().toArray(new String[plooiIntern.getExtId().size()]));
        target.getPlooiFiles().forEach(file -> {
            if (file.getContentSupplier() != null) {
                request.addPart(file.getFile().getLabel(), file.getContentSupplier());
            } else if (file.getContent() != null) {
                request.addPart(file.getFile().getLabel(), file.getContent());
            }
        });
        return request;
    }

    private void postRequest(DeliveryEnvelope target, AanleverenClient.Request request, boolean forceProcess) {
        try {
            var posted = request.post();
            if (posted.isEmpty() && !forceProcess) {
                this.logger.debug("{}, is discarded", target);
                target.status().discard().addDiagnose(DiagnosticCode.DISCARD, "Discarding duplicate document");
            } else {
                if (!target.getRelationsForStage().isEmpty()) {
                    this.publicatieClient.postRelations(target.getPlooiIntern().getDcnId(), Stage.INGRESS.toString(), target.getRelationsForStage());
                }
                this.publishingStateRepository.updateAndSave(target.getPlooiIntern().getDcnId(), PublishingState.State.TODO);
            }
        } catch (ClientException e) {
            target.status().discard().addDiagnose(DiagnosticCode.INTEGRATION, e.getMessage());
        }
    }
}

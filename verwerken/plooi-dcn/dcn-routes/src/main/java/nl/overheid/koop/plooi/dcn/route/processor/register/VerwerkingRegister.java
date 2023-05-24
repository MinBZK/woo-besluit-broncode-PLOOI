package nl.overheid.koop.plooi.dcn.route.processor.register;

import java.util.List;
import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.dcn.route.prep.IngressExecutionPrep;
import nl.overheid.koop.plooi.dcn.route.prep.StagePrep;
import nl.overheid.koop.plooi.dcn.route.processor.IdProcessor;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.registration.model.Diagnose;
import nl.overheid.koop.plooi.registration.model.Severity;
import nl.overheid.koop.plooi.registration.model.Verwerking;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Registers a {@link Verwerking}.
 */
@Service
public class VerwerkingRegister implements Processor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RegistrationClient registrationClient;

    public VerwerkingRegister(RegistrationClient regClient) {
        this.registrationClient = regClient;
    }

    @Override
    public void process(Exchange exchange) {
        var stage = exchange.getIn().getHeader(StagePrep.DCN_STAGE_HEADER, Stage.class);
        var procesId = exchange.getIn().getHeader(IngressExecutionPrep.DCN_EXECUTION_HEADER, String.class);
        var body = exchange.getIn().getBody();
        if (body instanceof Envelope plooiDoc) {
            if (!plooiDoc.status().isDiscarded() || !plooiDoc.status().getDiagnoses().isEmpty()) {
                registerVerwerking(plooiDoc, procesId, stage);
            }
        } else {
            var dcnId = exchange.getIn().getHeader(IdProcessor.HEADER_PLOOI_ID, String.class);
            registerVerwerking(procesId, dcnId, null, null, stage, null);
        }
    }

    private String registerVerwerking(Envelope envelope, String procesId, Stage stage) {
        if (envelope.getVerwerkingId() == null) {
            var verwerkingId = registerVerwerking(
                    procesId,
                    envelope.getPlooiIntern().getDcnId(),
                    envelope.getPlooiIntern().getSourceLabel(),
                    envelope.getPlooiIntern().getExtId(),
                    stage,
                    envelope.status().getDiagnoses());
            envelope.verwerkingId(verwerkingId);
            return verwerkingId;
        } else {
            this.logger.debug("Skipping registration for already registered {} event for {}", stage, envelope.getPlooiIntern().getDcnId());
            return envelope.getVerwerkingId();
        }
    }

    private String registerVerwerking(String procesId, String dcnId, String source, List<String> extIds, Stage stage, List<Diagnose> diagnoses) {
        if (source == null) {
            try {
                source = DcnIdentifierUtil.extractSource(dcnId);
            } catch (IllegalArgumentException e) {
                // If an illegal dcnId is the cause of an exception, we don't want exception handling to fail because of it
                dcnId = null;
            }
        }
        var verwerking = this.registrationClient.createProcesVerwerking(procesId, new Verwerking()
                .procesId(procesId)
                .dcnId(dcnId)
                .sourceLabel(source)
                .extIds(extIds)
                .stage((stage == null ? Stage.UNKNOWN : stage).toString())
                .diagnoses(diagnoses));
        this.logger.debug("Registered {} event {} for {} {} errors",
                stage, verwerking.getId(), dcnId, verwerking.getSeverity() == Severity.ERROR ? "with" : "without");
        return verwerking.getId();
    }
}

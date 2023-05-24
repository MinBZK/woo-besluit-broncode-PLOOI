package nl.overheid.koop.plooi.dcn.route.processor.register;

import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.dcn.route.common.httpexception.HttpOperationResponse;
import nl.overheid.koop.plooi.dcn.route.prep.IngressExecutionPrep;
import nl.overheid.koop.plooi.dcn.route.prep.StagePrep;
import nl.overheid.koop.plooi.dcn.route.processor.IdProcessor;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.registration.model.Exceptie;
import nl.overheid.koop.plooi.registration.model.Verwerking;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Registers a {@link Exceptie} for an error found while processing a document, if applicable together with a
 * {@link Verwerking}.
 */
@Service
public class ExceptieRegister {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RegistrationClient registrationClient;

    public ExceptieRegister(RegistrationClient regClient) {
        this.registrationClient = regClient;
    }

    public void registerError(Exchange exchange, Exception exception) {
        var procesId = exchange.getIn().getHeader(IngressExecutionPrep.DCN_EXECUTION_HEADER, String.class);
        var fromRoute = exchange.getProperty(Exchange.FAILURE_ENDPOINT, "", String.class);
        var body = exchange.getIn().getBody();
        if (body instanceof PlooiFile plooiFile && plooiFile.getCollectedIn() != null) {
            body = plooiFile.getCollectedIn();
        }
        var stage = exchange.getIn().getHeader(StagePrep.DCN_STAGE_HEADER, Stage.UNKNOWN, Stage.class).toString();
        var exceptie = new Exceptie().fromRoute(fromRoute);
        var verwerking = new Verwerking().procesId(procesId).stage(stage);
        if (body instanceof Envelope envelope) {
            verwerking.dcnId(envelope.getPlooiIntern().getDcnId())
                    .sourceLabel(envelope.getPlooiIntern().getSourceLabel())
                    .extIds(envelope.getPlooiIntern().getExtId());
            exceptie.messageBody(envelope instanceof PlooiEnvelope plooi ? PlooiBindings.plooiBinding().marshalToString(plooi.getPlooi()) : body.toString());
            envelope.status().discard();
        } else {
            verwerking.dcnId(exchange.getIn().getHeader(IdProcessor.HEADER_PLOOI_ID, String.class));
            exceptie.messageBody(body == null ? null : exchange.getIn().getBody(String.class));
        }
        if (exception != null) {
            exceptie.exceptionClass(exception.getClass().getName())
                    .exceptionMessage(ExceptionUtils.getRootCauseMessage(exception))
                    .exceptionStacktrace(ExceptionUtils.getStackTrace(exception));
            if (exception instanceof HttpOperationResponse httpException) {
                exceptie.statusCode(httpException.getStatusCode()).statusText(httpException.getStatusText());
            }
        }
        verwerking.setExceptie(exceptie);
        if (procesId == null) {
            this.logger.error("proces id is null, verwerking with exception is not posted {}", exceptie);
        } else {
            this.registrationClient.createProcesVerwerking(verwerking.getProcesId(), verwerking);
            this.logger.info("Registered {}", exceptie);
        }
    }
}

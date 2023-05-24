package nl.overheid.koop.plooi.dcn.route.processor;

import java.util.List;
import java.util.Optional;
import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes the {@link Envelope} in the message body, by applying {@link EnvelopeProcessing#process(Envelope)}.
 */
public class EnvelopeProcessor<T extends Envelope> implements Processor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final EnvelopeProcessing<T> processor;

    public EnvelopeProcessor(EnvelopeProcessing<T> envProcessor) {
        this.processor = envProcessor;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void process(Exchange exchange) throws InvalidPayloadException {
        var body = exchange.getIn().getBody();
        exchange.getIn().setBody(body instanceof List listBody ? listBody.stream().map(this::processEnvelope).toList() : processEnvelope(body));
    }

    private Envelope processEnvelope(Object body) {
        if (body instanceof Envelope envelope) {
            this.logger.debug("Processing PlooiDocument {} with {}", body, this.processor);
            var timer = ProcessorMetrics.start();
            @SuppressWarnings("unchecked")
            var processed = this.processor.process((T) envelope);
            timer.stop(this, this.processor, Optional.of(processed.getPlooiIntern().getSourceLabel()));
            return processed;
        } else {
            throw new IllegalArgumentException("Expected Envelope, but got " + body.getClass());
        }
    }
}

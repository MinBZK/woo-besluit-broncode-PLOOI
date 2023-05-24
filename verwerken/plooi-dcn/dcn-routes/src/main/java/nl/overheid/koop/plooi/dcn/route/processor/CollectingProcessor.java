package nl.overheid.koop.plooi.dcn.route.processor;

import java.util.Optional;
import nl.overheid.koop.plooi.dcn.component.types.DocumentCollecting;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collects a list of {@link nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope}s from the {@link PlooiFile} in
 * the message body, by applying {@link DocumentCollecting#collect(PlooiFile)}. The output message's body needs to be
 * {@link org.apache.camel.model.ProcessorDefinition#split() split} for further processing of each DeliveryEnvelope.
 */
public class CollectingProcessor implements Processor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DocumentCollecting collector;

    public CollectingProcessor(DocumentCollecting plooiCollector) {
        this.collector = plooiCollector;
    }

    @Override
    public void process(Exchange exchange) throws InvalidPayloadException {
        var timer = ProcessorMetrics.start();
        var plooiFile = exchange.getIn().getMandatoryBody(PlooiFile.class);
        this.logger.debug("Collecting {}", plooiFile);
        var envelopes = this.collector.collect(plooiFile);
        exchange.getIn().setBody(envelopes);
        timer.stop(this, this.collector, Optional.of(envelopes.get(0).getPlooiIntern().getSourceLabel()));
    }
}

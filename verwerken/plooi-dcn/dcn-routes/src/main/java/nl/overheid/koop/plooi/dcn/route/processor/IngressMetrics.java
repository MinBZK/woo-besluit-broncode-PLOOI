package nl.overheid.koop.plooi.dcn.route.processor;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;

public class IngressMetrics implements Processor {

    private static final String LABEL = "dcn_ingress";

    @Override
    public void process(Exchange exchange) throws InvalidPayloadException {
        var plooi = exchange.getIn().getMandatoryBody(Envelope.class);
        // Would be nice if we could tag the publisher, we but don't know that during ingress
        Metrics.counter(LABEL, Tags.of(
                Tag.of(ProcessorMetrics.SOURCE, plooi.getPlooiIntern().getSourceLabel()),
                Tag.of(ProcessorMetrics.DISCARDED, String.valueOf(plooi.status().isDiscarded()))))
                .increment();
    }
}

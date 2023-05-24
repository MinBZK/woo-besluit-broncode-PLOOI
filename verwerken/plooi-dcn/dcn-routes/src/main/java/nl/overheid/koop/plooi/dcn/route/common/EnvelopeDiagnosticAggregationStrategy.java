package nl.overheid.koop.plooi.dcn.route.common;

import java.util.Objects;
import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.registration.model.Diagnose;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

/** Returns the input exchange, after adding Diagnostic from the newExchange if its body is an newExchange. */
public class EnvelopeDiagnosticAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange, Exchange inputExchange) {
        return aggregate(inputExchange, newExchange);
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Objects.requireNonNull(oldExchange.getIn().getBody(DeliveryEnvelope.class), "A DeliveryEnvelope body is required")
                .status()
                .addDiagnose(newExchange.getIn().getBody(Diagnose.class));
        return oldExchange;
    }
}

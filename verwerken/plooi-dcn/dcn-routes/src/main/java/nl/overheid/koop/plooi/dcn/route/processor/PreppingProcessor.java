package nl.overheid.koop.plooi.dcn.route.processor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nl.overheid.koop.plooi.dcn.route.prep.Prepping;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initiates a DCN {@link nl.overheid.koop.plooi.dcn.model.Execution}, by applying
 * {@link Prepping#prepare(java.util.Map)}.
 */
public class PreppingProcessor implements Processor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Prepping> preppers;

    public PreppingProcessor(Prepping... inits) {
        this.preppers = Arrays.asList(inits);
    }

    @Override
    public void process(Exchange exchange) {
        var headers = exchange.getIn().getHeaders();
        this.preppers.forEach(i -> prepare(i, headers));
        this.logger.debug("Initiated headers {} with {}", headers, this.preppers);
        exchange.getIn().setHeaders(headers);
    }

    private void prepare(Prepping prepper, Map<String, Object> headers) {
        var timer = ProcessorMetrics.start();
        prepper.prepare(headers);
        timer.stop(this, prepper, Optional.empty());
    }
}

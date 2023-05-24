package nl.overheid.koop.plooi.dcn.route.processor;

import java.util.Optional;
import nl.overheid.koop.plooi.dcn.component.common.ProcessingException;
import nl.overheid.koop.plooi.dcn.component.types.ObjectProcessing;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes a String in the message body, by applying {@link ObjectProcessing#process(String)}.
 */
public class ObjectProcessor<T extends Object> implements Processor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectProcessing<T> processor;

    public ObjectProcessor(ObjectProcessing<T> objectProcessor) {
        this.processor = objectProcessor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Exchange exchange) throws InvalidPayloadException {
        var body = exchange.getIn().getMandatoryBody();
        try {
            var timer = ProcessorMetrics.start();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Processing {} with {}", StringUtils.abbreviate(body.toString(), 128), this.processor);
            }
            exchange.getIn().setBody(this.processor.process((T) body));
            timer.stop(this, this.processor, Optional.empty());
        } catch (ClassCastException e) {
            throw new ProcessingException("Cannot cast " + body + " for " + this.processor);
        }
    }
}

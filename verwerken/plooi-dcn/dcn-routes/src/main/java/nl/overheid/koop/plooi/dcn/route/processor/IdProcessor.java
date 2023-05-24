package nl.overheid.koop.plooi.dcn.route.processor;

import java.util.Optional;
import nl.overheid.koop.plooi.dcn.component.types.ObjectProcessing;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes the DCN id in the message body, by applying {@link ObjectProcessing#process(String)}.
 */
public class IdProcessor implements Processor {

    public static final String HEADER_PLOOI_ID = "dcn_identifier";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectProcessing<String> processor;

    public IdProcessor(ObjectProcessing<String> idProcessor) {
        this.processor = idProcessor;
    }

    @Override
    public void process(Exchange exchange) throws InvalidPayloadException {
        String dcnId = exchange.getIn().getMandatoryBody(String.class);
        if (StringUtils.isBlank(dcnId)) {
            throw new IllegalArgumentException("A document identifier is required in the body");
        } else {
            var timer = ProcessorMetrics.start();
            exchange.getIn().setHeader(HEADER_PLOOI_ID, dcnId);
            this.logger.debug("Processing {} with {}", dcnId, this.processor);
            exchange.getIn().setBody(this.processor.process(dcnId));
            timer.stop(this, this.processor, Optional.of(DcnIdentifierUtil.extractSource(dcnId)));
        }
    }
}

package nl.overheid.koop.plooi.dcn.route.processor;

import java.util.Optional;
import nl.overheid.koop.plooi.dcn.component.types.FileProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes the {@link PlooiFile} in the message body, by applying {@link FileProcessing#process(PlooiFile)}.
 */
public class FileProcessor implements Processor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final FileProcessing processor;

    public FileProcessor(FileProcessing fileProcessor) {
        this.processor = fileProcessor;
    }

    @Override
    public void process(Exchange exchange) throws InvalidPayloadException {
        var timer = ProcessorMetrics.start();
        PlooiFile plooiFile = exchange.getIn().getMandatoryBody(PlooiFile.class);
        this.logger.debug("Processing PlooiFile {} with {}", plooiFile, this.processor);
        exchange.getIn().setBody(this.processor.process(plooiFile));
        timer.stop(this, this.processor, Optional.empty());
    }
}

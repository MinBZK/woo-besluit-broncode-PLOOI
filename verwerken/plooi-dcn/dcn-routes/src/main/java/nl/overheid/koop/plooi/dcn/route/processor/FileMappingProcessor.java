package nl.overheid.koop.plooi.dcn.route.processor;

import java.util.Map;
import java.util.Optional;
import nl.overheid.koop.plooi.dcn.component.types.FileProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.document.map.ConfigurableFileMapper;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translates a string message body to a {@link PlooiFile}, by applying {@link ConfigurableFileMapper#populate(String)}.
 */
public class FileMappingProcessor implements Processor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final FileProcessing mapper;

    public FileMappingProcessor(FileProcessing plooiFileMapper) {
        this.mapper = plooiFileMapper;
    }

    @Override
    public void process(Exchange exchange) throws InvalidPayloadException {
        var timer = ProcessorMetrics.start();
        String body = exchange.getIn().getMandatoryBody(String.class);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Mapping {}", StringUtils.abbreviate(body, 160));
        }
        exchange.getIn().setBody(enrichFromHeaders(this.mapper.populate(body), exchange.getIn().getHeaders()));
        timer.stop(this, this.mapper, Optional.empty());
    }

    public static PlooiFile enrichFromHeaders(PlooiFile plooiFile, Map<String, Object> headers) {
        var file = plooiFile.getFile();
        if (StringUtils.isBlank(file.getMimeType())) {
            file.mimeType(headers.getOrDefault(Exchange.CONTENT_TYPE, "").toString().replaceAll(";.*", ""));
        }
        if (StringUtils.isBlank(file.getUrl())) {
            file.url(headers.getOrDefault(Exchange.HTTP_URI, "").toString());
        }
        return plooiFile;
    }
}

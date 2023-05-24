package nl.overheid.koop.plooi.dcn.route.common;

import java.io.InputStream;
import java.util.Objects;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.dcn.route.processor.FileMappingProcessor;
import nl.overheid.koop.plooi.registration.model.Diagnose;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Returns input PlooiFile with enriched content set. */
public class EnrichedFileContentAggregationStrategy implements AggregationStrategy {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        var file = Objects.requireNonNull(oldExchange.getIn().getBody(PlooiFile.class), "A PlooiFile body is required");
        if (file.hasContent()) {
            this.logger.debug("Skipping already downloaded file {}", file);
        } else if (newExchange == null) {
            returnDiagnostic(oldExchange, "Could not find " + file);
        } else {
            FileMappingProcessor.enrichFromHeaders(
                    file.contentSupplier(() -> newExchange.getIn().getBody(InputStream.class)),
                    newExchange.getIn().getHeaders());
        }
        return oldExchange;
    }

    private void returnDiagnostic(Exchange oldExchange, String message) {
        oldExchange.getIn()
                .setBody(new Diagnose()
                        .code(DiagnosticCode.INTEGRATION.toString())
                        .severity(DiagnosticCode.getSeverity(DiagnosticCode.INTEGRATION))
                        .message(Objects.requireNonNull(message, "Diagnostic message is required")));
        this.logger.debug(message);
    }
}

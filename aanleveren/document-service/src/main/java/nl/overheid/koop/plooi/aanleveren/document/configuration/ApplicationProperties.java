package nl.overheid.koop.plooi.aanleveren.document.configuration;

import nl.overheid.koop.plooi.aanleveren.document.infrastructure.dcn.DcnProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "documentservice")
public record ApplicationProperties(
        long maxFileSize,
        DcnProperties dcn) {
}

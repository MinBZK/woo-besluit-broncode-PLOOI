package nl.overheid.koop.plooi.aanleveren.metadata.configuration;

import nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn.DcnProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "metadataservice")
public record ApplicationProperties(String locationUrl, String pidEndpoint, DcnProperties dcn) {}

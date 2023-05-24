package nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "metadataservice.dcn")
public record DcnProperties(String dcnHost, String repositoryUrl, String registrationUrl) {
    public static final String SOURCE_LABEL = "plooi-api";
}

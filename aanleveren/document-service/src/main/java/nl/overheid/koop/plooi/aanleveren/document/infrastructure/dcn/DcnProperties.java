package nl.overheid.koop.plooi.aanleveren.document.infrastructure.dcn;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "documentservice.dcn")
public record DcnProperties(String repositoryUrl, String registrationUrl, String processUrl) {
    public static final String SOURCE_LABEL = "plooi-api";
}

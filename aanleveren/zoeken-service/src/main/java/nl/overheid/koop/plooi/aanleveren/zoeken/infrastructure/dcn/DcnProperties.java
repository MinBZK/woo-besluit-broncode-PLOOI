package nl.overheid.koop.plooi.aanleveren.zoeken.infrastructure.dcn;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zoekenservice.dcn")
public record DcnProperties(String searchUrl) {}

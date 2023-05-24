package nl.overheid.koop.plooi.plooiiamservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPublicKey;
import java.util.List;

@ConfigurationProperties(prefix = "plooi-iam-service")
public record ApplicationProperties(
        String keyStore,
        String keyPassword,
        RSAPublicKey caKeyLocation,
        List<String> allowedRedirectURIs) {
}

package nl.overheid.koop.plooi.dcn.admin.security;

import com.auth0.jwt.algorithms.Algorithm;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dcn.admin.jwt")
public class DCNJwtConfig {

    private String secret;
    private long expirationMs;
    private String tokenPrefix;
    private long refreshExpirationMs;

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return this.expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }

    public String getTokenPrefix() {
        return this.tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public long getRefreshExpirationMs() {
        return this.refreshExpirationMs;
    }

    public void setRefreshExpirationMs(long refreshExpirationMs) {
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public Algorithm jwtAlgorithm() {
        return Algorithm.HMAC256(this.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}

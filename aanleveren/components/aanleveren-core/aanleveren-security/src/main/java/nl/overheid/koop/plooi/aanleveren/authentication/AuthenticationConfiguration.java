package nl.overheid.koop.plooi.aanleveren.authentication;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;

@ConditionalOnProperty(
        value="aanleveren.security.enabled",
        havingValue = "true",
        matchIfMissing = true)
@Import(AanleverTokenFilter.class)
public class AuthenticationConfiguration {
}

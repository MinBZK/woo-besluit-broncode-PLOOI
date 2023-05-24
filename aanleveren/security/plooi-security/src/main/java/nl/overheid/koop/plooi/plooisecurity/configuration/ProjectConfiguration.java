package nl.overheid.koop.plooi.plooisecurity.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProjectConfiguration {

    @Bean
    public WebClient webClientIamService(final Environment env) {
        var url = env.getProperty("plooi.security.plooi-iam-service-url");
        if (url == null) {
            throw new RuntimeException("PLOOI IAM Service url is not set");
        }

        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}

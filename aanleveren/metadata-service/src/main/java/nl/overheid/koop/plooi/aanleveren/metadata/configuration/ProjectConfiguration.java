package nl.overheid.koop.plooi.aanleveren.metadata.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ProjectConfiguration {
    private final ApplicationProperties applicationProperties;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(applicationProperties.dcn().dcnHost())
                .build();
    }
}

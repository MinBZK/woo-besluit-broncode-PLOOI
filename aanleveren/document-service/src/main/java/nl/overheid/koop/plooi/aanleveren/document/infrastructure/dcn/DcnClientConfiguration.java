package nl.overheid.koop.plooi.aanleveren.document.infrastructure.dcn;

import lombok.RequiredArgsConstructor;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import nl.overheid.koop.plooi.repository.client.ApiClient;
import nl.overheid.koop.plooi.repository.client.DocumentenClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class DcnClientConfiguration {
    private final DcnProperties properties;

    @Bean
    public ApiClient repositoryApiClient() {
        return new ApiClient(properties.repositoryUrl());
    }

    @Bean
    @DependsOn("repositoryApiClient")
    public AanleverenClient aanleverenClient() {
        return new AanleverenClient(repositoryApiClient());
    }

    @Bean
    @DependsOn("repositoryApiClient")
    public DocumentenClient documentenClient() {
        return new DocumentenClient(repositoryApiClient());
    }

    @Bean
    @DependsOn("repositoryApiClient")
    public PublicatieClient publicatieClient() {
        return new PublicatieClient(repositoryApiClient());
    }

    @Bean
    public RegistrationClient registrationClient() {
        return new RegistrationClient(properties.registrationUrl());
    }

    @Bean
    public WebClient webClientDcn() {
        return WebClient.builder()
                .baseUrl(properties.processUrl())
                .build();
    }
}

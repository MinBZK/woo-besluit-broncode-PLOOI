package nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn;

import lombok.RequiredArgsConstructor;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import nl.overheid.koop.plooi.repository.client.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

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
    public RegistrationClient registrationClient() {
        return new RegistrationClient(properties.registrationUrl());
    }
}

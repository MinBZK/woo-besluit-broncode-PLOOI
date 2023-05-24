package nl.overheid.koop.plooi.dcn;

import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import nl.overheid.koop.plooi.repository.client.ApiClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class RepositoryConfiguration {

    private ApiClient repositoryApiClient;

    @Bean
    public ApiClient repositoryApiClient(@Value("${repository.base.url:}") String repositoryServiceUrl) {
        this.repositoryApiClient = new ApiClient(repositoryServiceUrl);
        return this.repositoryApiClient;
    }

    @Bean
    @DependsOn("repositoryApiClient")
    public AanleverenClient aanleverenClient() {
        return new AanleverenClient(this.repositoryApiClient);
    }

    @Bean
    @DependsOn("repositoryApiClient")
    public PublicatieClient publicatieClient() {
        return new PublicatieClient(this.repositoryApiClient);
    }
}

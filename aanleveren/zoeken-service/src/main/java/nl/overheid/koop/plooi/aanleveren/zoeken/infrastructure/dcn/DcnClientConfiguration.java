package nl.overheid.koop.plooi.aanleveren.zoeken.infrastructure.dcn;

import lombok.RequiredArgsConstructor;
import nl.overheid.koop.plooi.search.client.SearchClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DcnClientConfiguration {
    private final DcnProperties properties;

    @Bean
    public SearchClient searchClient() {
        return new SearchClient(properties.searchUrl());
    }
}

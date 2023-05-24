package nl.overheid.koop.plooi.aanleveren.clamav.configuration;

import fi.solita.clamav.ClamAVClient;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfiguration {

    @NonNull
    @Value("${clam.av.service.url}")
    private String clamAvHost;

    @Bean
    public ClamAVClient clamAVClient() {
        return new ClamAVClient(clamAvHost, 3310, 600000);
    }
}

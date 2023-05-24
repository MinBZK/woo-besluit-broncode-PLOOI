package nl.overheid.koop.plooi.dcn;

import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistrationConfiguration {

    @Bean
    public RegistrationClient registrationClient(@Value("${registration.base.url}") String registrationUrl) {
        return new RegistrationClient(registrationUrl);
    }
}

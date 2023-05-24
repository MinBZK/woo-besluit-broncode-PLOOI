package nl.overheid.koop.plooi.registration.service;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@PropertySource("classpath:application-it.properties")
@EnableJpaRepositories(basePackages = "nl.overheid.koop.plooi.registration.model")
@EntityScan(basePackages = "nl.overheid.koop.plooi.registration.model")
@ComponentScan(basePackages = "nl.overheid.koop.plooi.registration.model")
class IntegratedTestConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

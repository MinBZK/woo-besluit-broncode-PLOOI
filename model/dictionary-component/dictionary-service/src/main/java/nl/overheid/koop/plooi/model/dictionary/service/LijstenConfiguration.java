package nl.overheid.koop.plooi.model.dictionary.service;

import nl.overheid.koop.plooi.model.dictionary.TooiDictionaries;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class LijstenConfiguration implements WebMvcConfigurer {

    @Bean
    TooiDictionaries defaultTooiDictionaries() {
        return new TooiDictionaries();
    }

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_XML);
    }
}

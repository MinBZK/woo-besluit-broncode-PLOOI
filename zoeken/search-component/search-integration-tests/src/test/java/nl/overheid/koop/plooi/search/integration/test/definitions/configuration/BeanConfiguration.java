package nl.overheid.koop.plooi.search.integration.test.definitions.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/*
 * Instantiate singleton Spring beans here
 */
public class BeanConfiguration {

    @Bean
    public Gson getGson() {
        return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    }

    /*
     * This is where we define application properties and load it to the Spring Context without using SpringBoot test
     * dependency
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
        properties.setLocation(new ClassPathResource("application.properties"));
        properties.setIgnoreResourceNotFound(false);
        return properties;
    }

}

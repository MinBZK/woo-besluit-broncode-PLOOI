package nl.overheid.koop.plooi.aanleveren.metadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan
public class PlooiMetadataApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlooiMetadataApplication.class, args);
    }
}

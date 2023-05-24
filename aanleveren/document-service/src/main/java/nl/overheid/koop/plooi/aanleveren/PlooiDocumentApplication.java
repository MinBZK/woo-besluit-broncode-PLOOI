package nl.overheid.koop.plooi.aanleveren;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class PlooiDocumentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlooiDocumentApplication.class, args);
    }
}

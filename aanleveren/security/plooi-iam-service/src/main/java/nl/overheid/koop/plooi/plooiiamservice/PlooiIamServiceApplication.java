package nl.overheid.koop.plooi.plooiiamservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PlooiIamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlooiIamServiceApplication.class, args);
    }

}

package nl.overheid.koop.plooi.aanleveren;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PlooiZoekenApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlooiZoekenApplication.class, args);
    }
}

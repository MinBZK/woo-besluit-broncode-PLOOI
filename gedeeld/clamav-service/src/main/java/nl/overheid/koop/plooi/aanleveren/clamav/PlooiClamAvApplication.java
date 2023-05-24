package nl.overheid.koop.plooi.aanleveren.clamav;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class PlooiClamAvApplication {

    public PlooiClamAvApplication() {}

    public static void main(String[] args) {
        SpringApplication.run(PlooiClamAvApplication.class, args);
    }
}

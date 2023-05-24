package nl.overheid.koop.plooi.aanleveren.waardelijst;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition
@SpringBootApplication
public class PlooiAanleverApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlooiAanleverApplication.class, args);
    }

}

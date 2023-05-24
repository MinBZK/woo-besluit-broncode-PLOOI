package nl.overheid.koop.plooi.model.dictionary.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "nl.overheid.koop.plooi")
public class LijstenApplication {

    public static void main(String[] args) {
        SpringApplication.run(LijstenApplication.class, args);
    }
}

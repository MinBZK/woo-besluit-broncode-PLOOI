package nl.overheid.koop.plooi.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "nl.overheid.koop.plooi")
public class Service {

    public static void main(String[] args) {
        SpringApplication.run(Service.class, args);
    }
}

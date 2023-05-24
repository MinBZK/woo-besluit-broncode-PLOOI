package nl.overheid.koop.plooi.dcn;

import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;

@SpringBootApplication(scanBasePackages = "nl.overheid.koop.plooi", exclude = SolrAutoConfiguration.class)
@CamelOpenTelemetry
public class PlooiDcnApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlooiDcnApplication.class, args);
    }
}

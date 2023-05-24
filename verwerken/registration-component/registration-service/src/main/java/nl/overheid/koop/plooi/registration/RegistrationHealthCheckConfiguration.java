package nl.overheid.koop.plooi.registration;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RegistrationHealthCheckConfiguration {
    public RegistrationHealthCheckConfiguration(MeterRegistry registry, HealthEndpoint healthEndpoint) {
        Gauge.builder("registration-health", healthEndpoint, this::getStatusCode).strongReference(true).register(registry);
    }

    private int getStatusCode(HealthEndpoint health) {
        HealthComponent healthComponent = health.health();
        switch (healthComponent.getStatus().getCode()) {
            case "UP":
                return 3;
            case "OUT_OF_SERVICE":
                return 2;
            case "DOWN":
                return 1;
            case "UNKNOWN":
            default:
                return 0;
        }
    }
}

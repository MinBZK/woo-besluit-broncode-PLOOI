package nl.overheid.koop.plooi.repository;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ServiceHealthCheck {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ServiceHealthCheck(MeterRegistry registry, HealthEndpoint healthEndpoint) {
        Gauge.builder("reposotory-health", healthEndpoint, this::getStatusCode).strongReference(true).register(registry);
    }

    private int getStatusCode(HealthEndpoint health) {
        return switch (health.health().getStatus().getCode()) {
            case "UP" -> 3;
            case "OUT_OF_SERVICE" -> 2;
            case "DOWN" -> 2;
            case "UNKNOWN" -> 1;
            default -> 0;
        };
    }
}

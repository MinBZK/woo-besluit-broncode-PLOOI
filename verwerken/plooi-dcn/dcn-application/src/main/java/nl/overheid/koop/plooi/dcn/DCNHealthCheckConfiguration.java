package nl.overheid.koop.plooi.dcn;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.health.SystemHealth;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DCNHealthCheckConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DCNHealthCheckConfiguration(MeterRegistry registry, HealthEndpoint healthEndpoint) {
        Gauge.builder("dcn-health", healthEndpoint, this::getStatusCode).strongReference(true).register(registry);
    }

    private int getStatusCode(HealthEndpoint health) {
        HealthComponent healthComponent = health.health();

        return switch (health.health().getStatus().getCode()) {
            case "UP" -> 3;
            case "OUT_OF_SERVICE" -> 2;
            case "DOWN" -> {
                this.logDownComponents(healthComponent);
                yield 1;
            }
            case "UNKNOWN" -> 0;
            default -> 0;
        };
    }

    private void logDownComponents(HealthComponent healthComponent) {
        String downComponents = ((SystemHealth) healthComponent).getComponents()
                .values()
                .stream()
                .filter(component -> component.getStatus().getCode().equals(Status.DOWN.getCode()))
                .flatMap(hc -> ((Health) hc).getDetails().entrySet().stream())
                .filter(e -> e.getValue().equals(Status.DOWN.getCode()))
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", ", "{", "}"));
        this.logger.error("DCN component(s) {} is {} ", downComponents, healthComponent.getStatus().getCode());
    }
}

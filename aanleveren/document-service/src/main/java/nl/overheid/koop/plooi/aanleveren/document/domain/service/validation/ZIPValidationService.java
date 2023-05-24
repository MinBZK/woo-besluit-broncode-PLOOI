package nl.overheid.koop.plooi.aanleveren.document.domain.service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.overheid.koop.plooi.aanleveren.document.configuration.ApplicationProperties;
import nl.overheid.koop.plooi.aanleveren.models.MeasuringPoint;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import static net.logstash.logback.marker.Markers.appendRaw;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZIPValidationService implements Validator {
    private final ApplicationProperties properties;

    @Override
    public boolean isValid(final byte[] resource) {
        log.info(appendRaw("measuringpoint", MeasuringPoint.builder()
                .description("Resource is: ZIP")
                .processIdentifier(MDC.get("ProcessIdentifier"))
                .build().toString()), "MeasuringPoint marker added");

        checkResourceNotTooLarge(resource, properties.maxFileSize());
        return !ArrayUtils.isEmpty(resource);
    }

    @Override
    public boolean isCorrectType(final byte[] resource) {
        return resource[0] == 0x50 && resource[1] == 0x4B && resource[2] == 0x03 && resource[3] == 0x04 ||
                resource[0] == 0x50 && resource[1] == 0x4B && resource[2] == 0x05 && resource[3] == 0x06 ||
                resource[0] == 0x50 && resource[1] == 0x4B && resource[2] == 0x07 && resource[3] == 0x08;
    }

}

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
public class PDFValidationService implements Validator {
    private final ApplicationProperties properties;

    @Override
    public boolean isValid(final byte[] resource) {
        log.info(appendRaw("measuringpoint", MeasuringPoint.builder()
                .description("Resource is: PDF")
                .processIdentifier(MDC.get("ProcessIdentifier"))
                .build().toString()), "MeasuringPoint marker added");

        checkResourceNotTooLarge(resource, properties.maxFileSize());
        return !ArrayUtils.isEmpty(resource);
    }

    @Override
    public boolean isCorrectType(final byte[] resource) {
        return (resource[0] == 0x25 && resource[1] == 0x50 && resource[2] == 0x44 && resource[3] == 0x46);
    }
}

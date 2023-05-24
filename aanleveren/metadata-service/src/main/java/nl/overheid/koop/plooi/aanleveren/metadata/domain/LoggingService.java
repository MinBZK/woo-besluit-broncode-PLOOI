package nl.overheid.koop.plooi.aanleveren.metadata.domain;

import lombok.extern.slf4j.Slf4j;
import nl.overheid.koop.plooi.aanleveren.models.MeasuringPoint;
import org.springframework.http.HttpStatus;

import static net.logstash.logback.marker.Markers.appendRaw;

@Slf4j
public class LoggingService {
    private static final String MEASURINGPOINT = "measuringpoint";
    private static final String MEASURINGPOINT_ADDED = "MeasuringPoint marker added";

    public static void offeredMetadata(final String processId, final String description) {
        log.info(appendRaw(
                        MEASURINGPOINT,
                        MeasuringPoint.builder()
                                .description(description)
                                .processIdentifier(processId)
                                .build()
                                .toString()),
                MEASURINGPOINT_ADDED);
    }

    public static void createdMetadata(final String processId, final String pid) {
        log.info(appendRaw(
                        MEASURINGPOINT,
                        MeasuringPoint.builder()
                                .description("Validatie Metadata Afgerond")
                                .processIdentifier(processId)
                                .pid(pid)
                                .statuscode(HttpStatus.CREATED.value())
                                .build()
                                .toString()),
                MEASURINGPOINT_ADDED);
    }
}

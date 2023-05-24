package nl.overheid.koop.plooi.aanleveren.clamav.endpoints;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.api.ClamApi;
import nl.overheid.koop.plooi.aanleveren.clamav.service.ClamAVService;
import nl.overheid.koop.plooi.aanleveren.models.MeasuringPoint;
import nl.overheid.koop.plooi.aanleveren.models.ScanPassed;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

import static net.logstash.logback.marker.Markers.appendRaw;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClamAVEndpoint implements ClamApi {
    private final ClamAVService clamAVService;

    @Override
    public ResponseEntity<ScanPassed> scanDocument(@Valid byte[] fileForScan) {
        val processIdentifier = UUID.randomUUID().toString();
        MDC.put("ProcessIdentifier", processIdentifier);
        log.info(appendRaw("measuringpoint", MeasuringPoint.builder()
                .description("ClamAV has received a document")
                .processIdentifier(MDC.get("ProcessIdentifier"))
                .build().toString()), "MeasuringPoint marker added");
        log.info("Clam Documentsize: " + fileForScan.length);

        if (clamAVService.isFileOk(fileForScan)) {
            log.info("File is OK");
            return ResponseEntity.ok().build();
        } else {
            log.info("File is NOT ok");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

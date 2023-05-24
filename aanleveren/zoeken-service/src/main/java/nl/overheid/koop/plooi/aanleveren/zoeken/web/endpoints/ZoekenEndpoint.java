package nl.overheid.koop.plooi.aanleveren.zoeken.web.endpoints;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.models.MeasuringPoint;
import nl.overheid.koop.plooi.aanleveren.zoeken.domain.ZoekenService;
import nl.overheid.koop.plooi.search.model.SearchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static net.logstash.logback.marker.Markers.appendRaw;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ZoekenEndpoint {
    private final ZoekenService zoekenService;

    @GetMapping(value = "/overheid/openbaarmaken/api/v0/_zoek",
            produces = "application/json")
    public ResponseEntity<SearchResponse> searchDocuments(@RequestParam final long start) {
        if (start < 0) {
            throw new IllegalArgumentException("Start parameter is not valid");
        }

        val searchResponse = zoekenService.getDocuments(start);

        val measuringPoint = MeasuringPoint.builder()
                .pid("DITISEENPID")
                .description("DESCRIPTION")
                .processIdentifier(UUID.randomUUID().toString())
                .statuscode(200)
                .build();
        log.info(appendRaw("measuringpoint", measuringPoint.toString()), null);

        return ResponseEntity.ok(searchResponse);
    }
}

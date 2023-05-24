package nl.overheid.koop.plooi.aanleveren.clamav.endpoints;

import lombok.RequiredArgsConstructor;
import nl.overheid.koop.plooi.aanleveren.api.PingApi;
import nl.overheid.koop.plooi.aanleveren.clamav.service.ClamAVService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PingClamAVEndpoint implements PingApi {
    private final ClamAVService clamAVService;

    @Override
    public ResponseEntity<String> pingDeamon() {
        clamAVService.pingClamAVDeamon();
        return ResponseEntity.ok().build();
    }
}

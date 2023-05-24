package nl.overheid.koop.plooi.repository.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class RepositoryController implements RepositoryApi {

    @Override
    @Deprecated(forRemoval = false)
    public ResponseEntity<String> legacyFile(String id, String versieNummer, String label, String bestandsnaam) {
        return ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION, DocumentenController.buildFileUrl(id, label)).build();
    }
}

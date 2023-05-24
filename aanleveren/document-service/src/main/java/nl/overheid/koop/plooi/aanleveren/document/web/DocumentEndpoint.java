package nl.overheid.koop.plooi.aanleveren.document.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.document.domain.service.document.DocumentService;
import nl.overheid.koop.plooi.aanleveren.document.infrastructure.dcn.DcnClient;
import nl.overheid.koop.plooi.aanleveren.document.web.response.AanleverenResponseDocument;
import nl.overheid.koop.plooi.aanleveren.document.web.response.AanleverenResponseSuccesPost;
import nl.overheid.koop.plooi.aanleveren.document.web.response.AanleverenResponseSuccesPut;
import nl.overheid.koop.plooi.aanleveren.models.MeasuringPoint;
import org.slf4j.MDC;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static net.logstash.logback.marker.Markers.appendRaw;
@Slf4j
@RestController
@RequiredArgsConstructor
public class DocumentEndpoint {
    private static final String CREATE_DOCUMENT_ACCEPTED = "DocumentAccepted";
    private static final String REPLACE_DOCUMENT_ACCEPTED = "ReplaceDocumentAccepted";
    private static final String TITLE = "Het document is in behandeling genomen.";
    private static final String DETAIL = "De aanlevering is ontvangen en wordt gecontroleerd. Voor het raadplegen van de verwerkingsstatus kunt u bij de 'RaadpleegService' een request indienen.";

    private static final String MEASURINGPOINT = "measuringpoint";
    private static final String MEASURINGPOINT_ADDED = "MeasuringPoint marker added";
    private final DocumentService documentService;
    private final DcnClient dcnClient;
    private final HttpServletRequest request;

    @GetMapping(
            value = "/overheid/openbaarmaken/api/v0/documenten/{documentId}",
            produces = { "application/pdf", "application/zip" }
    )
    public ResponseEntity<Resource> getDocumentInstance(@PathVariable final String documentId) {
        MDC.put("ProcessIdentifier", dcnClient.registerProcess("Opvragen", "Document is being retrieved"));
        val document = documentService.findDocument(documentId);
        return ResponseEntity.ok(document);
    }

    @PostMapping(
            value = "/overheid/openbaarmaken/api/v0/documenten/{documentId}",
            produces = { "application/json" },
            consumes = { "application/pdf", "application/zip" }
    )
    public ResponseEntity<Object> createDocumentInstance(@PathVariable final String documentId, @RequestBody final byte[] document) {
        MDC.put("ProcessIdentifier", request.getHeader("X-Session-ID"));
        log.info(appendRaw(MEASURINGPOINT, MeasuringPoint.builder()
                .description(String.format("Request has arrived with document lenght: %d", document.length))
                .processIdentifier(MDC.get("ProcessIdentifier"))
                .build().toString()), MEASURINGPOINT_ADDED);

        return publishDocument(documentId, document, CREATE_DOCUMENT_ACCEPTED);
    }

    @PutMapping(
            value = "/overheid/openbaarmaken/api/v0/documenten/{documentId}",
            produces = { "application/json" },
            consumes = { "application/pdf", "application/zip" }
    )
    public ResponseEntity<Object> replaceDocumentInstance(@PathVariable final String documentId, @RequestBody final byte[] document) {
        MDC.put("ProcessIdentifier", request.getHeader("X-Session-ID"));
        return publishDocument(documentId, document, REPLACE_DOCUMENT_ACCEPTED);
    }

    @DeleteMapping(value = "/overheid/openbaarmaken/api/v0/documenten/{documentId}")
    public ResponseEntity<Void> deleteDocumentInstance(@PathVariable final String documentId) {
        MDC.put("ProcessIdentifier", dcnClient.registerProcess("Intrekken", "Document is being deleted"));
        documentService.deleteDocument(documentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private ResponseEntity<Object> publishDocument(final String documentId, final byte[] document, final String responseId) {
        documentService.publishDocument(documentId, document);

        if (CREATE_DOCUMENT_ACCEPTED.equals(responseId)) {
            return ResponseEntity.accepted().body(new AanleverenResponseSuccesPost(
                    TITLE,
                    202,
                    DETAIL,
                    LocalDateTime.now(ZoneId.of("Europe/Amsterdam")).toString(),
                    new AanleverenResponseDocument(MDC.get("PID"))
            ));
        } else if (REPLACE_DOCUMENT_ACCEPTED.equals(responseId)) {
            return ResponseEntity.accepted().body(new AanleverenResponseSuccesPut(
                    TITLE,
                    202,
                    DETAIL,
                    LocalDateTime.now(ZoneId.of("Europe/Amsterdam")).toString(),
                    new AanleverenResponseDocument(MDC.get("PID"))
            ));
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
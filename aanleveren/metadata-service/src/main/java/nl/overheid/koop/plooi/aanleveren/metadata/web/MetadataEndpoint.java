package nl.overheid.koop.plooi.aanleveren.metadata.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.metadata.configuration.ApplicationProperties;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.LoggingService;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.MetadataService;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.NotValidUUIDException;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.PlooiJsonParseException;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.service.identifier.PlooiIdentifierService;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.service.validation.ValidationService;
import nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn.DcnClient;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MetadataEndpoint {
    private static final String TRIGGER_TYPE_AANLEVERING = "Aanlevering";
    private static final String TRIGGER_AANLEVERING_METADATA = "Aanlevering metadata";
    private static final String SESSION_ID_HEADER = "X-Session-ID";
    public static final String PROCESS_IDENTIFIER = "ProcessIdentifier";

    private final MetadataService metadataService;
    private final ValidationService validationService;
    private final PlooiIdentifierService plooiIdentifierService;
    private final DcnClient dcnClient;
    private final ApplicationProperties applicationProperties;

    @GetMapping(
            value = "/overheid/openbaarmaken/api/v0/metadata/{documentId}",
            produces = "application/json")
    public ResponseEntity<String> getMetadataInstance(@PathVariable final String documentId) {
        MDC.put(PROCESS_IDENTIFIER, UUID.randomUUID().toString());
        checkIfValidUuidSyntax(documentId);
        return ResponseEntity.ok(dcnClient.searchMetadataByUUID(documentId));
    }

    @PostMapping(
            value = "/overheid/openbaarmaken/api/v0/metadata",
            produces = "application/json",
            consumes = "application/json")
    public ResponseEntity<String> createMetadata(@RequestBody final String metadata) {
        val processId = UUID.randomUUID().toString();
        MDC.put(PROCESS_IDENTIFIER, processId);

        LoggingService.offeredMetadata(processId, "Metadata Aangeboden");

        if (!metadataService.isAuthorized(metadata)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        val documentId = plooiIdentifierService.createDocumentId();
        val metadataWithPid = metadataService
                .createMetadata(metadata, documentId);
        val dcnProcessId = metadataService.registerProcess(TRIGGER_TYPE_AANLEVERING, TRIGGER_AANLEVERING_METADATA);

        return ResponseEntity
                .created(URI.create(applicationProperties.locationUrl() + documentId))
                .header(SESSION_ID_HEADER, dcnProcessId)
                .body(metadataWithPid);
    }

    @PutMapping(
            value = "/overheid/openbaarmaken/api/v0/metadata/{documentId}",
            produces = "application/json",
            consumes = "application/json")
    public ResponseEntity<String> replaceMetadataInstance(
            @PathVariable final String documentId,
            @RequestBody final String body) {
        val processId = UUID.randomUUID().toString();
        MDC.put(PROCESS_IDENTIFIER, processId);

        LoggingService.offeredMetadata(processId, "Metadata Vervangen");

        validationService.isMetadataValid(body, true);
        val currentMetadata = dcnClient.searchMetadataByUUID(documentId);
        validationService.checkMetadata(currentMetadata, body);
        val pid = plooiIdentifierService.getPID(documentId);
        val metadataModelWithPID = response2xx(pid, body);

        dcnClient.replaceMetadataByUUID(documentId, body);

        val dcnProcessId = metadataService.registerProcess(TRIGGER_TYPE_AANLEVERING, TRIGGER_AANLEVERING_METADATA);

        LoggingService.createdMetadata(processId, pid);

        return ResponseEntity
                .created(URI.create(applicationProperties.locationUrl() + documentId))
                .header(SESSION_ID_HEADER, dcnProcessId)
                .body(metadataModelWithPID);
    }

    private String response2xx(String pid, String metadataModel) {
        val mapper = new ObjectMapper();
        try {
            val resultAsString = "{\"pid\":\"" + pid + "\"}";
            val rootNode = (ObjectNode) mapper.readTree(metadataModel);
            val node = (ObjectNode) mapper.readTree(metadataModel).path("document");
            if (!node.has("pid")) {
                val fieldObjects = (ObjectNode) (mapper.readTree(resultAsString));
                rootNode.with("document").setAll(fieldObjects);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
            }
        } catch (JsonProcessingException e) {
            throw new PlooiJsonParseException(e.getMessage(), e);
        }
        return metadataModel;
    }

    private void checkIfValidUuidSyntax(final String uuid) {
        val uuidRegex = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
        if (uuid == null || !uuidRegex.matcher(uuid).matches()) {
            throw new NotValidUUIDException();
        }
    }

}


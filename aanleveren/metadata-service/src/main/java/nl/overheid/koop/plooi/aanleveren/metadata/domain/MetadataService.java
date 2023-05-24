package nl.overheid.koop.plooi.aanleveren.metadata.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.PlooiJsonParseException;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.service.identifier.PlooiIdentifierService;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.service.validation.ValidationService;
import nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn.DcnClientService;
import nl.overheid.koop.plooi.plooisecurity.domain.policy.AttributePolicyEnforcement;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import static nl.overheid.koop.plooi.aanleveren.metadata.web.MetadataEndpoint.PROCESS_IDENTIFIER;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final DcnClientService dcnClient;
    private final ValidationService validationService;
    private final PlooiIdentifierService plooiIdentifierService;
    private final AttributePolicyEnforcement policyEnforcement;

    public String createMetadata(final String metadata, final String documentId) {
        validationService.isMetadataValid(metadata);

        val pid = plooiIdentifierService.getPID(documentId);

        val metadataWithPid = addPid(pid, metadata);
        dcnClient.postMetadata(documentId, metadataWithPid);

        LoggingService.createdMetadata(MDC.get(PROCESS_IDENTIFIER), pid);

        return metadataWithPid;
    }

    public boolean isAuthorized(final String metadata) {
        val policyContext = validationService.toPolicyContext(metadata);
        return policyEnforcement.enforcePolicy(policyContext);
    }

    public String registerProcess(final String triggerType, final String trigger) {
        return dcnClient.registerProces(triggerType, trigger);
    }

    private String addPid(String pid, String metadata) {
        val mapper = new ObjectMapper();
        try {
            val resultAsString = "{\"pid\":\"" + pid + "\"}";
            val rootNode = (ObjectNode) mapper.readTree(metadata);
            val node = (ObjectNode) mapper.readTree(metadata).path("document");
            if (!node.has("pid")) {
                val fieldObjects = (ObjectNode) (mapper.readTree(resultAsString));
                rootNode.with("document").setAll(fieldObjects);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
            }
        } catch (JsonProcessingException e) {
            throw new PlooiJsonParseException(e.getMessage(), e);
        }
        return metadata;
    }
}

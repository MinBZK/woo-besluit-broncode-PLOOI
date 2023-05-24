package nl.overheid.koop.plooi.aanleveren.metadata.domain.service.validation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.*;
import nl.overheid.koop.plooi.plooisecurity.domain.policy.Attributes;
import nl.overheid.koop.plooi.plooisecurity.domain.policy.PolicyContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.ExceptionConstants.DETAIL_INVALID_JSON;

@Slf4j
@Service
public class ValidationService {
    private static final String METADATA_YAML = "metadata.yaml";
    private static final String METADATA_WITH_PID_YAML = "metadataWithPid.yaml";
    private static final String DOCUMENT_NODE = "document";
    private static final String PUBLISHER_NODE = "publisher";
    private static final String ID_NODE = "id";
    private static final String PID_NODE = "pid";
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;

    public ValidationService() {
        jsonMapper = new ObjectMapper();
        yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    public PolicyContext toPolicyContext(final String metadata) {
        val mappedMetadata = getJsonNode(metadata);

        return new PolicyContext("crud_document",
                new Attributes(
                        getPublisher(mappedMetadata),
                        getDocumentsoorten(mappedMetadata)));
    }

    private String getPublisher(final JsonNode metadata) {
        val publisher = metadata.get(DOCUMENT_NODE).get(PUBLISHER_NODE).get(ID_NODE).textValue();
        return publisher.substring(publisher.lastIndexOf("/") + 1);
    }

    private List<String> getDocumentsoorten(final JsonNode metadata) {
        val documentsoorten = new ArrayList<String>();
        val soorten = metadata.get(DOCUMENT_NODE).get("classificatiecollectie").get("documentsoorten");

        for (val node : soorten) {
            val documentsoort = node.get(ID_NODE).textValue();
            documentsoorten.add(documentsoort);
        }

        return documentsoorten;
    }

    public boolean isMetadataValid(final String jsonMetadataFile) {
        return isMetadataValid(jsonMetadataFile, false);
    }

    public boolean isMetadataValid(final String jsonMetadataFile, final boolean isWithPid) {
        val jsonSchema = getJsonSchema(isWithPid);
        val jsonNode = getJsonNode(jsonMetadataFile);
        val validationMessages = jsonSchema.validate(jsonNode);
        if (!validationMessages.isEmpty()) {
            throw new PlooiJsonValidationException(validationMessages);
        }
        if (isScriptTagFound(jsonMetadataFile)) {
            throw new PlooiJsonParseException(DETAIL_INVALID_JSON);
        }

        log.info("Metadata-validation succeeded.");
        return true;
    }

    public void checkMetadata(final String currentMetadata, final String newMetadata) {
        if (currentMetadata == null || currentMetadata.isEmpty()) {
            throw new NotFoundException();
        }
        val jsonCurrentMetadata = getJsonNode(currentMetadata);
        val jsonNewMetadata = getJsonNode(newMetadata);

        if (jsonNewMetadata.get(DOCUMENT_NODE).get(PID_NODE) == null) {
            throw new IdentifierException("PID has no value");
        }

        if (!jsonCurrentMetadata.get(DOCUMENT_NODE).get(PID_NODE).asText()
                .equals(jsonNewMetadata.get(DOCUMENT_NODE).get(PID_NODE).asText())) {
            throw new ConflictException();
        }
        if (!jsonCurrentMetadata.get(DOCUMENT_NODE).get(PUBLISHER_NODE).get(ID_NODE).asText()
                .equals(jsonNewMetadata.get(DOCUMENT_NODE).get(PUBLISHER_NODE).get(ID_NODE).asText())) {
            throw new ForbiddenException();
        }
    }

    private JsonSchema getJsonSchema(final boolean isWithPid) {
        val factory = JsonSchemaFactory.builder(JsonSchemaFactory.getInstance(VersionFlag.V201909))
                .objectMapper(yamlMapper)
                .build();
        val yamlFile = isWithPid ? METADATA_WITH_PID_YAML : METADATA_YAML;
        return factory.getSchema(getValidationSchema(yamlFile));
    }

    private JsonNode getJsonNode(final String jsonMetadataFile) {
        val dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        jsonMapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
        jsonMapper.setDateFormat(dateFormatter);
        try {
            return jsonMapper.readTree(jsonMetadataFile);
        } catch (JsonProcessingException e) {
            throw new PlooiJsonParseException(DETAIL_INVALID_JSON, e);
        }
    }

    private JsonNode getValidationSchema(final String validationSchemaFileName) {
        try {
            return yamlMapper.readTree(new ClassPathResource("metadata/" + validationSchemaFileName).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isScriptTagFound(final String jsonMetadataFile) {
        return jsonMetadataFile.contains("<script>");
    }
}

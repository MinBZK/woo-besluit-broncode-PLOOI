package nl.overheid.koop.plooi.aanleveren.document.domain.service.document;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.overheid.koop.plooi.aanleveren.document.domain.exceptions.ResourceTypeException;
import nl.overheid.koop.plooi.aanleveren.document.domain.service.validation.ValidationFactory;
import nl.overheid.koop.plooi.aanleveren.document.infrastructure.dcn.DcnClient;
import nl.overheid.koop.plooi.aanleveren.models.MeasuringPoint;
import org.slf4j.MDC;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static net.logstash.logback.marker.Markers.appendRaw;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private static final String MEASURINGPOINT = "measuringpoint";
    private static final String MEASURINGPOINT_ADDED = "MeasuringPoint marker added";
    private static final String PROCESSIDENTIFIER = "ProcessIdentifier";

    private final ValidationFactory validationFactory;
    private final DcnClient dcnClient;

    public void publishDocument(final String documentId, final byte[] document) {
        checkValidUUID(documentId);

        if (!(validationFactory.getCorrectValidator(document).isValid(document))) {
            throw new ResourceTypeException("Content is not correct");
        }

        log.info(appendRaw(MEASURINGPOINT, MeasuringPoint.builder()
                .description("Validatie Document Afgerond")
                .processIdentifier(MDC.get(PROCESSIDENTIFIER))
                .pid(MDC.get("PID"))
                .statuscode(HttpStatus.ACCEPTED.value())
                .build().toString()), MEASURINGPOINT_ADDED);

        dcnClient.postDocument(documentId, document);
    }

    public Resource findDocument(final String documentId) {
        checkValidUUID(documentId);
        return dcnClient.searchDocument(documentId);
    }


    public void deleteDocument(final String documentId) {
        checkValidUUID(documentId);
        dcnClient.deleteDocument(documentId);
    }

    private void checkValidUUID(final String uuid) {
        final Pattern uuidRegex = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
        if (uuid == null || !uuidRegex.matcher(uuid).matches()) {
            throw new IllegalArgumentException("UUID is niet geldig");
        }
    }

}

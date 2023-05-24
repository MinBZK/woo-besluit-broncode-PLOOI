package nl.overheid.koop.plooi.aanleveren.metadata.domain.service.identifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.metadata.configuration.ApplicationProperties;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.IdentifierException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlooiIdentifierService {
    private final ApplicationProperties applicationProperties;

    public String createDocumentId() {
        val documentId = UUID.randomUUID().toString();
        log.info("Generated Document identifier: {}", documentId);
        return documentId;
    }

    public String getPID(final String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IdentifierException("UUID is missing.");
        }

        if (applicationProperties.pidEndpoint() == null || applicationProperties.pidEndpoint().isBlank()) {
            throw new IdentifierException("pid.endpoint has no value");
        }

        val pidSB = new StringBuilder(applicationProperties.pidEndpoint());
        if (!applicationProperties.pidEndpoint().endsWith("/")) {
            pidSB.append("/");
        }
        pidSB.append(identifier);
        val pid = pidSB.toString();
        log.info("Generated PID: {}", pid);

        return pid;
    }
}

package nl.overheid.koop.plooi.aanleveren.document.infrastructure.dcn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import nl.overheid.koop.plooi.repository.client.DocumentenClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.slf4j.MDC;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static nl.overheid.koop.plooi.aanleveren.document.infrastructure.dcn.DcnProperties.SOURCE_LABEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class DcnClient {
    private final AanleverenClient aanleverenClient;
    private final DocumentenClient documentenClient;
    private final RegistrationClient registrationClient;
    private final PublicatieClient publicatieClient;
    private final WebClient webclientDcn;

    public void postDocument(final String documentId, final byte[] document) {
        aanleverenClient.createRequest(
                new Versie().oorzaak(Versie.OorzaakEnum.AANLEVERING),
                        SOURCE_LABEL,
                        documentId)
                .addPart("file", document)
                .post();
        signalToProcess("VERWERKING", MDC.get("ProcessIdentifier"), documentId);
    }

    public Resource searchDocument(final String documentId) {
        return new InputStreamResource(documentenClient.getFileContent(documentId, "file"));
    }

    public void deleteDocument(final String documentId) {
        publicatieClient.delete(documentId, Versie.OorzaakEnum.INTREKKING,"Verwijderen van document");
        signalToProcess("INTREKKING", MDC.get("ProcessIdentifier"), documentId);
    }

    public String registerProcess(final String triggerType, final String trigger) {
        return registrationClient.createProces(SOURCE_LABEL, triggerType, trigger).getId();
    }

    private void signalToProcess(final String action, final String sessionId, final String documentId) {
        webclientDcn.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/verwerken/{action}/{procesId}")
                        .build(action, sessionId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(String.format("[\"plooi-api-%s\"]", documentId)))
                .retrieve()
                .bodyToMono(HttpStatus.class)
                .blockOptional();
    }
}

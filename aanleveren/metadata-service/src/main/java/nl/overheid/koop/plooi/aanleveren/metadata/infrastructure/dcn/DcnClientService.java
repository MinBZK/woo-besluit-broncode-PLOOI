package nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn.DcnProperties.SOURCE_LABEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class DcnClientService {
    private final AanleverenClient aanleverenClient;
    private final RegistrationClient registrationClient;

    public void postMetadata(final String documentId, final String metadata) {
        aanleverenClient.createRequest(
                        new Versie().oorzaak(Versie.OorzaakEnum.AANLEVERING),
                        SOURCE_LABEL,
                        documentId)
                .addPart("metadata_v1", metadata.getBytes(StandardCharsets.UTF_8))
                .post();
    }

    public String registerProces(final String triggerType, final String trigger) {
        final var proces = registrationClient.createProces(SOURCE_LABEL, triggerType, trigger);
        return proces.getId();
    }
}

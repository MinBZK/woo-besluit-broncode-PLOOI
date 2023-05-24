package nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.InternalServerErrorException;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.NotFoundException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

import static nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.ExceptionConstants.*;

@Service
@RequiredArgsConstructor
public class DcnClient {
    private static final String DCN_DELIVER_ENDPOINT = "/dcn/api/";
    private static final String DCN_ENDPOINT = "/dcn/api/metadata_v1/";
    private final WebClient webClient;

    public void replaceMetadataByUUID(final String documentId, final String metadata) {
        webClient.post()
                .uri(DCN_DELIVER_ENDPOINT + documentId)
                .body(BodyInserters.fromMultipartData(createMultiValueMap(metadata)))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new InternalServerErrorException(MESSAGE_SERVICE_VERVANGEN_NIET_BESCHIKBAAR)))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new InternalServerErrorException(MESSAGE_SERVICE_VERVANGEN_NIET_BESCHIKBAAR)))
                .bodyToMono(HttpStatus.class)
                .onErrorMap(Predicate.not(NotFoundException.class::isInstance), throwable -> new InternalServerErrorException(MESSAGE_SERVICE_VERVANGEN_NIET_BESCHIKBAAR))
                .blockOptional();
    }

    public String searchMetadataByUUID(final String uuid) {
        val dcnServiceResponse = webClient.get()
                .uri(DCN_ENDPOINT + uuid)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new NotFoundException()))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new InternalServerErrorException(MESSAGE_SERVICE_ZOEKEN_NIET_BESCHIKBAAR)))
                .bodyToMono(ByteArrayResource.class)
                .onErrorMap(Predicate.not(NotFoundException.class::isInstance), throwable -> new InternalServerErrorException(MESSAGE_SERVICE_ZOEKEN_NIET_BESCHIKBAAR))
                .map(ByteArrayResource::getByteArray)
                .blockOptional();

        final InputStream metadataStream = new ByteArrayInputStream(dcnServiceResponse.orElseThrow(NotFoundException::new));
        try {
            return new String(metadataStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InternalServerErrorException(MESSAGE_METADATA_NIET_VERWERKBAAR);
        }
    }

    private MultiValueMap<String, HttpEntity<?>> createMultiValueMap(final String metadata) {
        val metadataJson = new MockMultipartFile("metadata", "metadata.json", "application/json", metadata.getBytes());
        val builder = new MultipartBodyBuilder();
        builder.part("metadata", metadataJson.getResource());
        return builder.build();
    }
}

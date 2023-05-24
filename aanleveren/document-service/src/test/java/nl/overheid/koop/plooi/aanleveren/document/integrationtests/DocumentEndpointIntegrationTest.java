package nl.overheid.koop.plooi.aanleveren.document.integrationtests;

import nl.overheid.koop.plooi.aanleveren.document.domain.exceptions.NotFoundException;
import nl.overheid.koop.plooi.aanleveren.document.infrastructure.dcn.DcnClient;
import nl.overheid.koop.plooi.registration.client.ClientException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static nl.overheid.koop.plooi.aanleveren.document.util.TestConstants.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DocumentEndpointIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DcnClient dcnClientMock;

    @Test
    void createDocument() throws IOException {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .contentType(MediaType.APPLICATION_PDF)
                .bodyValue(getValidPDF())
                .header("x-session-id", "1234")
                .exchange()
                .expectHeader().valueEquals(API_VERSION_HEADER, API_VERSION_VALUE)
                .expectHeader().valueEquals(CACHE_CONTROL_HEADER, CACHE_CONTROL_VALUE)
                .expectHeader().valueEquals(CONTENT_LANGUAGE_HEADER, CONTENT_LANGUAGE_VALUE)
                .expectHeader().valueEquals(XFRAME_OPTIONS_HEADER, XFRAME_OPTIONS_VALUE)
                .expectHeader().valueEquals(XCONTENT_TYPE_OPTIONS_HEADER, XCONTENT_TYPE_OPTIONS_VALUE)
                .expectHeader().valueEquals(CONTENT_SECURITY_POLICY_HEADER, CONTENT_SECURITY_POLICY_VALUE)
                .expectHeader().valueEquals(REFERER_POLICY_HEADER, REFERER_POLICY_VALUE)
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("title").isEqualTo("Het document is in behandeling genomen.");
    }

    @Test
    void createDocument_invalidPDF() throws IOException {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .contentType(MediaType.APPLICATION_PDF)
                .bodyValue(getInValidPDF())
                .header("x-session-id", "1234")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("title").isEqualTo("Bestandstype-fout");
    }

    @Test
    void createDocument_Zip() throws IOException {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .contentType(MediaType.APPLICATION_PDF)
                .bodyValue(getZip())
                .header("x-session-id", "1234")
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("title").isEqualTo("Het document is in behandeling genomen.");
    }

    @Test
    void createDocument_emptyZip() throws IOException {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .contentType(MediaType.APPLICATION_PDF)
                .bodyValue(getEmptyZip())
                .header("x-session-id", "1234")
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("title").isEqualTo("Het document is in behandeling genomen.");
    }

    @Test
    void getDocument() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getDocument_invalidUuid() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment("123")
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("title").isEqualTo("Validatiefout");
    }

    @Test
    void getDocument_documentNotFound() {
        when(dcnClientMock.registerProcess(anyString(), anyString())).thenReturn("123");
        when(dcnClientMock.searchDocument(anyString())).thenThrow(new NotFoundException("test"));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("title").isEqualTo("Niet-gevonden-fout");
    }

    @Test
    void replaceDocument_invalidPDF() throws IOException {
        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .contentType(MediaType.APPLICATION_PDF)
                .bodyValue(getInValidPDF())
                .header("x-session-id", "1234")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("title").isEqualTo("Bestandstype-fout");
    }

    @Test
    void replaceDocument_zip() throws IOException {
        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .contentType(MediaType.APPLICATION_PDF)
                .bodyValue(getZip())
                .header("x-session-id", "1234")
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void replaceDocument_emptyZip() throws IOException {
        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .contentType(MediaType.APPLICATION_PDF)
                .bodyValue(getEmptyZip())
                .header("x-session-id", "1234")
                .exchange()
                .expectStatus().isAccepted();
    }

    @Test
    void replaceDocument_NotFound() throws IOException {
        doThrow(NotFoundException.class).when(dcnClientMock).postDocument(anyString(), any());

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .contentType(MediaType.APPLICATION_PDF)
                .bodyValue(getEmptyZip())
                .header("x-session-id", "1234")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void replaceDocument_dcnServiceNotAvailable() throws IOException {
        doThrow(ClientException.class).when(dcnClientMock).postDocument(anyString(), any());

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID)
                        .build())
                .contentType(MediaType.APPLICATION_PDF)
                .bodyValue(getEmptyZip())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void deleteDocument() {
        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID_SECOND)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteDocument_documentNotFound() {
        when(dcnClientMock.registerProcess(anyString(), anyString())).thenReturn("123");
        doThrow(new NotFoundException("Test")).when(dcnClientMock).deleteDocument(anyString());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/overheid/openbaarmaken/api/v0/documenten/")
                        .pathSegment(UUID_SECOND)
                        .build())
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    private byte[] getValidPDF() throws IOException {
        File resource = new ClassPathResource("pdf/Nummer20219495.pdf").getFile();
        return Files.readAllBytes(resource.toPath());
    }

    private byte[] getZip() throws IOException {
        File resource = new ClassPathResource("zip/zippie-gevuld.zip").getFile();
        return Files.readAllBytes(resource.toPath());
    }

    private byte[] getEmptyZip() throws IOException {
        File resource = new ClassPathResource("zip/zippie-leeg.zip").getFile();
        return Files.readAllBytes(resource.toPath());
    }

    private byte[] getInValidPDF() throws IOException {
        File resource = new ClassPathResource("pdf/IAmWrongPDF.pdf").getFile();
        return Files.readAllBytes(resource.toPath());
    }
}

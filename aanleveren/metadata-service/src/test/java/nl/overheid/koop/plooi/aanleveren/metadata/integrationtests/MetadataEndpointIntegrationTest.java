package nl.overheid.koop.plooi.aanleveren.metadata.integrationtests;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.ExceptionConstants;
import nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn.DcnClientService;
import nl.overheid.koop.plooi.plooisecurity.domain.policy.AttributePolicyEnforcement;
import nl.overheid.koop.plooi.plooisecurity.domain.policy.PolicyContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static nl.overheid.koop.plooi.aanleveren.metadata.util.TestDataFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MetadataEndpointIntegrationTest {
    @MockBean
    private AttributePolicyEnforcement policyEnforcement;
    @MockBean
    private DcnClientService dcnClientMock;
    
    static MockWebServer mockServer;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void getMetadataInstance_ok() {
        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("response")
                .setResponseCode(200));

        webTestClient.get()
                .uri(uri -> uri.path(METADATA_ENDPOINT)
                        .pathSegment(DOCUMENT_ID)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getMetadataInstance_notFound() {
        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("response")
                .setResponseCode(404));

        webTestClient.get()
                .uri(uri -> uri.path(METADATA_ENDPOINT)
                        .pathSegment(DOCUMENT_ID)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.title").isEqualTo(ExceptionConstants.TITLE_NIET_GEVONDEN_FOUT);
    }

    @Test
    void createMetadata() {
        when(dcnClientMock.registerProces(anyString(), anyString()))
                .thenReturn("process_id");
        when(policyEnforcement.enforcePolicy(any(PolicyContext.class)))
                .thenReturn(true);

        webTestClient.post()
                .uri(METADATA_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA)))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .valueMatches("X-Session-ID", "process_id")
                .expectBody()
                .jsonPath("$.document.publisher").exists()
                .jsonPath("$.document.extraMetadata").exists()
                .jsonPath("$.document.pid").exists();
    }

    @Test
    void createMetadata_unauthorized() {
        when(policyEnforcement.enforcePolicy(any(PolicyContext.class)))
                .thenReturn(false);

        webTestClient.post()
                .uri(METADATA_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA)))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createMetadata_badRequest() {
        when(policyEnforcement.enforcePolicy(any(PolicyContext.class)))
                .thenReturn(true);

        webTestClient.post()
                .uri(METADATA_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA_WITHOUT_IDENTIFIERS)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("ValidatieMetadatafout")
                .jsonPath("$.messages[0].content").toString().contains("$.document.identifiers: is missing but it is required");
    }

    @Test
    void replaceMetadata() {
        when(dcnClientMock.registerProces(anyString(), anyString()))
                .thenReturn("process_id");
        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(getBody(METADATA_PID))
                .setResponseCode(200));

        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(200));

        webTestClient.put()
                .uri(uri -> uri.path(METADATA_ENDPOINT)
                        .pathSegment(DOCUMENT_ID)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA_PID)))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .valueMatches("X-Session-ID", "process_id");
    }

    @Test
    void replaceMetadata_badRequest() {
        webTestClient.put()
                .uri(uri -> uri.path(METADATA_ENDPOINT)
                        .pathSegment(DOCUMENT_ID)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA_PID_WITHOUT_IDENTIFIERS)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("ValidatieMetadatafout")
                .jsonPath("$.messages[0].content").toString().contains("$.document.identifiers: is missing but it is required");
    }

    @Test
    void replaceMetadata_conflict() {
        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(getBody(METADATA_PID))
                .setResponseCode(200));

        webTestClient.put()
                .uri(uri -> uri.path(METADATA_ENDPOINT)
                        .pathSegment(DOCUMENT_ID)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA_OTHER_PID)))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.title").isEqualTo(ExceptionConstants.TITLE_CONFLICT_FOUT);
    }

    @Test
    void replaceMetadata_forbidden() {
        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(getMetadataModel(METADATA_PID))
                .setResponseCode(200));

        webTestClient.put()
                .uri(uri -> uri.path(METADATA_ENDPOINT)
                        .pathSegment(DOCUMENT_ID)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA_PID_OTHER_PUBLISHER)))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.title").isEqualTo(ExceptionConstants.TITLE_AUTORISATIE_FOUT);

    }

    @Test
    void replaceMetadata_notProcessed() {
        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(getBody(METADATA_PID))
                .setResponseCode(200));

        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(400));

        webTestClient.put()
                .uri(uri -> uri.path(METADATA_ENDPOINT)
                        .pathSegment(DOCUMENT_ID)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA_PID)))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void replaceMetadata_internalError() {
        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(getBody(METADATA_PID))
                .setResponseCode(200));

        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(500));

        webTestClient.put()
                .uri(uri -> uri.path(METADATA_ENDPOINT)
                        .pathSegment(DOCUMENT_ID)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA_PID)))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void replaceMetadata_identifierError() {
        mockServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(getBody(METADATA_WITHOUT_PID))
                .setResponseCode(200));

        webTestClient.put()
                .uri(uri -> uri.path(METADATA_ENDPOINT)
                        .pathSegment(DOCUMENT_ID)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(getBody(METADATA_WITHOUT_PID)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo(ExceptionConstants.TITLE_IDENTIFIER_NIET_GELDIG);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        WebClient webClient() {
            HttpUrl url = mockServer.url("http://localhost:" + mockServer.getPort());
            return WebClient.create(url.toString());
        }
    }
}

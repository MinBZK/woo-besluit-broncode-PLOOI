package nl.overheid.koop.plooi.aanleveren.zoeken.integration.endpoints;

import nl.overheid.koop.plooi.search.client.SearchClient;
import nl.overheid.koop.plooi.search.model.SearchRequest;
import nl.overheid.koop.plooi.search.model.SearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ZoekenEndpointIntegrationTest {
    @MockBean
    private SearchClient searchClient;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void searchDocuments() {
        when(searchClient.search(any(SearchRequest.class), eq("v1")))
                .thenReturn(new SearchResponse());

        this.webTestClient.get().uri(uriBuilder ->
                        uriBuilder
                                .path("/overheid/openbaarmaken/api/v0/_zoek")
                                .queryParam("publisher", "https://identifier.overheid.nl/tooi/id/gemeente/gm0007")
                                .queryParam("start", 1)
                                .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void searchDocuments_badRequest() {
        this.webTestClient.get().uri(uriBuilder ->
                        uriBuilder
                                .path("/overheid/openbaarmaken/api/v0/_zoek")
                                .queryParam("publisher", "https://identifier.overheid.nl/tooi/id/gemeente/gm0007")
                                .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void searchDocuments_invalidArgument() {
        this.webTestClient.get().uri(uriBuilder ->
                        uriBuilder
                                .path("/overheid/openbaarmaken/api/v0/_zoek")
                                .queryParam("publisher", "https://identifier.overheid.nl/tooi/id/gemeente/gm0007")
                                .queryParam("start", -1)
                                .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}

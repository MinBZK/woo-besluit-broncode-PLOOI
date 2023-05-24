package nl.overheid.koop.plooi.aanleveren.waardelijst.integration;

import nl.overheid.koop.plooi.aanleveren.waardelijst.domain.model.WaardelijstSoort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WaardelijstserviceIntegrationTest {
    private final static String PATH = "/waardelijsten";
    private final static String QUERY_PARAM_SOORT = "soort";
    @Autowired
    private WebTestClient client;

    @ParameterizedTest
    @EnumSource(WaardelijstSoort.class)
    void getWaardelijst(final WaardelijstSoort soort) {
        client.get()
                .uri(uri -> uri.path(PATH)
                        .queryParam(QUERY_PARAM_SOORT, soort.name())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getWaardelijst_soortDoesNotExist() {
        client.get()
                .uri(uri -> uri.path(PATH)
                        .queryParam(QUERY_PARAM_SOORT, "CAR")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }
}

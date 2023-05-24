package nl.overheid.koop.plooi.plooiiamservice.web;

import com.nimbusds.jose.JOSEException;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.configuration.ApplicationProperties;
import nl.overheid.koop.plooi.plooiiamservice.domain.TokenService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenrequestEndpointTest {
    private static TokenRequestEndpoint sut;

    @BeforeAll
    static void beforeAll() throws JOSEException {
        val properties = new ApplicationProperties(null, null, null,
                List.of("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                        "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                        "http://localhost:3000",
                        "http://localhost:3000/"));
        val tokenService = mock(TokenService.class);
        when(tokenService.generateToken(any())).thenReturn("123");
        sut = new TokenRequestEndpoint(properties, tokenService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://localhost:3000",
            "http://localhost:3000/",
            "http%3A%2F%2Flocalhost%3A3000%2F",
            "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
            "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"})
    void requestSaml(String redirectUri) throws JOSEException {
        val response = sut.requestSaml(null, redirectUri);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(redirectUri + "?access_token=123", response.getHeaders().get("Location").get(0));
    }

    @Test
    void requestSaml_badRequest() throws JOSEException {
        val response = sut.requestSaml(null, "http://localhost:8080");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

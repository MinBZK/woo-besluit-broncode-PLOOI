package nl.overheid.koop.plooi.plooisecurity.domain.authorization;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenAuthorizationServiceTest {
    private TokenAuthorizationService sut;
    @Mock
    private ExchangeFunction exchangeFunction;

    @BeforeEach
    void beforeEach() {
        val webclient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();
        this.sut = new TokenAuthorizationService(webclient);
    }

    @ParameterizedTest
    @ValueSource(strings = {"token", " ", ""})
    @NullSource
    void isAuthorizationMissing(String token) {
        assertTrue(sut.isAuthorizationMissing(token));
    }

    @Test
    void isAuthorizationMissing_notMissing() {
        val token = "Bearer token";

        assertFalse(sut.isAuthorizationMissing(token));
    }

    @Test
    void authorize() {
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(
                        ClientResponse.create(HttpStatus.OK).build()));

        val isAuthorized = sut.authorize("Bearer token");

        assertTrue(isAuthorized);
    }

    @Test
    void authorize_unauthorized() {
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(
                        ClientResponse.create(HttpStatus.UNAUTHORIZED).build()));

        val isAuthorized = sut.authorize("Bearer token");

        assertFalse(isAuthorized);
    }

    @Test
    void authorize_internalServerError() {
        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(
                        ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build()));

        val isAuthorized = sut.authorize("Bearer token");

        assertFalse(isAuthorized);
    }
}

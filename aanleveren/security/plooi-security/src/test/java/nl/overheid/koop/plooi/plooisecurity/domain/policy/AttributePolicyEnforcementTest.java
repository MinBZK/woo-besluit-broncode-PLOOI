package nl.overheid.koop.plooi.plooisecurity.domain.policy;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttributePolicyEnforcementTest {
    private WebClient webclient;
    @Mock private ExchangeFunction exchangeFunction;

    @BeforeEach
    void beforeEach() {
        this.webclient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();
    }

    @Test
    void enforcePolicy() {
        val request = new MockHttpServletRequest();
        request.addHeader("authorization", "Bearer token");
        val sut = new AttributePolicyEnforcement(webclient, request);
        val policyContext =
                new PolicyContext(
                        "crud_document",
                        new Attributes("mnre1034", List.of("pdf")));

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(
                        ClientResponse.create(HttpStatus.OK).build()));

        assertTrue(sut.enforcePolicy(policyContext));
    }

    @Test
    void enforcePolicy_badRequest() {
        val request = new MockHttpServletRequest();
        request.addHeader("authorization", "Bearer token");
        val sut = new AttributePolicyEnforcement(webclient, request);
        val policyContext =
                new PolicyContext(
                        "crud_document",
                        new Attributes("mnre1034", List.of("pdf")));

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(
                        ClientResponse.create(HttpStatus.BAD_REQUEST).build()));

        assertFalse(sut.enforcePolicy(policyContext));
    }

    @Test
    void enforcePolicy_unauthorized() {
        val request = new MockHttpServletRequest();
        request.addHeader("authorization", "Bearer token");
        val sut = new AttributePolicyEnforcement(webclient, request);
        val policyContext =
                new PolicyContext(
                        "crud_document",
                        new Attributes("mnre1034", List.of("pdf")));

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(
                        ClientResponse.create(HttpStatus.UNAUTHORIZED).build()));

        assertFalse(sut.enforcePolicy(policyContext));
    }

    @Test
    void enforcePolicy_noAuthHeader() {
        val sut = new AttributePolicyEnforcement(webclient, new MockHttpServletRequest());
        val policyContext =
                new PolicyContext(
                        "crud_document",
                        new Attributes("mnre1034", List.of("pdf")));

        assertFalse(sut.enforcePolicy(policyContext));
    }

}

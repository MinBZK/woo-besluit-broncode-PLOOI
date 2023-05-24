package nl.overheid.koop.plooi.plooisecurity.web.filter;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.reactive.function.client.ClientRequest;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestFilterTest {
    private static final String AUTH_HEADER_NAME = "authorization";
    private static final String AUTH_HEADER_VALUE = "123";
    private final ClientRequest clientRequest = ClientRequest
            .create(HttpMethod.GET, URI.create("http://localhost:8080"))
            .build();

    @Test
    void propagateAuthorization_Jwt() {
        val request = new MockHttpServletRequest();
        request.addHeader(AUTH_HEADER_NAME, AUTH_HEADER_VALUE);
        val sut = new RequestFilter(request);

        val response = sut.propagateAuthorization(clientRequest).block();

        val header = response.headers().get(AUTH_HEADER_NAME).get(0);
        assertEquals(AUTH_HEADER_VALUE, header);
    }

    @Test
    void propagateAuthorization_withoutJwt() {
        val request = new MockHttpServletRequest();
        val sut = new RequestFilter(request);

        val response = sut.propagateAuthorization(clientRequest).block();

        val header = response.headers().get(AUTH_HEADER_NAME);
        assertNull(header);
    }
}

package nl.overheid.koop.plooi.aanleveren.authentication;


import nl.overheid.koop.plooi.aanleveren.authentication.service.AuthenticationService;
import nl.overheid.koop.plooi.aanleveren.authentication.response.ExpiredToken;
import nl.overheid.koop.plooi.aanleveren.authentication.response.InvalidToken;
import nl.overheid.koop.plooi.aanleveren.authentication.response.MissingToken;
import nl.overheid.koop.plooi.aanleveren.authentication.response.WriterFactory;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AanleverTokenFilterTest {
    private static final String AANLEVERTOKEN_HEADER = "Aanlever-Token";
    private static final String AANLEVERTOKEN_ID = "123";

    @Mock private AuthenticationService authenticationService;
    @Mock private WriterFactory writerFactory;
    @InjectMocks private AanleverTokenFilter sut;

    @Test
    void doFilterInternal() throws ServletException, IOException {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val filterChain = new MockFilterChain();
        request.addHeader(AANLEVERTOKEN_HEADER, AANLEVERTOKEN_ID);
        when(authenticationService.authenticate(AANLEVERTOKEN_ID))
                .thenReturn(HttpStatus.OK);

        sut.doFilter(request, response, filterChain);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void doFilterInternal_MissingToken() throws ServletException, IOException {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val filterChain = new MockFilterChain();
        when(writerFactory.getResponseWriter(HttpStatus.FORBIDDEN))
                .thenReturn(new MissingToken());

        sut.doFilter(request, response, filterChain);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("\"title\" : \"Autorisatiefout\""));
        assertTrue(response.getContentAsString()
                .contains("\"detail\" : \"U bent niet geautoriseerd voor deze actie. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).\""));

    }

    @Test
    void doFilterInternal_invalidSandboxToken() throws ServletException, IOException {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val filterChain = new MockFilterChain();
        request.addHeader(AANLEVERTOKEN_HEADER, AANLEVERTOKEN_ID);
        when(authenticationService.authenticate(AANLEVERTOKEN_ID))
                .thenReturn(HttpStatus.BAD_REQUEST);
        when(writerFactory.getResponseWriter(HttpStatus.BAD_REQUEST))
                .thenReturn(new InvalidToken());

        sut.doFilter(request, response, filterChain);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("\"title\" : \"identifier-fout\""));
        assertTrue(response.getContentAsString().contains("\"detail\" : \"De meegegeven identifier is niet geldig. Raadpleeg voor meer informatie de documentatie.\""));
    }

    @Test
    void doFilter_expiredSandboxToken() throws ServletException, IOException {
        val request = new MockHttpServletRequest();
        val response = new MockHttpServletResponse();
        val filterChain = new MockFilterChain();
        request.addHeader(AANLEVERTOKEN_HEADER, AANLEVERTOKEN_ID);
        when(authenticationService.authenticate(AANLEVERTOKEN_ID))
                .thenReturn(HttpStatus.UNAUTHORIZED);
        when(writerFactory.getResponseWriter(HttpStatus.UNAUTHORIZED))
                .thenReturn(new ExpiredToken());

        sut.doFilter(request, response, filterChain);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertTrue(response.getContentAsString().contains("\"title\" : \"Identificatiefout\""));
        assertTrue(response.getContentAsString().contains("\"detail\" : \"Deze handeling is alleen mogelijk voor geautoriseerden. Raadpleeg voor meer informatie de documentatie.\""));
    }

    @ParameterizedTest
    @CsvSource({
            "OPTIONS, /overheid/openbaarmaken/api/v0/_zoek, true",
            "GET, /actuator/health, true",
            "GET, /overheid/openbaarmaken/api/v0/_zoek, false"})
    void shouldNotFilter(String method, String requestURI, boolean shouldFilter) {
        val request = new MockHttpServletRequest();
        request.setMethod(method);
        request.setRequestURI(requestURI);
        assertEquals(shouldFilter, sut.shouldNotFilter(request));
    }
}

package nl.overheid.koop.plooi.plooiiamservice.web;

import com.nimbusds.jose.JOSEException;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenIntrospectionEndpointTest {
    private static final String AUTH_HEADER = "x-access-token";
    private static final String ACCESS_TOKEN = "123";
    private MockHttpServletRequest request;
    @Mock private TokenService tokenService;
    @InjectMocks private TokenIntrospectionEndpoint sut;

    @BeforeEach
    void beforeEach() {
        request = new MockHttpServletRequest();
    }

    @Test
    void checkToken() throws ParseException, JOSEException {
        when(tokenService.validateToken(ACCESS_TOKEN)).thenReturn(true);
        request.addHeader(AUTH_HEADER, ACCESS_TOKEN);

        val response = sut.checkToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void checkToken_withoutBearerPrefix() {
        request.addHeader(AUTH_HEADER, ACCESS_TOKEN);

        val response = sut.checkToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void checkToken_authHeaderIsNull() {
        val response = sut.checkToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void checkToken_invalidToken() throws ParseException, JOSEException {
        when(tokenService.validateToken(ACCESS_TOKEN)).thenReturn(false);
        request.addHeader(AUTH_HEADER, ACCESS_TOKEN);

        val response = sut.checkToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void checkToken_parseException() throws ParseException, JOSEException {
        when(tokenService.validateToken(ACCESS_TOKEN)).thenThrow(new ParseException("Invalid", 1));
        request.addHeader(AUTH_HEADER, ACCESS_TOKEN);

        val response = sut.checkToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void checkToken_JOSEException() throws ParseException, JOSEException {
        when(tokenService.validateToken(ACCESS_TOKEN)).thenThrow(new JOSEException("Invalid"));
        request.addHeader(AUTH_HEADER, ACCESS_TOKEN);

        val response = sut.checkToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}

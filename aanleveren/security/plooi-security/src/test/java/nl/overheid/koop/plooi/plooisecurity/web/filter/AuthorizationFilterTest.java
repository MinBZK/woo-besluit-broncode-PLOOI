package nl.overheid.koop.plooi.plooisecurity.web.filter;

import lombok.val;
import nl.overheid.koop.plooi.plooisecurity.domain.authorization.SecurityContextInitializer;
import nl.overheid.koop.plooi.plooisecurity.domain.authorization.TokenAuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static nl.overheid.koop.plooi.plooisecurity.web.filter.AuthorizationFilter.AUTH_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationFilterTest {
    private static final String TOKEN = "token";
    @Mock private TokenAuthorizationService tokenAuthorizationService;
    @Mock private SecurityContextInitializer securityContextInitializer;
    @InjectMocks private AuthorizationFilter sut;

    private final MockFilterChain filterChain = new MockFilterChain();
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void beforeEach() {
         this.request = new MockHttpServletRequest();
         this.response  = new MockHttpServletResponse();
         request.addHeader(AUTH_HEADER_NAME, TOKEN);

    }

    @Test
    void doFilterInternal() throws ServletException, IOException {
        when(tokenAuthorizationService.isAuthorizationMissing(TOKEN))
                .thenReturn(false);
        when(tokenAuthorizationService.authorize(TOKEN))
                .thenReturn(true);

        sut.doFilterInternal(this.request, this.response, this.filterChain);

        assertEquals(200, this.response.getStatus());
    }

    @Test
    void doFilterInternal_unauthorized() throws ServletException, IOException {
        when(tokenAuthorizationService.isAuthorizationMissing(TOKEN))
                .thenReturn(false);
        when(tokenAuthorizationService.authorize(TOKEN))
                .thenReturn(false);

        sut.doFilterInternal(this.request, this.response, this.filterChain);

        assertEquals(401, this.response.getStatus());
        assertEquals("""
                {
                  "type" : "https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen/index.html#identificatie-fout",
                  "title" : "Identificatiefout",
                  "status" : 401,
                  "detail" : "Deze handeling is alleen mogelijk voor geautoriseerden. Raadpleeg voor meer informatie de documentatie.",
                  "instance" : "https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen",
                  "messages" : [ "De identificerende gegevens in dit verzoek ontbreken of zijn onjuist. Geef (andere) identificerende gegevens mee." ]
                }""", this.response.getContentAsString());
    }

    @Test
    void doFilterInternal_forbidden() throws ServletException, IOException {
        when(tokenAuthorizationService.isAuthorizationMissing(TOKEN))
                .thenReturn(true);

        sut.doFilterInternal(this.request, this.response, this.filterChain);

        assertEquals(403, this.response.getStatus());
        assertEquals("""
                {
                  "type" : "https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen/index.html#autorisatie-fout",
                  "title" : "Autorisatiefout",
                  "status" : 403,
                  "detail" : "U bent niet geautoriseerd voor deze actie. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).",
                  "instance" : "https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen",
                  "messages" : [ "Aan de identificerende gegevens in dit verzoek zijn geen privileges tot deze handeling verbonden. Geef andere identificerende gegevens mee." ]
                }""", this.response.getContentAsString());
    }

    @Test
    void shouldNotFilter_methodOptions() {
        this.request.setMethod("OPTIONS");

        val shouldNotFilter = sut.shouldNotFilter(this.request);

        assertTrue(shouldNotFilter);
    }

    @Test
    void shouldNotFilter_actuatorEndpoint() {
        this.request.setRequestURI("/actuator/health");

        val shouldNotFilter = sut.shouldNotFilter(this.request);

        assertTrue(shouldNotFilter);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST", "PUT", "DELETE"})
    void shouldNotFilter_methodGET(String method) {
        this.request.setMethod(method);

        val shouldNotFilter = sut.shouldNotFilter(this.request);

        assertFalse(shouldNotFilter);
    }

    @Test
    void shouldNotFilter_endpoint() {
        this.request.setRequestURI("/resources");

        val shouldNotFilter = sut.shouldNotFilter(this.request);

        assertFalse(shouldNotFilter);
    }
}

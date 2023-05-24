package nl.overheid.koop.plooi.plooiiamservice.web;

import com.nimbusds.jose.JOSEException;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.TokenService;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.JwtClaim;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.Actions;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.Attributes;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.PolicyCheckService;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.PolicyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import static nl.overheid.koop.plooi.plooiiamservice.domain.policies.rules.KSTPolicy.KAMERSTUK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyEndpointTest {
    private static final String AUTH_HEADER = "x-access-token";
    private static final String ACCESS_TOKEN = "123";
    private final List<JwtClaim> jwtClaims = Collections.emptyList();
    private final PolicyContext policyContext = new PolicyContext(Actions.CRUD_DOCUMENT.name(),
        new Attributes("mnre1045", List.of(KAMERSTUK,
                "https://identifier.overheid.nl/tooi/def/thes/kern/c_ef935990",
                "https://identifier.overheid.nl/tooi/def/thes/kern/c_dfa0ff1f")));

    private MockHttpServletRequest request;
    @Mock
    private PolicyCheckService policyCheckService;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private PolicyEndpoint sut;

    @BeforeEach
    void beforeEach() {
        request = new MockHttpServletRequest();
        request.addHeader(AUTH_HEADER, ACCESS_TOKEN);
    }

    @Test
    void checkPolicy() throws ParseException, JOSEException {
        when(tokenService.getDecodedClaims(ACCESS_TOKEN)).thenReturn(jwtClaims);
        when(policyCheckService.enforcePolicy(jwtClaims, policyContext)).thenReturn(true);

        val response = sut.checkPolicy(request, policyContext);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void checkPolicy_policyUnauthorized() throws ParseException, JOSEException {
        when(tokenService.getDecodedClaims(ACCESS_TOKEN)).thenReturn(jwtClaims);
        when(policyCheckService.enforcePolicy(jwtClaims, policyContext)).thenReturn(false);

        val response = sut.checkPolicy(request, policyContext);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void checkPolicy_authHeaderIsNull() throws ParseException, JOSEException {
        val response = sut.checkPolicy(new MockHttpServletRequest(), policyContext);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}

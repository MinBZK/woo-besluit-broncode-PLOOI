package nl.overheid.koop.plooi.plooiiamservice.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.configuration.ApplicationProperties;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.ClaimFactory;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.Organisation;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;

import java.text.ParseException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Organisation.ORGANISATION_CLAIM_KEY;
import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Role.ROLE_CLAIM_KEY;
import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Role.ROLE_KAMERSTUK;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    private final DefaultSaml2AuthenticatedPrincipal principal =
            new DefaultSaml2AuthenticatedPrincipal(
                    "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                    Map.of(ROLE_CLAIM_KEY, List.of("Publisher KST"),
                            ORGANISATION_CLAIM_KEY, List.of("ministerie van Binnenlandse Zaken en Koninkrijksrelaties")));
    private final ApplicationProperties properties =
            new ApplicationProperties(
                    "plooi-test.jks",
                    "password",
                    null,
                    null);
    private final TokenService sut = new TokenService(new SignedJwt(new RsaKey(properties), new ClaimFactory(List.of(new Role(), new Organisation(new ObjectMapper())))), new JwtVerifier(new RsaKey(properties)));

    @Test
    void generateToken() throws JOSEException {
        val token = sut.generateToken(principal);

        val payload = getPayload(token);
        assertTrue(payload.contains("roles"));
        assertTrue(payload.contains("org"));
        assertTrue(payload.contains("exp"));
    }

    @Test
    void validateToken() throws JOSEException, ParseException {
        val token = sut.generateToken(principal);

        val isValid = sut.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_invalidToken() throws ParseException, JOSEException {
        val isValid = sut.validateToken("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");

        assertFalse(isValid);
    }

    @Test
    void validateToken_expiredToken() throws ParseException, JOSEException {
        val isValid = sut.validateToken("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");

        assertFalse(isValid);
    }

    @Test
    void decodeClaims() throws ParseException, JOSEException {
        val token = sut.generateToken(principal);
        val claims = sut.getDecodedClaims(token);

        assertEquals(2, claims.size());
        assertEquals("org", claims.get(0).key());
        assertEquals(List.of("mnre1034"), claims.get(0).values());
        assertEquals("roles", claims.get(1).key());
        assertEquals(List.of(ROLE_KAMERSTUK), claims.get(1).values());
    }

    @Test
    void decodeClaims_invalidToken() throws ParseException, JOSEException {
        val invalidToken = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";

        val claims = sut.getDecodedClaims(invalidToken);

        assertTrue(claims.isEmpty());
    }

    @Test
    void decodeClaims_emptyToken() throws ParseException, JOSEException {
        val principal =
                new DefaultSaml2AuthenticatedPrincipal(
                        "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                        Collections.emptyMap());
        val token = sut.generateToken(principal);

        val claims = sut.getDecodedClaims(token);

        assertEquals(2, claims.size());
        assertEquals("org", claims.get(0).key());
        assertTrue(claims.get(0).values().isEmpty());
        assertEquals("roles", claims.get(1).key());
        assertTrue(claims.get(1).values().isEmpty());
    }

    private String getPayload(final String token) {
        String[] parts = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        return new String(decoder.decode(parts[1]));
    }
}
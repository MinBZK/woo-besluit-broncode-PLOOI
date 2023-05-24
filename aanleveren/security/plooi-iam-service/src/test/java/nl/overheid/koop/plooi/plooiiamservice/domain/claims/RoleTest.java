package nl.overheid.koop.plooi.plooiiamservice.domain.claims;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;

import java.util.List;
import java.util.Map;

import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Role.ROLE_CLAIM_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleTest {
    private final Role sut = new Role();

    @Test
    void getAttribute() {
        val principal = new DefaultSaml2AuthenticatedPrincipal(
                "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                Map.of(ROLE_CLAIM_KEY, List.of("Publisher KST")));

        val attribute = sut.getAttribute(principal);

        assertEquals(List.of("Publisher KST"), attribute);
    }

    @Test
    void getAttribute_WithoutAttribute() {
        val principal = new DefaultSaml2AuthenticatedPrincipal(
                "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                Map.of("Other", List.of("Other value")));

        val attribute = sut.getAttribute(principal);

        assertTrue(attribute.isEmpty());
    }
}

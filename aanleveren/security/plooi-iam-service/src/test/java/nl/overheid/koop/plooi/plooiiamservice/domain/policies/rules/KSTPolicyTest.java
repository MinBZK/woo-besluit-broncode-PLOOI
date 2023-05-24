package nl.overheid.koop.plooi.plooiiamservice.domain.policies.rules;

import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.JwtClaim;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.Actions;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.Attributes;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.PolicyContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Role.ROLE_BASIS;
import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Role.ROLE_KAMERSTUK;
import static nl.overheid.koop.plooi.plooiiamservice.domain.policies.rules.KSTPolicy.KAMERSTUK;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KSTPolicyTest {
    private final KSTPolicy sut = new KSTPolicy();

    @Test
    void evaluate_allow() {
        val jwtClaims = List.of(
                new JwtClaim("org", List.of("mnre1045")),
                new JwtClaim("roles", List.of(ROLE_KAMERSTUK)));


        val policyContext = new PolicyContext(
                Actions.CRUD_DOCUMENT.name(),
                new Attributes(
                        "mnre1045",
                        List.of(KAMERSTUK,
                                "https://identifier.overheid.nl/tooi/def/thes/kern/c_ef935990",
                                "https://identifier.overheid.nl/tooi/def/thes/kern/c_dfa0ff1f")));

        assertTrue(sut.evaluate(jwtClaims, policyContext));
    }

    @Test
    void evaluate_deny() {
        val jwtClaims = List.of(
                new JwtClaim("org", List.of("mnre1010")),
                new JwtClaim("roles", List.of(ROLE_BASIS)));

        val policyContext = new PolicyContext(Actions.CRUD_DOCUMENT.name(),
                new Attributes(
                        "mnre1045",
                        List.of(KAMERSTUK,
                                "https://identifier.overheid.nl/tooi/def/thes/kern/c_ef935990",
                                "https://identifier.overheid.nl/tooi/def/thes/kern/c_dfa0ff1f")));

        assertFalse(sut.evaluate(jwtClaims, policyContext));
    }

    @Test
    void evaluate_notKamerstuk() {
        val jwtClaims = List.of(
                new JwtClaim("org", List.of("mnre1045")),
                new JwtClaim("roles", List.of(ROLE_KAMERSTUK)));

        val policyContext = new PolicyContext(Actions.CRUD_DOCUMENT.name(),
                new Attributes(
                        "mnre1045",
                        List.of("https://identifier.overheid.nl/tooi/def/thes/kern/c_ef935990",
                                "https://identifier.overheid.nl/tooi/def/thes/kern/c_dfa0ff1f")));

        assertTrue(sut.evaluate(jwtClaims, policyContext));
    }

    @Test
    void match_true() {
        assertTrue(sut.match(Actions.CRUD_DOCUMENT.name()));
    }

    @Test
    void match_false() {
        assertFalse(sut.match("Do something else"));
    }
}

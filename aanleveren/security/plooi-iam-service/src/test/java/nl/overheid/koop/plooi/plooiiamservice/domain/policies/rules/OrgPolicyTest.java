package nl.overheid.koop.plooi.plooiiamservice.domain.policies.rules;

import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.JwtClaim;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.Actions;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.Attributes;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.PolicyContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.overheid.koop.plooi.plooiiamservice.domain.policies.rules.KSTPolicy.KAMERSTUK;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrgPolicyTest {
    private final OrgPolicy sut = new OrgPolicy();

    @Test
    void evaluate_allow() {
        val jwtClaims = List.of(new JwtClaim("org", List.of("mnre1045")));
        val policyContext = new PolicyContext(Actions.CRUD_DOCUMENT.name(),
                new Attributes(
                        "mnre1045",
                        List.of(KAMERSTUK)));
        assertTrue(sut.evaluate(jwtClaims, policyContext));
    }

    @Test
    void evaluate_deny() {
        val jwtClaims = List.of(new JwtClaim("org", List.of("mnre1010")));
        val policyContext = new PolicyContext(Actions.CRUD_DOCUMENT.name(),
                new Attributes(
                        "mnre1045",
                        List.of(KAMERSTUK)));

        assertFalse(sut.evaluate(jwtClaims, policyContext));
    }

    @Test
    void match_true() {
        assertTrue(sut.match(Actions.CRUD_DOCUMENT.name()));
    }

    @Test
    void match_false() {
        assertFalse(sut.match("FakeAction"));
    }
}

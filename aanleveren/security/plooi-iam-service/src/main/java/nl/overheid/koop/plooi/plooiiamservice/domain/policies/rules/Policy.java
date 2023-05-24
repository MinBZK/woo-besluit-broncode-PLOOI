package nl.overheid.koop.plooi.plooiiamservice.domain.policies.rules;

import nl.overheid.koop.plooi.plooiiamservice.domain.claims.JwtClaim;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.PolicyContext;

import java.util.List;

public interface Policy {
    boolean evaluate(List<JwtClaim> claims, PolicyContext policyContext);
    boolean match(String action);
}

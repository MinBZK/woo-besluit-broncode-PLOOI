package nl.overheid.koop.plooi.plooiiamservice.domain.policies.rules;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.JwtClaim;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.Actions;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.PolicyContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrgPolicy implements Policy {

    @Override
    public boolean evaluate(final List<JwtClaim> jwtClaims, PolicyContext policyContext) {
        val orgId = policyContext.attributes().organisatie_id();

        return jwtClaims
                .stream()
                .filter(jwtClaim -> jwtClaim.key().equals("org"))
                .anyMatch(jwtClaim -> jwtClaim.values().contains(orgId));
    }

    @Override
    public boolean match(final String action) {
        return action.equalsIgnoreCase(Actions.CRUD_DOCUMENT.name());
    }
}

package nl.overheid.koop.plooi.plooiiamservice.domain.policies;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.JwtClaim;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.PolicyContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyCheckService {
    private final PolicyFactory policyFactory;

    public boolean enforcePolicy(final List<JwtClaim> claims, final PolicyContext policyContext) {
        val policies = policyFactory.get(policyContext.action());
        return policies.stream().allMatch(policy -> policy.evaluate(claims, policyContext));
    }
}

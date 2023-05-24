package nl.overheid.koop.plooi.plooiiamservice.domain.policies.rules;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.JwtClaim;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.Actions;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.PolicyContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Role.ROLE_KAMERSTUK;

@Component
@RequiredArgsConstructor
public class KSTPolicy implements Policy {
    public static final String KAMERSTUK = "https://identifier.overheid.nl/tooi/def/thes/kern/c_056a75e1";

    @Override
    public boolean evaluate(final List<JwtClaim> jwtClaims, final PolicyContext policyContext) {
        val documentsoorten = policyContext.attributes().documentsoorten();

        val possibleKamerstuk = documentsoorten.stream()
                .filter(soort -> soort.equalsIgnoreCase(KAMERSTUK))
                .findFirst();

        if (possibleKamerstuk.isPresent()) {
            return jwtClaims.stream()
                    .filter(claims -> claims.key().equals("roles"))
                    .anyMatch(claims -> claims.values().contains(ROLE_KAMERSTUK));
        }
        return true;
    }

    @Override
    public boolean match(final String action) {
        return action.equalsIgnoreCase(Actions.CRUD_DOCUMENT.name());
    }
}

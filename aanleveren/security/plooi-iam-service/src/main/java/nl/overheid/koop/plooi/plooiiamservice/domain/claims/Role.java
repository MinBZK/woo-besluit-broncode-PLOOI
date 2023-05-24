package nl.overheid.koop.plooi.plooiiamservice.domain.claims;

import lombok.val;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Role implements Claim {
    public static final String ROLE_CLAIM_KEY = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role";
    public static final String ROLE_KAMERSTUK = "Publisher KST";
    public static final String ROLE_BASIS = "Publisher Basis";

    @Override
    public List<String> getAttribute(Saml2AuthenticatedPrincipal principal) {
        val attribute = principal.getAttribute(ROLE_CLAIM_KEY);

        return attribute != null ? mapToString(attribute) : Collections.emptyList();
    }

    private List<String> mapToString(final List<Object> attribute) {
        return attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    @Override
    public boolean getClaim(String claimType) {
        return claimType.equalsIgnoreCase(ROLE_CLAIM_KEY);
    }
}

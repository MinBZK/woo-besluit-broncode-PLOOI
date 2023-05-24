package nl.overheid.koop.plooi.plooiiamservice.domain.claims;

import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;

import java.util.List;

public interface Claim {
    List<String> getAttribute(Saml2AuthenticatedPrincipal principal);
    boolean getClaim(String claimType);
}

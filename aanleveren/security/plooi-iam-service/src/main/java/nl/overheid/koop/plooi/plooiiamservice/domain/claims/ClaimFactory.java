package nl.overheid.koop.plooi.plooiiamservice.domain.claims;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClaimFactory {
    private final List<Claim> claims;

    public Claim get(final String claimType) {

        return claims.stream()
                .filter(claim -> claim.getClaim(claimType))
                .findFirst()
                .orElse(null);
    }
}

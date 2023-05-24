package nl.overheid.koop.plooi.plooiiamservice.domain;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.JwtClaim;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final SignedJwt signedJwt;
    private final JwtVerifier jwtVerifier;

    public boolean validateToken(final String token) throws ParseException, JOSEException {
        return jwtVerifier.verify(token);
    }

    public List<JwtClaim> getDecodedClaims(final String token) throws ParseException, JOSEException {
        return jwtVerifier.decodeClaims(token);
    }

    public String generateToken(final Saml2AuthenticatedPrincipal principal) throws JOSEException {
        return signedJwt.generate(principal);
    }
}

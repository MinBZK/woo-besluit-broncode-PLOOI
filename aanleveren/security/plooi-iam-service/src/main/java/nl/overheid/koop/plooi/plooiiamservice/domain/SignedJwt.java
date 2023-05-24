package nl.overheid.koop.plooi.plooiiamservice.domain;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.ClaimFactory;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Organisation.ORGANISATION_CLAIM_KEY;
import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Role.ROLE_CLAIM_KEY;

@Component
@RequiredArgsConstructor
class SignedJwt {
    private final RsaKey rsaKey;
    private final ClaimFactory claimFactory;

    String generate(final Saml2AuthenticatedPrincipal principal) throws JOSEException {
        val rsaKey = this.rsaKey.generateKey();
        val signer = new RSASSASigner(rsaKey);

        val signedJWT = new SignedJWT(
                createJWSHeader(rsaKey),
                createClaimsSet(principal));

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    private JWSHeader createJWSHeader(final RSAKey rsaKey) {
        return new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.getKeyID())
                .build();
    }

    private JWTClaimsSet createClaimsSet(final Saml2AuthenticatedPrincipal principal) {
        return new JWTClaimsSet.Builder()
                .expirationTime(getExpirationTime())
                .claim("roles",
                        claimFactory.get(ROLE_CLAIM_KEY)
                                .getAttribute(principal))
                .claim("org",
                        claimFactory.get(ORGANISATION_CLAIM_KEY)
                                .getAttribute(principal))
                .build();
    }

    private Date getExpirationTime() {
        val cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }
}

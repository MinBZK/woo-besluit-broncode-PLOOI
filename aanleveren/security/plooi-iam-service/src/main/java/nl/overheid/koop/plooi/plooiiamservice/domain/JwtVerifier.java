package nl.overheid.koop.plooi.plooiiamservice.domain;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.claims.JwtClaim;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
class JwtVerifier {
    private final RsaKey rsaKey;

    boolean verify(String token) throws ParseException, JOSEException {
        val rsaKey = this.rsaKey.generateKey();
        val rsaPublicKey = rsaKey.toPublicJWK();
        val verifier = new RSASSAVerifier(rsaPublicKey);

        val signedJWT = SignedJWT.parse(token);

        return isValid(signedJWT, verifier) && isNotExpired(signedJWT);
    }

    List<JwtClaim> decodeClaims(String token) throws ParseException, JOSEException {
        if (verify(token)) {
            val signedJWT = SignedJWT.parse(token);
            val claims = signedJWT.getJWTClaimsSet().getClaims();

            return claims.entrySet().stream()
                    .filter(entry -> shouldDecodeClaim(entry.getKey()))
                    .map(entry -> new JwtClaim(entry.getKey(), toList(entry.getValue())))
                    .toList();
        }
        return Collections.emptyList();
    }

    private boolean isValid(final SignedJWT signedJWT, final RSASSAVerifier verifier) throws JOSEException {
        if (signedJWT.verify(verifier)) {
            log.info("JWT is valid");
            return true;
        }
        log.info("JWT is invalid");
        return false;
    }

    private boolean isNotExpired(final SignedJWT signedJWT) throws ParseException {
        if (new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime())) {
            log.info("JWT is not expired");
            return true;
        }
        log.info("JWT is expired");
        return false;
    }

    private boolean shouldDecodeClaim(final String key) {
        return key.equals("org") || key.equals("roles");
    }

    private List<String> toList(final Object value) {
        if (value instanceof List<?> newList) {
            return newList.stream()
                    .map(String::valueOf)
                    .toList();
        }
        return Collections.emptyList();
    }
}

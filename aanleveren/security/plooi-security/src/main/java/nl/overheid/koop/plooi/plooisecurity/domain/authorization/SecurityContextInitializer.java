package nl.overheid.koop.plooi.plooisecurity.domain.authorization;

import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class SecurityContextInitializer {

    public void setSecurityContext(final String token) {
        try {
            val signedJWT = SignedJWT.parse(token.split("Bearer ")[1]);
            val claims = signedJWT.getJWTClaimsSet().getClaims();

            val organisations = getOrganisations(claims);
            val roles = getRoles(claims);

            val authentication
                    = JwtAuthenticationToken.authenticated(null, organisations, null, roles);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ParseException exception) {
            throw new RuntimeException("Unable to set the Security Context");
        }
    }

    private List<String> getOrganisations(final Map<String, Object> claims) {
        return (List<String>)claims.get("org");
    }

    private List<SimpleGrantedAuthority> getRoles(final Map<String, Object> claims) {
        val roles = (List<String>) claims.get("roles");
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}

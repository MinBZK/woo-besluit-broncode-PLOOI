package nl.overheid.koop.plooi.plooisecurity.domain.authorization;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final List<String> organisations;
    private Object credentials;

    private JwtAuthenticationToken(
            Object principal,
            List<String> organisations,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.organisations = organisations;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    public static JwtAuthenticationToken authenticated(
            Object principal,
            List<String> organisations,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        return new JwtAuthenticationToken(principal, organisations, credentials, authorities);
    }

    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public List<String> getOrganisations() {
        return this.organisations;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}

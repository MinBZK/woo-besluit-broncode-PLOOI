package nl.overheid.koop.plooi.plooiiamservice.web;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.configuration.ApplicationProperties;
import nl.overheid.koop.plooi.plooiiamservice.domain.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Organisation.ORGANISATION_CLAIM_KEY;
import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Role.ROLE_CLAIM_KEY;

@RestController
@RequiredArgsConstructor
public class TokenRequestEndpoint {
    private final ApplicationProperties properties;
    private final TokenService tokenService;

    @GetMapping("/token/saml")
    public ResponseEntity<Void> requestSaml(
            @AuthenticationPrincipal Saml2AuthenticatedPrincipal principal,
            @RequestParam(name = "redirect-uri") final String redirectUri) throws JOSEException {
        if (!allowedRedirect(redirectUri)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(
                        URI.create(redirectUri +
                                "?access_token=%s".formatted(tokenService.generateToken(principal))))
                .build();
    }

//    Tijdelijk niet beschikbaar. Wordt opgepakt wanneer DPC via CA gaat aansluiten.
//    @GetMapping("/token/jwt")
//    public ResponseEntity<Object> requestJWT() throws JOSEException {
//        return ResponseEntity.status(HttpStatus.OK).body(tokenService.generateToken());
//    }

    //SSSSSSSSSSSSSSSSSSSSSSSSSSSSS
    @GetMapping("/token/test")
    public ResponseEntity<Map<String, String>> requestTestToken(
            @RequestParam final String org,
            @RequestParam final String role) throws JOSEException {
        val principal = new DefaultSaml2AuthenticatedPrincipal("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                Map.of(ROLE_CLAIM_KEY, List.of(role),
                        ORGANISATION_CLAIM_KEY, List.of(org)));

        return ResponseEntity.ok(
                Map.of("access_token", tokenService.generateToken(principal)));
    }

    private boolean allowedRedirect(final String redirectURI) {
        val decodedRedirectUri = URLDecoder.decode(redirectURI, StandardCharsets.UTF_8);
        return properties.allowedRedirectURIs().contains(decodedRedirectUri);
    }
}

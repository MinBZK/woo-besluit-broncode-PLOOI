package nl.overheid.koop.plooi.plooiiamservice.web;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;


@RestController
@RequiredArgsConstructor
public class TokenIntrospectionEndpoint {
    private final TokenService tokenService;

    @GetMapping("/check_token")
    public ResponseEntity<Void> checkToken(final HttpServletRequest request) {
        val token = request.getHeader("x-access-token");

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            if (!tokenService.validateToken(token))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ParseException | JOSEException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().build();
    }

//    Tijdelijk niet beschikbaar. Wordt opgepakt wanneer DPC via CA gaat aansluiten.
//    @GetMapping("/check_ca_token")
//    public ResponseEntity<Void> checkCaToken() {
//        return ResponseEntity.ok().build();
//    }
}

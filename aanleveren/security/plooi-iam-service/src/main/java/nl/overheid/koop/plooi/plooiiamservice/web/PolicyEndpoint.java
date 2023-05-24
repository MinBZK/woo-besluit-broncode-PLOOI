package nl.overheid.koop.plooi.plooiiamservice.web;

import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.plooiiamservice.domain.TokenService;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.PolicyCheckService;
import nl.overheid.koop.plooi.plooiiamservice.domain.policies.model.PolicyContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class PolicyEndpoint {
    private final PolicyCheckService policyCheckService;
    private final TokenService tokenService;

    @PostMapping("/policies")
    public ResponseEntity<Void> checkPolicy(
            final HttpServletRequest request,
            @RequestBody @Valid final PolicyContext policyContext) throws ParseException, JOSEException {
        val jwtToken = request.getHeader("x-access-token");

        if (jwtToken == null
                || !policyCheckService.enforcePolicy(
                        tokenService.getDecodedClaims(jwtToken), policyContext)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }
}

package nl.overheid.koop.plooi.plooisecurity.domain.policy;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

import static nl.overheid.koop.plooi.plooisecurity.web.filter.AuthorizationFilter.AUTH_HEADER_NAME;

@Component
@RequiredArgsConstructor
public class AttributePolicyEnforcement {
    private static final String BEARER = "Bearer ";
    private final WebClient webClientIamService;
    private final HttpServletRequest request;

    public boolean enforcePolicy(final PolicyContext context) {
        val possibleToken = getAuthorizationHeader();

        return possibleToken.map(token -> webClientIamService
                        .post()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path("/policies")
                                        .build())
                        .bodyValue(context)
                        .header("x-access-token", token.split(BEARER)[1])
                        .exchangeToMono(cr -> Mono.just(cr.statusCode()))
                        .blockOptional()
                        .map(HttpStatus::is2xxSuccessful)
                        .orElse(false))
                .orElse(false);
    }

    private Optional<String> getAuthorizationHeader() {
        return Collections.list(request.getHeaderNames()).stream()
                .filter(this::hasJwtHeader)
                .map(request::getHeader)
                .findFirst();
    }

    private boolean hasJwtHeader(final String headerName) {
        return headerName.equals(AUTH_HEADER_NAME);
    }
}

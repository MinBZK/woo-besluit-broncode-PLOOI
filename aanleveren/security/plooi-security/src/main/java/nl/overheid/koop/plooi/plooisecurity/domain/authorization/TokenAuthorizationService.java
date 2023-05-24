package nl.overheid.koop.plooi.plooisecurity.domain.authorization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthorizationService {
    private static final String BEARER = "Bearer ";
    private final WebClient webClientIamService;

    public boolean isAuthorizationMissing(final String jwt) {
        return jwt == null || jwt.isBlank() || !jwt.startsWith("Bearer ");
    }

    public boolean authorize(final String jwt) {
        return webClientIamService.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/check_token")
                                .build())
                .header("x-access-token", jwt.split(BEARER)[1])
                .exchangeToMono(clientResponse -> Mono.just(clientResponse.statusCode()))
                .blockOptional()
                .filter(HttpStatus::is2xxSuccessful)
                .isPresent();
    }
}

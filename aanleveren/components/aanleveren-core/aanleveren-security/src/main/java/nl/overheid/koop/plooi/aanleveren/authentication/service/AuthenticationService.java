package nl.overheid.koop.plooi.aanleveren.authentication.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class AuthenticationService {
    private static final String AUTHENTICATION_SERVER_URL = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";
    private final WebClient webClient;

    public AuthenticationService() {
        this.webClient = WebClient.builder()
                .baseUrl(AUTHENTICATION_SERVER_URL)
                .build();
    }

    public HttpStatus authenticate(final String aanleverToken) {
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/authenticate")
                                .queryParam("aanlever_token", aanleverToken)
                                .build())
                .exchangeToMono(clientResponse -> Mono.just(clientResponse.statusCode()))
                .block();
    }
}

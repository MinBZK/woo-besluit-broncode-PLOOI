package nl.overheid.koop.plooi.plooisecurity.web.filter;

import org.springframework.web.reactive.function.client.ClientRequest;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

import static nl.overheid.koop.plooi.plooisecurity.web.filter.AuthorizationFilter.AUTH_HEADER_NAME;

public class RequestFilter {
    private final HttpServletRequest request;

    public RequestFilter(HttpServletRequest request) {
        this.request = request;
    }

    public Mono<ClientRequest> propagateAuthorization(final ClientRequest clientRequest) {
        return getHeaderName().map(name ->
                        Mono.just(ClientRequest.from(clientRequest)
                                .header(name, request.getHeader(name))
                                .build()))
                .orElseGet(() -> Mono.just(clientRequest));
    }

    private Optional<String> getHeaderName() {
        return Collections.list(request.getHeaderNames()).stream()
                .filter(this::hasJwtHeader)
                .findFirst();
    }

    private boolean hasJwtHeader(final String headerName) {
        return headerName.equals(AUTH_HEADER_NAME);
    }

}

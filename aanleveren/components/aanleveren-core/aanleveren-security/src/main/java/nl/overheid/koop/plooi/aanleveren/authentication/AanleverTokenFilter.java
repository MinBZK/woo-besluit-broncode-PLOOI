package nl.overheid.koop.plooi.aanleveren.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.authentication.response.ExpiredToken;
import nl.overheid.koop.plooi.aanleveren.authentication.response.InvalidToken;
import nl.overheid.koop.plooi.aanleveren.authentication.response.MissingToken;
import nl.overheid.koop.plooi.aanleveren.authentication.response.WriterFactory;
import nl.overheid.koop.plooi.aanleveren.authentication.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AanleverTokenFilter extends OncePerRequestFilter {
    private static final String AANLEVER_TOKEN = "Aanlever-Token";
    private final AuthenticationService authenticationService;
    private final WriterFactory writerFactory;

    public AanleverTokenFilter() {
        authenticationService = new AuthenticationService();
        writerFactory = new WriterFactory(List.of(new ExpiredToken(), new InvalidToken(), new MissingToken()));
    }

    // x-jwt-token

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        var headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            var element = headerNames.nextElement();
            log.debug("Header name: %s, value: %s".formatted(element, request.getHeader(element)));
        }
        
        log.info("Autoriseren van de aanlevertoken");
        val aanleverToken = request.getHeader(AANLEVER_TOKEN);
        if (aanleverToken == null) {
            log.info("Aanlevertoken ontbreekt");
            writerFactory.getResponseWriter(HttpStatus.FORBIDDEN).write(response);
            return;
        }
        val httpStatus = authenticationService.authenticate(aanleverToken);
        if (!httpStatus.equals(HttpStatus.OK)) {
            log.info("Aanlevertoken is ongeldig");
            writerFactory.getResponseWriter(httpStatus).write(response);
            return;
        }
        log.info("Autorisatie van de aanlevertoken is succesvol afgerond");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return "OPTIONS".equals(request.getMethod())
                || request.getRequestURI().startsWith("/actuator/health");
    }
}

package nl.overheid.koop.plooi.plooisecurity.web.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.plooisecurity.domain.authorization.SecurityContextInitializer;
import nl.overheid.koop.plooi.plooisecurity.domain.authorization.TokenAuthorizationService;
import nl.overheid.koop.plooi.plooisecurity.domain.response.ResponseService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
    public static final String AUTH_HEADER_NAME = "authorization";
    private final TokenAuthorizationService authorizationService;
    private final SecurityContextInitializer securityContextInitializer;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        log.info("Authorization is being processed");
        val responseService = new ResponseService();
        val token = request.getHeader(AUTH_HEADER_NAME);

        if (!authorizationService.isAuthorizationMissing(token)) {
            if (!authorizationService.authorize(token)) {
                responseService.unauthorized(response);
                return;
            }
        } else {
            responseService.forbidden(response);
            log.info("Authorization is missing");
            return;
        }

        securityContextInitializer.setSecurityContext(token);
        log.info("Authorization is successful");
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        return "OPTIONS".equals(request.getMethod())
                || request.getRequestURI().startsWith("/actuator/health");
    }
}

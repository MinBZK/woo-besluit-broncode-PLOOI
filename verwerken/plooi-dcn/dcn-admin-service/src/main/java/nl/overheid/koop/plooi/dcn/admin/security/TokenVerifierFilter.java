package nl.overheid.koop.plooi.dcn.admin.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This filter will check if requests to admin api contains header with valid token. if token is not valid, requested
 * resources will be not allowed.
 */
@Component
public class TokenVerifierFilter extends OncePerRequestFilter {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JwtUtils jwtUtils;
    
    public TokenVerifierFilter(JwtUtils jwtUtils) {
    	this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/admin")) {
            String token = request.getHeader(AUTHORIZATION);
            try {
            	logger.debug("validated token for request {}", request.getRequestURL());
                this.jwtUtils.validateToken(token);
                String username = this.jwtUtils.getUserNameFromJwtToken(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException exception) {
            	logger.info("Cannot validate token: {}", token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}

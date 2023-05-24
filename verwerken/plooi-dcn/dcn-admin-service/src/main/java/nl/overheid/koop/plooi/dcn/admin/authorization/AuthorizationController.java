package nl.overheid.koop.plooi.dcn.admin.authorization;

import com.auth0.jwt.exceptions.TokenExpiredException;
import java.util.Objects;
import nl.overheid.koop.plooi.dcn.admin.security.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthorizationController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthorizationResponse> generateAuthenticationToken(@RequestBody AuthorizationRequest authenticationRequest) {
        Authentication authentication = authenticate(authenticationRequest.username(), authenticationRequest.password());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String token = this.jwtUtils.generateJwtToken(userDetails.getUsername());
        final String refreshToken = this.jwtUtils.generateJwtRefreshToken(userDetails.getUsername());
        this.logger.info("user {} is logged in and token is generated", authenticationRequest.username());
        return ResponseEntity.ok(new AuthorizationResponse(token, userDetails.getUsername(), refreshToken));
    }

    @PostMapping(value = "/refreshtoken")
    public ResponseEntity<AuthorizationResponse> generateRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            if (StringUtils.isEmpty(refreshTokenRequest.refreshToken())) {
                throw new TokenExpiredException("Incoming RefreshToken cannot be null or empty.");
            }
            this.jwtUtils.validateRefreshToken(refreshTokenRequest.refreshToken());
            final String token = this.jwtUtils.generateJwtToken(refreshTokenRequest.username());
            final String refreshToken = this.jwtUtils.generateJwtRefreshToken(refreshTokenRequest.username());
            this.logger.debug("Refresh Token is created for user {}", refreshTokenRequest.username());
            return ResponseEntity.ok(new AuthorizationResponse(token, refreshTokenRequest.username(), refreshToken));
        } catch (TokenExpiredException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    private Authentication authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        this.logger.debug("authenticate user {}", username);
        return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}

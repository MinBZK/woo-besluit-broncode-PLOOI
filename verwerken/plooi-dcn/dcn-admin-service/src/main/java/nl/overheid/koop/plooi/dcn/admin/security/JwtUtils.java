package nl.overheid.koop.plooi.dcn.admin.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DCNJwtConfig jwtConfig;

    public JwtUtils(DCNJwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateJwtToken(String username) {
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException("Token can not created for user with empty or null name");
        }
        try {
            String token = JWT
                    .create()
                    .withSubject(username)
                    .withIssuedAt(new Date())
                    .withExpiresAt(getExpireDate(this.jwtConfig.getExpirationMs()))
                    .sign(this.jwtConfig.jwtAlgorithm());
            this.logger.trace("Token is generated for user {} will expired at {}", username, getExpireDate(this.jwtConfig.getExpirationMs()));
            return String.format("%s %s", this.jwtConfig.getTokenPrefix(), token);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Token not generated" + e.getMessage());
        }
    }

    private Date getExpireDate(long expirationDate) {
        return new Date((new Date()).getTime() + expirationDate);
    }

    public void validateToken(String header) {
        String token = getToken(header);
        JWTVerifier verifier = JWT.require(this.jwtConfig.jwtAlgorithm()).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        if (decodedJWT.getExpiresAt().before(new Date())) {
            this.logger.info("Token is expired at {}", decodedJWT.getExpiresAt());
            throw new TokenExpiredException("Expired date");
        }

    }

    public String getUserNameFromJwtToken(String header) {
        String token = getToken(header);
        return JWT.decode(token).getSubject();
    }

    private String getToken(String header) {
        if (StringUtils.isEmpty(header)) {
            throw new IllegalArgumentException("Empty or null token");
        }
        return header.substring(this.jwtConfig.getTokenPrefix().length()).trim();
    }

    public String generateJwtRefreshToken(String userName) {
        try {
            String token = JWT
                    .create()
                    .withSubject(userName)
                    .withIssuedAt(new Date())
                    .withExpiresAt(getExpireDate(this.jwtConfig.getRefreshExpirationMs()))
                    .sign(this.jwtConfig.jwtAlgorithm());
            this.logger.trace("Refresh Token is generated will expired at {}", getExpireDate(this.jwtConfig.getRefreshExpirationMs()));
            return token;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Token not generated" + e.getMessage());
        }
    }

    public void validateRefreshToken(String refreshToken) {
        JWTVerifier verifier = JWT.require(this.jwtConfig.jwtAlgorithm()).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        if (decodedJWT.getExpiresAt().before(new Date())) {
            this.logger.info("Token is expired at {}", decodedJWT.getExpiresAt());
            throw new TokenExpiredException("Refresh token is expired");
        }
    }
}

package nl.overheid.koop.plooi.dcn.admin.security;

import static org.assertj.core.api.Assertions.assertThat;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ContextConfiguration(classes = { JwtUtils.class })
@TestPropertySource(locations = "classpath:application.properties")
@EnableConfigurationProperties(DCNJwtConfig.class)
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void exceptionThrownWhenUserNameIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.jwtUtils.generateJwtToken(null);
        });
    }

    @Test
    void exceptionThrownWhenUserNameIsEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.jwtUtils.generateJwtToken("");
        });
    }

    @Test
    void generateTokenByValidUser() {
        String token = this.jwtUtils.generateJwtToken("johan");
        assertThat(token)
                .isNotNull()
                .startsWith("Bearer");
    }

    @Test
    @Disabled("Test is broken after moving it to this module")
    void expiredTokenThrowingExcecption() {
        String header = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";
        Assertions.assertThrows(TokenExpiredException.class, () -> {
            this.jwtUtils.validateToken(header);
        });
    }

    @Test
    void invalidateToken() {
        String header = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";
        Assertions.assertThrows(JWTVerificationException.class, () -> {
            this.jwtUtils.validateToken(header);
        });
    }

    @Test
    void validateEmptyToken() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.jwtUtils.validateToken("");
        });
    }
}

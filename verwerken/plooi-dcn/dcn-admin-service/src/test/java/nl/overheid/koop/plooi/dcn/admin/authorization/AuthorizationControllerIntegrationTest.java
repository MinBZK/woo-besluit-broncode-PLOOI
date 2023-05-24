package nl.overheid.koop.plooi.dcn.admin.authorization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("unit-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled("Test is broken after moving it to this module")
class AuthorizationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate template;

    @Test
    void givenLogInRequestOnValidWithUserNameAndPassword_shouldSucceedWith200() {

        AuthorizationRequest authorizationRequest = new AuthorizationRequest("test123", "test123");

        ResponseEntity<String> response = this.template.postForEntity("/authorization/login",
                authorizationRequest,
                String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenLogInRequestOnInValidWithUserNameAndPassword_shouldFailedWith401() {

        AuthorizationRequest authorizationRequest = new AuthorizationRequest("test123", "invalidPassword");

        ResponseEntity<String> response = this.template.postForEntity("/authorization/login",
                authorizationRequest,
                String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}

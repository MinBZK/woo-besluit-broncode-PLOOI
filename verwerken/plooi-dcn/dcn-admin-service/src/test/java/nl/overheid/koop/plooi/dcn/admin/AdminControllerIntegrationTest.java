package nl.overheid.koop.plooi.dcn.admin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import nl.overheid.koop.plooi.dcn.admin.security.JwtUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("unit-test")
@Disabled("Test is broken after moving it to this module")
class AdminControllerIntegrationTest {

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
        template.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", jwtUtils.generateJwtToken("john"));
                    return execution.execute(request, body);
                }));

        ResponseEntity<String> result = template.getForEntity("/admin/dcn-admin", String.class);
        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void givenAuthRequestOnPrivateService_WithWrongToken_shouldFieldWith401() throws Exception {
        template.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", "123");
                    return execution.execute(request, body);
                }));

        ResponseEntity<String> result = template.getForEntity("/admin/dcn-admin", String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void givenAuthRequestOnPrivateService_WithNullToken_shouldFieldWith401() throws Exception {
        System.out.println(jwtUtils.generateJwtToken("john"));
        template.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", null);
                    return execution.execute(request, body);
                }));

        ResponseEntity<String> result = template.getForEntity("/admin/dcn-admin", String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void givenAuthRequestOnPrivateService_WithExpiredToken_shouldFieldWith401() {
        String expiredToken = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";
        template.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", expiredToken);
                    return execution.execute(request, body);
                }));

        ResponseEntity<String> result = template.getForEntity("/admin/dcn-admin", String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }
}
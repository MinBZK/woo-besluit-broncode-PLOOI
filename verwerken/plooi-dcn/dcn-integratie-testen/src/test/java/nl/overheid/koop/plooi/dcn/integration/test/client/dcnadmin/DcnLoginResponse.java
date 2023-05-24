package nl.overheid.koop.plooi.dcn.integration.test.client.dcnadmin;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DcnLoginResponse {

    private final String userName;
    private final String token;
    private final String refreshToken;

    public DcnLoginResponse(String userName, String token, String refreshToken) {
        this.userName = userName;
        this.token = token;
        this.refreshToken = refreshToken;
        assertFieldsNotNull();
    }

    private void assertFieldsNotNull() {
        assertNotNull(this.userName);
        assertNotNull(this.token);
        assertNotNull(this.refreshToken);
    }

    public String getUserName() {
        return this.userName;
    }

    public String getToken() {
        return this.token;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }
}

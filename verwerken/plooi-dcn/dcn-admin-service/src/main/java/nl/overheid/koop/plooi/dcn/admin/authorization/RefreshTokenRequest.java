package nl.overheid.koop.plooi.dcn.admin.authorization;

public record RefreshTokenRequest(String username, String refreshToken) {
}

package nl.overheid.koop.plooi.dcn.admin.authorization;

public record AuthorizationResponse(String token, String userName, String refreshToken) {
}

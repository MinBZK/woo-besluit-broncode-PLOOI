package nl.overheid.koop.plooi.plooiiamservice.domain.claims;

import java.util.List;

public record JwtClaim(String key, List<String> values) { }

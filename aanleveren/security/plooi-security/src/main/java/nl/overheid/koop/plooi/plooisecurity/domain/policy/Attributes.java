package nl.overheid.koop.plooi.plooisecurity.domain.policy;

import java.util.List;

public record Attributes(String organisatie_id, List<String> documentsoorten) {}

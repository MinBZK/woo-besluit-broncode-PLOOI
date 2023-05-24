package nl.overheid.koop.plooi.plooiiamservice.domain.policies.model;

import javax.validation.constraints.NotNull;
import java.util.List;

public record Attributes(
        @NotNull String organisatie_id,
        @NotNull List<String> documentsoorten) {}

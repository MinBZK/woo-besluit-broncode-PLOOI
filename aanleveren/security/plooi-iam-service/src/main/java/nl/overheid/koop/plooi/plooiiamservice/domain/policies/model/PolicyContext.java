package nl.overheid.koop.plooi.plooiiamservice.domain.policies.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public record PolicyContext(
        @NotNull String action,
        @NotNull @Valid Attributes attributes) {}

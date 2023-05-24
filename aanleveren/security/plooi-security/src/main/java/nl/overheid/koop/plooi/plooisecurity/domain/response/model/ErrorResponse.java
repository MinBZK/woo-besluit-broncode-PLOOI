package nl.overheid.koop.plooi.plooisecurity.domain.response.model;

import lombok.Builder;

import java.util.List;

@Builder
public record ErrorResponse(
        String type,
        String title,
        Integer status,
        String detail,
        String instance,
        List<String> messages) {}
package nl.overheid.koop.plooi.aanleveren.metadata.web.response;

import java.util.List;

public record AanleverenResponseValidatiebericht(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        List<ValidatieBericht> messages) {}

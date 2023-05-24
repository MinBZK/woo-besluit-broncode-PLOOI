package nl.overheid.koop.plooi.aanleveren.document.web.response;

import java.util.List;

public record AanleverenResponseError(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        List<String> supportedMediaTypes) {

    public AanleverenResponseError(String type, String title, int status, String detail, String instance) {
        this(type, title, status, detail, instance, null);
    }
}


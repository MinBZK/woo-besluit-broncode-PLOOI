package nl.overheid.koop.plooi.aanleveren.metadata.web.response;

import java.util.List;

public record AanleverenResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        List<String> messages,
        List<String> supportedMediaTypes) {

    public AanleverenResponse(String type,
                              String title,
                              int status,
                              String detail,
                              String instance,
                              List<String> messages) {
        this(type, title, status, detail, instance, messages, null);
    }
}

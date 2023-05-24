package nl.overheid.koop.plooi.search.service.error;

import org.springframework.http.HttpStatus;

public final class ResponseErrorHelper {

    private ResponseErrorHelper() {
    }

    public static HttpStatusException inputValueError(String message, String value, String advice) {
        return new HttpStatusException(HttpStatus.BAD_REQUEST, String.format("%s [%s] %s", message, value, advice));
    }

}

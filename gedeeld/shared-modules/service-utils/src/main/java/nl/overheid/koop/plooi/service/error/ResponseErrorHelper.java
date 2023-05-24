package nl.overheid.koop.plooi.service.error;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

public final class ResponseErrorHelper {

    private ResponseErrorHelper() {
    }

    public static HttpStatusException inputError(String subject, String... messageParts) {
        return error(HttpStatus.BAD_REQUEST, subject, messageParts);
    }

    public static HttpStatusException inputError(String subject, Exception exception) {
        return error(HttpStatus.BAD_REQUEST, subject, exception);
    }

    public static HttpStatusException internalError(String subject, String... messageParts) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, subject, messageParts);
    }

    public static HttpStatusException internalError(String subject, Exception exception) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, subject, exception);
    }

    public static HttpStatusException unknownParameter(String subject, String parameter, String value) {
        return error(HttpStatus.NOT_FOUND, subject, String.format("Unknown %s %s", parameter, value));
    }

    public static HttpStatusException error(HttpStatus httpStatus, String subject, String... messageParts) {
        return new HttpStatusException(httpStatus, String.join(" ", messageParts) + (StringUtils.isBlank(subject) ? "" : " for " + subject));
    }

    public static HttpStatusException error(HttpStatus httpStatus, String subject, Exception exception) {
        return error(httpStatus, subject, ExceptionUtils.getRootCauseMessage(exception));
    }
}

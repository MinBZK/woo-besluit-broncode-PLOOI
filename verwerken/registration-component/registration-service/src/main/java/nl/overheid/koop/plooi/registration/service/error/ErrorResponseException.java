package nl.overheid.koop.plooi.registration.service.error;

import org.springframework.http.HttpStatus;

public class ErrorResponseException extends RuntimeException {

    private static final long serialVersionUID = 8383455304174306961L;
    private final ErrorResponse errorResponse;

    public ErrorResponseException(HttpStatus status, String message) {
        this.errorResponse = new ErrorResponse(status, message);
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }
}

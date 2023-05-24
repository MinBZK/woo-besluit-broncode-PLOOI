package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

import com.networknt.schema.ValidationMessage;

import java.util.Set;

public class BadRequestException extends RuntimeException {

    private Set<ValidationMessage> validationMessages;

    public BadRequestException() {
        super();
    }

    public BadRequestException(final String message) {
        super(message);
    }

    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(final Set<ValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
    }

    public Set<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }
}

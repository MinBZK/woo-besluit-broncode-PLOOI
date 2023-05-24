package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.networknt.schema.ValidationMessage;

import java.util.Set;

public class PlooiJsonProcessingException extends JsonProcessingException {

    private final Set<ValidationMessage> validationMessages;

    public PlooiJsonProcessingException(final Set<ValidationMessage> validationMessages) {
        super(validationMessages.toString());
        this.validationMessages = validationMessages;
    }

    public Set<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

}

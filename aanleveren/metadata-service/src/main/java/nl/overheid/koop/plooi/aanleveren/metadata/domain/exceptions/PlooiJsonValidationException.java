package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

import com.networknt.schema.ValidationMessage;

import java.util.Set;

public class PlooiJsonValidationException extends BadRequestException {

    public PlooiJsonValidationException(final Set<ValidationMessage> validationMessages) {
        super(validationMessages);
    }
}

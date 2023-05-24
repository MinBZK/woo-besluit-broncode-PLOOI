package nl.overheid.koop.plooi.aanleveren.document.domain.service.validation;

import lombok.RequiredArgsConstructor;
import nl.overheid.koop.plooi.aanleveren.document.domain.exceptions.ResourceTypeException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationFactory {
    private final List<Validator> validators;

    public Validator getCorrectValidator(final byte[] resource) {
        return validators.stream()
                .filter(validator -> validator.isCorrectType(resource))
                .findFirst()
                .orElseThrow(() -> new ResourceTypeException("Content is not PDF or ZIP"));
    }
}

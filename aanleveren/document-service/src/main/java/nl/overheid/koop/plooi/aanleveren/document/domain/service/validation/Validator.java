package nl.overheid.koop.plooi.aanleveren.document.domain.service.validation;


import nl.overheid.koop.plooi.aanleveren.document.domain.exceptions.ResourceTooLargeException;

public interface Validator {
    boolean isValid(byte[] resource);

    boolean isCorrectType(byte[] resource);

    default void checkResourceNotTooLarge(final byte[] resource, final Long maxFileSize) {
        if (resource.length > maxFileSize) {
            throw new ResourceTooLargeException("Resource is too large");
        }
    }
}


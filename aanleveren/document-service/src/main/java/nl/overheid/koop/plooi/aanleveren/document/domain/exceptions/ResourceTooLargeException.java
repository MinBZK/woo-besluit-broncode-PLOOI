package nl.overheid.koop.plooi.aanleveren.document.domain.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceTooLargeException extends RuntimeException {

    public ResourceTooLargeException(final String message) {
        super(message);
        log.error(message);
    }
}

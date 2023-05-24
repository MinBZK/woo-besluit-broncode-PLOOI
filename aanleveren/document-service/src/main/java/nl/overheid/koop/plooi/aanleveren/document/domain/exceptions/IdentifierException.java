package nl.overheid.koop.plooi.aanleveren.document.domain.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdentifierException extends RuntimeException{

    public IdentifierException(final String message) {
        super();
        log.error(message);
    }
}

package nl.overheid.koop.plooi.aanleveren.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotValidUUIDExeception extends RuntimeException {

    public NotValidUUIDExeception(final String message) {
        super(message);
        log.error(message);
    }
}

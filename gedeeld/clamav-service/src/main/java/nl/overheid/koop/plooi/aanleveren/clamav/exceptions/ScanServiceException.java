package nl.overheid.koop.plooi.aanleveren.clamav.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScanServiceException extends RuntimeException {

    public ScanServiceException(final String message) {
        super(message);
        log.error(message);
    }
}

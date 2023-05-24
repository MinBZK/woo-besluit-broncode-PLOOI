package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotValidUUIDException extends BadRequestException {

    public NotValidUUIDException() {
        super();
    }
}

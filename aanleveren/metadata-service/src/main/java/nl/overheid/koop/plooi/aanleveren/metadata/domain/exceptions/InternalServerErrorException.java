package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(final String message) {
        super(message);
    }
}

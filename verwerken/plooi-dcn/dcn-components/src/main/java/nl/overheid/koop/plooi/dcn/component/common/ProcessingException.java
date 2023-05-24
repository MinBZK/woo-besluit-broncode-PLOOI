package nl.overheid.koop.plooi.dcn.component.common;

public class ProcessingException extends RuntimeException {

    private static final long serialVersionUID = -8066905385665698613L;

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

package nl.overheid.koop.plooi.document.normalize;

public class PlooiNormalizationException extends RuntimeException {

    private static final long serialVersionUID = -4886885140517441576L;

    public PlooiNormalizationException(String message) {
        super(message);
    }

    public PlooiNormalizationException(String message, Throwable cause) {
        super(message, cause);
    }
}

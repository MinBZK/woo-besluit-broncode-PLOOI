package nl.overheid.koop.plooi.document.map;

public class PlooiMappingException extends RuntimeException {

    private static final long serialVersionUID = 1015296844365466013L;

    public PlooiMappingException(String message) {
        super(message);
    }

    public PlooiMappingException(Throwable cause) {
        super(cause);
    }

    public PlooiMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}

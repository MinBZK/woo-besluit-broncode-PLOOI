package nl.overheid.koop.plooi.repository.storage;

public class StorageException extends RuntimeException {

    private static final long serialVersionUID = -8066905385665698613L;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

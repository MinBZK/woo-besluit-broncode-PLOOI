package nl.overheid.koop.plooi.model.dictionary;

public class TooiDictionaryException extends RuntimeException {

    private static final long serialVersionUID = -1442145632169920859L;

    public TooiDictionaryException(String message, Exception e) {
        super(message, e);
    }
}

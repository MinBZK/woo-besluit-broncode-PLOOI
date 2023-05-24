package nl.overheid.koop.plooi.search.service;

public class Filter {

    enum Type {
        ID_STRING_FILTER,
        SINGLE_STRING_FILTER,
        MULTI_STRING_FILTER,
        DATE_FILTER,
        ZICHTBAARHEIDS_DATUM_TIJD_FILTER
    }

    private final Type type;
    private final String key;
    private final Object value;

    public Filter(Type type, String key, Object value) {
        super();
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Type getType() {
        return this.type;
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }

}

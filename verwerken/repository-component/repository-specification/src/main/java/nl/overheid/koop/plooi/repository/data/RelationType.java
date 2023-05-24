package nl.overheid.koop.plooi.repository.data;

import java.util.stream.Stream;

public enum RelationType {

    IDENTITEITS_GROEPEN("https://identifier.overheid.nl/plooi/def/thes/documentrelatie/identiteitsgroep", Type.GROEP),
    ONDERDEEL("https://identifier.overheid.nl/plooi/def/thes/documentrelatie/onderdeel", Type.DIRECT),
    BUNDEL("https://identifier.overheid.nl/plooi/def/thes/documentrelatie/bundel", Type.DIRECT),
    BIJLAGE("https://identifier.overheid.nl/plooi/def/thes/documentrelatie/bijlage", Type.DIRECT),
    ;

    private final String uri;
    private final Type type;

    RelationType(String uri, Type t) {
        this.uri = uri;
        this.type = t;
    }

    public String getUri() {
        return this.uri;
    }

    public Type getType() {
        return this.type;
    }

    public static RelationType forUri(String uri) {
        return Stream.of(RelationType.values())
                .filter(rt -> rt.uri.equals(uri))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown RelationType uri " + uri));
    }

    public enum Type {
        DIRECT, GROEP,
    }
}

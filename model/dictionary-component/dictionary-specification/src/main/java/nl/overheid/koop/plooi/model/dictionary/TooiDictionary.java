package nl.overheid.koop.plooi.model.dictionary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import nl.overheid.koop.plooi.model.dictionary.model.Label;

public class TooiDictionary {

    private final String scheme;
    private final Map<String, String> uris = new HashMap<>();
    private final Map<String, String> prefLabels = new HashMap<>();
    private final Map<String, String> altLabels = new HashMap<>();
    private final Collection<Label> displayLabels = new TreeSet<>();

    public TooiDictionary(String s) {
        this.scheme = s;
    }

    public String getScheme() {
        return this.scheme;
    }

    public Collection<Label> getDisplayLabels() {
        return this.displayLabels;
    }

    /** Returns the preferred term of a concept, given its URI */
    public Optional<String> findUri(String uri) {
        return Optional.ofNullable(this.uris.get(uri));
    }

    Map<String, String> getUris() {
        return this.uris;
    }

    /** Returns the URI of a concept, given its preferred term */
    public Optional<String> findPrefLabel(String label) {
        return Optional.ofNullable(this.prefLabels.get(label.toUpperCase()));
    }

    Map<String, String> getPrefLabels() {
        return this.prefLabels;
    }

    /** Returns the URI of a concept, given an alternative label */
    public Optional<String> findAltLabel(String label) {
        return Optional.ofNullable(this.altLabels.get(label.toUpperCase()));
    }

    Map<String, String> getAltLabels() {
        return this.altLabels;
    }

    @Override
    public String toString() {
        return this.scheme + " dictionary with " + this.uris.size() + " uris";
    }
}

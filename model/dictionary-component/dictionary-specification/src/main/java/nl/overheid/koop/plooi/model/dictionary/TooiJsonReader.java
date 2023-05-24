package nl.overheid.koop.plooi.model.dictionary;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import nl.overheid.koop.plooi.model.dictionary.model.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class TooiJsonReader {

    private static final String PREF_LABEL = "prefLabel";
    private static final String ALT_LABEL = "altLabel";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    void read(TooiDictionary dict, InputStream jsonStream) {
        try (var jsonReader = Json.createReader(jsonStream)) {
            for (var val : jsonReader.readArray()) {
                JsonObject concept = val.asJsonObject();
                var uri = concept.getString("@id");
                boolean invalidated = concept.containsKey("http://www.w3.org/ns/prov#invalidatedAtTime");
                if (concept.containsKey("@type")
                        && concept.getJsonArray("@type").getValuesAs(JsonValue::toString).contains("\"http://www.w3.org/2004/02/skos/core#Collection\"")) {
                    // SKOS Collections are ignored
                } else {
                    // Works for SKOS dictionaries:
                    readLabel(dict.getScheme(), PREF_LABEL, uri,
                            concept.get("http://www.w3.org/2004/02/skos/core#prefLabel"), dict.getUris(), dict.getPrefLabels(), invalidated);
                    readLabel(dict.getScheme(), ALT_LABEL, uri,
                            concept.get("http://www.w3.org/2004/02/skos/core#altLabel"), null, dict.getAltLabels(), invalidated);
                    // Works for instanties:
                    readLabel(dict.getScheme(), PREF_LABEL, uri,
                            concept.get("http://www.w3.org/2000/01/rdf-schema#label"), dict.getUris(), dict.getPrefLabels(), invalidated);
                    readLabel(dict.getScheme(), ALT_LABEL, uri,
                            concept.get("https://identifier.overheid.nl/tooi/def/ont/officieleNaamExclSoort"), null, dict.getAltLabels(), invalidated);
                    addDisplayLabel(uri, dict.getUris(), dict.getDisplayLabels(), invalidated);
                }
            }
        }
    }

    private void readLabel(String dict, String type, String uri, JsonValue label, Map<String, String> uris, Map<String, String> labels, boolean invalidated) {
        if (label != null) {
            switch (label.getValueType()) {
                case ARRAY:
                    doPutArray(label.asJsonArray(), dict, type, uri, uris, labels, invalidated);
                    break;
                case OBJECT:
                    doPut(dict, type, uri, label.asJsonObject().getString("@value", "Onbekend"), uris, labels, invalidated);
                    break;
                case STRING:
                    doPut(dict, type, uri, label.toString(), uris, labels, invalidated);
                    break;
                default:
                    this.logger.warn("Unsupported type {} for {}", label, uri);
            }
        }
    }

    private void doPutArray(JsonArray array, String dict, String type, String uri, Map<String, String> uris, Map<String, String> labels, boolean invalidated) {
        if (!array.isEmpty()) {
            if (PREF_LABEL.equals(type) && array.size() > 1) {
                this.logger.warn("Multiple prefLabels for {}", uri);
            }
            array.forEach(l -> doPut(dict, type, uri, l.asJsonObject().getString("@value", ""), uris, labels, invalidated));
        }
    }

    private void doPut(String dict, String type, String uri, String label, Map<String, String> uris, Map<String, String> labels, boolean invalidated) {
        if (!label.isEmpty()) {
            if (uris != null && !uris.containsKey(uri)) {
                uris.put(uri, label);
            } else if (uris != null) {
                this.logger.warn("Found duplicate uri in {}: {}", dict, uri);
            }
            if (labels.containsKey(label.toUpperCase())) {
                this.logger.info("Found duplicate {} in {}: {}", type, dict, label);
            }
            // Making sure we don't overwrite an existing value with an invalidated one
            if (!invalidated || !labels.containsKey(label.toUpperCase())) {
                labels.put(label.toUpperCase(), uri);
            }
        }
    }

    private void addDisplayLabel(String uri, Map<String, String> uris, Collection<Label> displayLabels, boolean invalidated) {
        if (!invalidated && uris.containsKey(uri)) {
            displayLabels.add(new SortableLabel().uri(uri).label(uris.get(uri)));
        }
    }
}

class SortableLabel extends Label implements Comparable<SortableLabel> {

    @Override
    public int compareTo(SortableLabel other) {
        return this.getLabel().equals(other.getLabel()) ? this.getUri().compareTo(other.getLabel()) : this.getLabel().compareTo(other.getLabel());
    }
}

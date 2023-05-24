package nl.overheid.koop.plooi.aanleveren.waardelijst.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class WaardeItem {
    private String identifier;
    private String label;

    @JsonProperty("identifier")
    public String getIdentifier() {
        return identifier;
    }

    @JsonProperty("@id")
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @JsonProperty("label")
    public String getLabel() {
        return this.label;
    }

    @JsonProperty("http://www.w3.org/2004/02/skos/core#prefLabel")
    public void setPrefLabel(List<Map<String, Object>> prefLabel) {
        this.label = (String) prefLabel.get(0).get("@value");
    }

    @JsonProperty("http://www.w3.org/2000/01/rdf-schema#label")
    public void setLabel(List<Map<String, Object>> label) {
        this.label = (String) label.get(0).get("@value");
    }
}

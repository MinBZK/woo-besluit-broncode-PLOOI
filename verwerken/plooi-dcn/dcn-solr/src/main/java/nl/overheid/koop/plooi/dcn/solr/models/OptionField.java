package nl.overheid.koop.plooi.dcn.solr.models;

import java.util.List;

public record OptionField(String name, List<Option> options) {
}

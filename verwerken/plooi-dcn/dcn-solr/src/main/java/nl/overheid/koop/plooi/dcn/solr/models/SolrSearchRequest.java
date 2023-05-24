package nl.overheid.koop.plooi.dcn.solr.models;

import java.util.List;

public record SolrSearchRequest(String query, List<SimpleField> textFields, List<OptionField> filterFields, DateRangeField dateRangeField) {
	public SolrSearchRequest() {
		this(null, null, null, null);
	}
}

package nl.overheid.koop.plooi.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilterQueryListBuilder {

    private List<Filter> list = new ArrayList<>();
    private List<String> filterQueries = new ArrayList<>();

    public FilterQueryListBuilder() {
    }

    @SuppressWarnings({ "unlikely-arg-type", "rawtypes" })
    public FilterQueryListBuilder add(Filter.Type type, String key, Object value) {
        if (key == null || key == "" || value == null || value == "" || this.list.contains(key) || ((value instanceof List) && ((List) value).isEmpty())
                || ((value instanceof String) && ((String) value == ""))) {
            return this;
        }

        this.list.add(new Filter(type, key, value));

        return this;
    }

    public FilterQueryListBuilder addZichtbaarHeidsDatumTijdFilter() {
        this.list.add(new Filter(Filter.Type.ZICHTBAARHEIDS_DATUM_TIJD_FILTER, null, null));

        return this;
    }

    public void print() {
        for (Filter filter : this.list) {
            System.out.printf("%s: %s: %s", filter.getKey(), filter.getType(), filter.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> buildFilterQueries() {
        this.list.forEach((f) -> {
            if (f.getType().equals(Filter.Type.DATE_FILTER)) {
                this.filterQueries.add(SolrQueryUtil.prepareDateFilterQuery(f.getKey(), f.getValue()));
            }

            if (f.getType().equals(Filter.Type.ID_STRING_FILTER)) {
                this.filterQueries.add(SolrQueryUtil.prepareIDFilterQuery((String) f.getValue()));
            }

            if (f.getType().equals(Filter.Type.SINGLE_STRING_FILTER)) {
                this.filterQueries.add(SolrQueryUtil.prepareFilterQuery(f.getKey(), (String) f.getValue()));
            }

            if (f.getType().equals(Filter.Type.MULTI_STRING_FILTER)) {
                this.filterQueries.add(SolrQueryUtil.prepareFilterQuery(f.getKey(), (List<String>) f.getValue()));
            }

            if (f.getType().equals(Filter.Type.ZICHTBAARHEIDS_DATUM_TIJD_FILTER)) {
                this.filterQueries.add(SolrQueryUtil.prepareZichtbaarheidfilter());
            }

        });

        return this.filterQueries.stream()
                .filter(Objects::nonNull)
                .toList();
    }
}

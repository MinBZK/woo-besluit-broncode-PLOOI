package nl.overheid.koop.plooi.dcn.solr.models;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

public final class SolrQueryUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrQueryUtil.class);

    private static final String DEFAULT_SOLR_DATE = "*";
    private static final String RANGE_SIGN = " TO ";
    private static final String START_BRACKET = "[";
    private static final String END_BRACKET = "]";
    private static final String AND = " AND ";
    private static final String MODIFIED = "modified";
    private static final String ID = "identifier";
    private static final String OR = " OR ";
    private static final String QUOTES = "\"";
    private static final String COLON = ":";
    private static final String CURSOR_MARK = "cursorMark";
    private static final int ROWS = 1000;

    private SolrQueryUtil() {
        // Cannot instantiate util class
    }

    public static SolrQuery createFilterSolrQuery(SolrSearchRequest filter, Pageable page, String[] fields) {
        LOGGER.debug("convert filter request {} to Solr query", filter);
        SolrQuery query = mapSearchRequestToQuery(filter);
        addPagingAndSorting(page, query, fields);
        LOGGER.debug("create solr query {}", query);

        return query;
    }

    public static SolrQuery createSolrQueryForBulk(SolrSearchRequest filter, String[] fields, String cursorMark) {
        SolrQuery query = mapSearchRequestToQuery(filter);
        query.setFields(fields);
        query.set(CURSOR_MARK, cursorMark);
        query.set("rows", ROWS);
        query.setSort(ID, SolrQuery.ORDER.desc);
        return query;
    }

    private static SolrQuery mapSearchRequestToQuery(SolrSearchRequest filter) {
        SolrQuery query = new SolrQuery();
        if (StringUtils.isNotEmpty(filter.query())) {
            addPlainQuery(filter, query);
        } else {
            for (int i = 0; i < filter.textFields().size(); i++) {
                filter.textFields()
                        .set(i, new SimpleField(
                                filter.textFields().get(i).name(),
                                ClientUtils.escapeQueryChars(filter.textFields().get(i).value())));
            }
            addFilterQueries(filter, query);
            addSimpleFields(filter, query);
        }
        return query;
    }

    public static void addSimpleFields(SolrSearchRequest filter, SolrQuery query) {
        List<SimpleField> simpleFields = new ArrayList<>(filter.textFields());
        if (filter.dateRangeField() != null) {
            addDateRangeToSimpleFields(filter, simpleFields);
        }
        String simpleFieldsQuery = simpleFields
                .stream()
                .map(simpleField -> simpleField.name()
                        .concat(COLON)
                        .concat(simpleField.value()))
                .collect(Collectors.joining(AND));
        query.set("q", simpleFieldsQuery);
    }

    private static void addPlainQuery(SolrSearchRequest filter, SolrQuery query) {
        if (StringUtils.isBlank(filter.query())) {
            return;
        }
        query.set("q", filter.query());
    }

    public static void addFilterQueries(SolrSearchRequest filter, SolrQuery query) {
        List<OptionField> optionFields = filter.filterFields();
        if (optionFields == null || optionFields.isEmpty()) {
            return;
        }

        query.setFilterQueries(optionFields
                .stream()
                .map(SolrQueryUtil::getSolrFilterQuery)
                .collect(Collectors.joining(AND)));
    }

    private static void addPagingAndSorting(Pageable page, SolrQuery query, String[] fields) {
        query.setStart(page.getPageSize() * page.getPageNumber());
        query.setRows(page.getPageSize());
        query.setFields(fields);
        query.setSort(MODIFIED, SolrQuery.ORDER.desc);
    }

    public static void addDateRangeToSimpleFields(SolrSearchRequest filter, List<SimpleField> simpleFields) {
        DateRangeField dateRangeField = filter.dateRangeField();
        LOGGER.debug("create solr date range query for range fields request {}", dateRangeField);
        if (dateRangeField.fromDate() > 0 || dateRangeField.toDate() > 0) {
            String fromDate = getSolrDate(dateRangeField.fromDate());
            String toDate = getSolrDate(dateRangeField.toDate());
            String query = START_BRACKET.concat(fromDate).concat(RANGE_SIGN).concat(toDate).concat(END_BRACKET);
            simpleFields.add(new SimpleField(dateRangeField.fieldName(), query));
        }
    }

    private static String getSolrDate(long epochMilliSeconds) {
        return epochMilliSeconds == 0
                ? DEFAULT_SOLR_DATE
                : ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilliSeconds), ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    private static String getSolrFilterQuery(OptionField solrFilter) {
        return solrFilter.options()
                .stream()
                .map(option -> solrFilter.name()
                        .concat(COLON)
                        .concat(QUOTES)
                        .concat(option.name())
                        .concat(QUOTES))
                .collect(Collectors.joining(OR));
    }

    public static String createSolrFilterIdQuery(String id, String[] fields) {
        return Arrays.stream(fields)
                .map(field -> field
                        .concat(COLON)
                        .concat(id))
                .collect(Collectors.joining(OR));
    }

    public static SolrQuery createFilterSolrQuery(SolrSearchRequest filter, String[] fields) {
        SolrQuery query = mapSearchRequestToQuery(filter);
        query.setFields(fields);
        return query;
    }
}

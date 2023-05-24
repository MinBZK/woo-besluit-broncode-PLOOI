package nl.overheid.koop.plooi.search.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import nl.overheid.koop.plooi.search.model.SearchFilterMutatiedatumtijd;
import nl.overheid.koop.plooi.search.model.SearchFilterOpenbaarmakingsdatum;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;

public class SolrQueryUtil {

    private static final String OR = " OR ";
    private static final String TO = " TO ";
    private static final String PARENTHESES_OPEN = "(";
    private static final String PARENTHESES_CLOSE = ")";
    private static final String QUOTE = "\"";
    private static final String COLON = ":";
    private static final String FILTER_QUERY_FORMAT = "%s" + COLON + PARENTHESES_OPEN + "%s" + PARENTHESES_CLOSE;
    private static final String ZICHTBAARHEID_QUERY = "(*:* -zichtbaarheidsdatumtijd:[* TO *]) OR (zichtbaarheidsdatumtijd:[* TO NOW])";

    public static String prepareFilterQuery(String field, String value) {
        return StringUtils.isEmpty(value) == true ? null : field + ":" + QUOTE + ClientUtils.escapeQueryChars(value) + QUOTE;
    }

    public static String prepareIDFilterQuery(String value) {
        return StringUtils.isEmpty(value) ? null
                : String.format("(id:\"%s\" OR id:\"%s_1\" OR dcn_id:\"%s\")", ClientUtils.escapeQueryChars(value), ClientUtils.escapeQueryChars(value),
                        ClientUtils.escapeQueryChars(value));
    }

    public static String prepareFilterQuery(String field, List<String> values) {
        if (values.isEmpty()) {
            return null;
        }

        if (values.size() == 1) {
            return prepareFilterQuery(field, values.get(0));
        }

        String filterValues = values
                .stream()
                .map(v -> QUOTE.concat(ClientUtils.escapeQueryChars(v)).concat(QUOTE))
                .collect(Collectors.joining(OR));

        return String.format(FILTER_QUERY_FORMAT, field, filterValues);
    }

    public static String prepareDateFilterQuery(String field, Object dateObject) {
        if (dateObject == null) {
            return null;
        }

        String van = "*";
        String tot = "*";
        if (dateObject instanceof SearchFilterMutatiedatumtijd) {
            SearchFilterMutatiedatumtijd mutatiedatumtijd = (SearchFilterMutatiedatumtijd) dateObject;
            OffsetDateTime mutatiedatumtijdVan = mutatiedatumtijd.getVan();
            OffsetDateTime mutatiedatumtijdTot = mutatiedatumtijd.getTot();

            if (mutatiedatumtijdVan == null && mutatiedatumtijdTot == null) {
                return null;
            }

            if (mutatiedatumtijdVan != null) {
                van = mutatiedatumtijdVan.format(DateTimeFormatter.ISO_INSTANT);
            }

            if (mutatiedatumtijdTot != null) {
                tot = mutatiedatumtijdTot.format(DateTimeFormatter.ISO_INSTANT);
            }
        } else if (dateObject instanceof SearchFilterOpenbaarmakingsdatum) {
            SearchFilterOpenbaarmakingsdatum openbaarmakingsdatum = (SearchFilterOpenbaarmakingsdatum) dateObject;
            LocalDate openbaarmakingsdatumVan = openbaarmakingsdatum.getVan();
            LocalDate openbaarmakingsdatumTot = openbaarmakingsdatum.getTot();

            if (openbaarmakingsdatumVan == null && openbaarmakingsdatumTot == null) {
                return null;
            }

            if (openbaarmakingsdatumVan != null) {
                van = checkAndUpdateDateFormat(openbaarmakingsdatumVan);
            }

            if (openbaarmakingsdatumTot != null) {
                tot = checkAndUpdateDateFormat(openbaarmakingsdatumTot);
            }
        }

        if (van == "*" && tot == "*") {
            return null;
        }

        return String.format("%s:[%s" + TO + "%s]", field, van, tot);
    }

    private static String checkAndUpdateDateFormat(LocalDate omdTot) {
        return omdTot.toString().contains("T") == false ? omdTot.toString().concat("T00:00:00.000Z") : omdTot.toString();
    }

    /*
     * TODO: '*' should not be escaped so the code duplicated and '*' removed to work with search-term
     */
    public static String customEscapeQueryChars(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
                    || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}'
                    || c == '~' || c == '?' || c == '|' || c == '&' || c == ';' || c == '/'
                    || Character.isWhitespace(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String prepareZichtbaarheidfilter() {
        return ZICHTBAARHEID_QUERY;
    }

}

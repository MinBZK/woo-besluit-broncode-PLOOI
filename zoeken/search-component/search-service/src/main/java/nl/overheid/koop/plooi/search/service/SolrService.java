package nl.overheid.koop.plooi.search.service;

import java.io.IOException;
import java.util.List;
import nl.overheid.koop.plooi.search.model.SearchFilter;
import nl.overheid.koop.plooi.search.model.SearchRequest;
import nl.overheid.koop.plooi.search.service.error.ResponseErrorHelper;
import nl.overheid.koop.plooi.search.service.error.SolrQueryException;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SolrService {

//    TODO: When 'plooijson' is there, that's the only field we need to retrieve
//    public static final String FIELD_LIST = "plooijson";
    public static final String FIELD_LIST = """
            identifier,dcn_id,id,external_id,title,informatiecategorie,informatiecategorie_id,\
            aanbieder,source_label,publisher,publisher_id,verantwoordelijke,verantwoordelijke_id,\
            topthema,topthema_id,available_start,zichtbaarheidsdatumtijd,publicatiestatus,timestamp\
            """;
    public static final String QUERY_FIELDS = "title^5.0 title_stemmed title_ngram description^4.0 description_stemmed description_ngram metadata^3.0 metadata_stemmed metadata_ngram text text_stemmed text_ngram";

    private static final List<String> VALID_SORT_ITEMS = List.of("timestamp", "wijzigingsdatum", "openbaarmakingsdatum");
    private static final List<String> VALID_SORT_ORDERS = List.of("asc", "desc");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String solrCollection;
    private final SolrClient solrClient;

    public SolrService(SolrClient solrClient, @Value("${solr.collection:plooi}") String solrCollection) {
        this.solrClient = solrClient;
        this.solrCollection = solrCollection;
    }

    public SolrDocumentList getSearchResults(SearchRequest searchRequest) {
        SolrQuery sq = new SolrQuery();

        ModifiableSolrParams queryParamMap = new ModifiableSolrParams();
        queryParamMap.add("q", "{!edismax qf=$queryFields v=$userQuery}");
        queryParamMap.add("fl", FIELD_LIST);
        queryParamMap.add("queryFields", QUERY_FIELDS);
        queryParamMap.add("userQuery",
                StringUtils.isEmpty(searchRequest.getZoektekst()) == true ? "*" : SolrQueryUtil.customEscapeQueryChars(searchRequest.getZoektekst()));
        if (searchRequest.getFilters() != null) {
            handleFilterQueries(searchRequest.getFilters(), searchRequest.getToonNietGepubliceerd()).stream()
                    .forEach(e -> queryParamMap.add("fq", e));
        }
        queryParamMap.add("start", searchRequest.getStart() == null ? "0" : ClientUtils.escapeQueryChars(searchRequest.getStart().toString()));
        queryParamMap.add("rows", handleAantal(searchRequest.getAantal()));

        String sorteer = searchRequest.getSorteer();
        if (!StringUtils.isEmpty(sorteer) && !sorteer.equals("relevantie")) {
            String item = sorteer.split(" ")[0];
            String order = sorteer.split(" ")[1];

            // TODO: "wijzigingsdatum" mapped to "timestamp" because it's not in solr index
            item = item.equals("wijzigingsdatum") ? "timestamp" : item;

            if (!VALID_SORT_ITEMS.contains(item)) {
                throw ResponseErrorHelper.inputValueError("Invalid sort type", item, "Valid sort types (default: 'relevantie'): " + VALID_SORT_ITEMS);
            } else if (!VALID_SORT_ORDERS.contains(order)) {
                throw ResponseErrorHelper.inputValueError("Invalid sort order", order, "Valid orders: " + VALID_SORT_ORDERS);
            }

            queryParamMap.add("sort", String.format("%s %s", item, order));
        }
        sq.add(queryParamMap);

        SolrDocumentList solrDocumentList = queryDocuments(sq);

        return solrDocumentList;
    }

    private String handleAantal(Long aantal) {
        if (aantal == null) {
            return "20";
        } else if (aantal > 1000) {
            return "1000";
        }

        return ClientUtils.escapeQueryChars(aantal.toString());
    }

    private List<String> handleFilterQueries(SearchFilter filters, Boolean toonNietGepubliceerd) {
        if (filters == null) {
            return null;
        }

        toonNietGepubliceerd = toonNietGepubliceerd == null ? false : toonNietGepubliceerd;

        List<String> documentsoorten = filters.getClassificatiecollectie() == null ? null : filters.getClassificatiecollectie().getDocumentsoorten();
        List<String> themas = filters.getClassificatiecollectie() == null ? null : filters.getClassificatiecollectie().getThemas();
//        TODO: onderwerpRijksoverheid.nl is nog niet aanwezig in het model
//        List<String> onderwerpRijksoverheidNl = classificatiecollectie == null ? null : classificatiecollectie.getOnderwerpRijksoverheidNl();

        String sourceLabel = filters.getPlooiIntern() == null ? null : filters.getPlooiIntern().getSourceLabel();
        String aanbieder = filters.getPlooiIntern() == null ? null : filters.getPlooiIntern().getAanbieder();
        String extId = filters.getPlooiIntern() == null ? null : filters.getPlooiIntern().getExtId();

        FilterQueryListBuilder filterQueryListBuilder = new FilterQueryListBuilder()
                .add(Filter.Type.MULTI_STRING_FILTER, "verantwoordelijke_id", filters.getVerantwoordelijke())
                .add(Filter.Type.MULTI_STRING_FILTER, "publisher_id", filters.getPublisher())
                .add(Filter.Type.MULTI_STRING_FILTER, "informatiecategorie_id", documentsoorten)
                .add(Filter.Type.MULTI_STRING_FILTER, "topthema_id", themas)
//                TODO: will be implemented later
//                .add(Filter.Type.MULTI_STRING_FILTER, "some_id", onderwerpRijksoverheidNl)
                .add(Filter.Type.DATE_FILTER, "modified", filters.getMutatiedatumtijd())
                .add(Filter.Type.DATE_FILTER, "openbaarmakingsdatum", filters.getOpenbaarmakingsdatum())
                .add(Filter.Type.ID_STRING_FILTER, "id", filters.getId())
                .add(Filter.Type.SINGLE_STRING_FILTER, "source_label", sourceLabel)
                .add(Filter.Type.SINGLE_STRING_FILTER, "aanbieder", aanbieder)
                .add(Filter.Type.SINGLE_STRING_FILTER, "external_id", extId);

        filterQueryListBuilder = !toonNietGepubliceerd ? filterQueryListBuilder.addZichtbaarHeidsDatumTijdFilter() : filterQueryListBuilder;

        return filterQueryListBuilder.buildFilterQueries();
    }

    private SolrDocumentList queryDocuments(SolrQuery solrQuery) {
        try {
            this.logger.debug("Search Solr documents with query {}", solrQuery);

            QueryResponse queryResponse = this.solrClient.query(this.solrCollection, solrQuery);

            return queryResponse.getResults();
        } catch (SolrServerException e) {
            throw new SolrQueryException(e);
        } catch (IOException e) {
            throw new SolrQueryException(e);
        }
    }

}

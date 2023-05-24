package nl.overheid.koop.plooi.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import nl.overheid.koop.plooi.search.model.Classificatiecollectie;
import nl.overheid.koop.plooi.search.model.Document;
import nl.overheid.koop.plooi.search.model.IdentifiedResource;
import nl.overheid.koop.plooi.search.model.Plooi;
import nl.overheid.koop.plooi.search.model.PlooiIntern;
import nl.overheid.koop.plooi.search.model.SearchRequest;
import nl.overheid.koop.plooi.search.model.SearchResponse;
import nl.overheid.koop.plooi.search.model.Titelcollectie;
import nl.overheid.koop.plooi.search.model.Versie;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    // TODO: will deal it with later when plooijson is ready
    @SuppressWarnings("unused")
    private final ObjectMapper objectMapper;
    private final SolrService solrService;

    public SearchService(ObjectMapper objectMapper, SolrService solrService) {
        this.objectMapper = objectMapper;
        this.solrService = solrService;
    }

    public SearchResponse handleSearchResponse(SearchRequest searchRequest) {
//      TODO: authentication is not ready for toonNietGepubliceerd
//        boolean toonNietGepubliceerd = searchRequest.getToonNietGepubliceerd() != null && searchRequest.getToonNietGepubliceerd() ? true : false;

        SolrDocumentList solrDocumentList = this.solrService.getSearchResults(searchRequest);

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setAantal(Long.valueOf(solrDocumentList.size()));
        searchResponse.setStart(solrDocumentList.getStart());
        searchResponse.setTotaal(solrDocumentList.getNumFound());

        List<Plooi> plooiResults = new ArrayList<>();
        for (SolrDocument solrDocument : solrDocumentList) {
//            TODO use plooijson when it's all there in te Solr index, and remove the construction of the Plooi document from individual Solr fields
//            String plooiJson = (String) solrDocument.get("plooijson");
//            Plooi plooi = this.objectMapper.readValue(plooiJson, Plooi.class);

            Plooi plooiDoc = null;
            String zichtbaarheidsdatumtijdStr = getStringValueFromSolrField(solrDocument, "zichtbaarheidsdatumtijd");

            // TODO: toonNietGepubliceerd was also in the AND chain but removed. Later can be placed again due to the IAM
            // integration. Here we check based on zichtbaarheidsdatumtijd
            if (isDateFuture(zichtbaarheidsdatumtijdStr)) {
                plooiDoc = new Plooi()
                        .document(new Document()
                                .pid(getStringValueFromSolrField(solrDocument, "dcn_id")))
                        .plooiIntern(new PlooiIntern()
                                .dcnId(getStringValueFromSolrField(solrDocument, "dcn_id"))
                                .publicatiestatus("latere-publicatie"))
                        .versies(List.of(new Versie()
                                .zichtbaarheidsdatumtijd(toOffsetDateTime(zichtbaarheidsdatumtijdStr))));
            } else {
                plooiDoc = new Plooi()
                        .document(new Document()
                                .titelcollectie(new Titelcollectie().officieleTitel(getStringValueFromSolrField(solrDocument, "title")))
                                .pid(getStringValueFromSolrField(solrDocument, "dcn_id"))
                                .creatiedatum(getStringValueFromSolrField(solrDocument, "available_start"))
                                .verantwoordelijke(new IdentifiedResource()
                                        .type("overheid:AndereOrganisatie")
                                        .label(getStringValueFromSolrField(solrDocument, "verantwoordelijke"))
                                        .id(getStringValueFromSolrField(solrDocument, "verantwoordelijke_id")))
                                .publisher(new IdentifiedResource()
                                        .type("overheid:AndereOrganisatie")
                                        .label(getStringValueFromSolrField(solrDocument, "publisher"))
                                        .id(getStringValueFromSolrField(solrDocument, "publisher_id")))
                                .classificatiecollectie(new Classificatiecollectie()
                                        .documentsoorten(Arrays.asList(new IdentifiedResource()
                                                .type("overheid:Informatietype")
                                                .label(getStringValueFromSolrField(solrDocument, "informatiecategorie"))
                                                .id(getStringValueFromSolrField(solrDocument, "informatiecategorie_id"))))))
                        .plooiIntern(new PlooiIntern()
                                .dcnId(getStringValueFromSolrField(solrDocument, "dcn_id"))
                                .publicatiestatus("gepubliceerd")
                                .extId(Arrays.asList(getStringValueFromSolrField(solrDocument, "external_id")))
                                .sourceLabel(getStringValueFromSolrField(solrDocument, "source_label"))
                                .aanbieder(getStringValueFromSolrField(solrDocument, "aanbieder")));
            }

            // Left out versies en themas for now
            plooiResults.add(plooiDoc);
        }

        searchResponse.setResultaten(plooiResults);

        return searchResponse;
    }

    private String getStringValueFromSolrField(SolrDocument solrDocument, String fieldName) {
        if (!solrDocument.containsKey(fieldName)) {
            return null;
        }

        Object field = solrDocument.get(fieldName);
        if (field instanceof String) {
            return (String) field;
        }
        if (field instanceof ArrayList<?>) {
            // Hack: for array values, we just get the first one (used for informatiecategorie etc)
            return ((ArrayList<?>) field).get(0).toString();
        }

        if (field instanceof Date) {
            return ((Date) field).toInstant().toString();
        }

        return null;
    }

    private static boolean isDateFuture(final String dateString) {
        if (StringUtils.isEmpty(dateString)) {
            return false;
        }

        return toOffsetDateTime(dateString).isAfter(OffsetDateTime.now());
    }

    private static OffsetDateTime toOffsetDateTime(String dateStr) {
        ZoneId zoneId = ZoneId.of("UTC"); // Or another geographic: Europe/Paris

        LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        ZoneOffset offset = zoneId.getRules().getOffset(dateTime);
        OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, offset);

        return offsetDateTime;
    }

}

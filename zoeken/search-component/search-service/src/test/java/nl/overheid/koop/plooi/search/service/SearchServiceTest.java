package nl.overheid.koop.plooi.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import nl.overheid.koop.plooi.search.model.SearchFilter;
import nl.overheid.koop.plooi.search.model.SearchFilterClassificatiecollectie;
import nl.overheid.koop.plooi.search.model.SearchFilterMutatiedatumtijd;
import nl.overheid.koop.plooi.search.model.SearchFilterOpenbaarmakingsdatum;
import nl.overheid.koop.plooi.search.model.SearchFilterPlooiIntern;
import nl.overheid.koop.plooi.search.model.SearchRequest;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;

@SpringBootTest
@MockBeans(value = {
        @MockBean(SolrClient.class)
})
public class SearchServiceTest {

    private static final String QUERY = "{!edismax qf=$queryFields v=$userQuery}";

    private static final String QUERY_PARAMS = "title^5.0 title_stemmed title_ngram description^4.0 description_stemmed description_ngram metadata^3.0 metadata_stemmed metadata_ngram text text_stemmed text_ngram";

    private static final String FIELD_LIST = "identifier,dcn_id,id,external_id,title,informatiecategorie,informatiecategorie_id,aanbieder,source_label,publisher,publisher_id,verantwoordelijke,verantwoordelijke_id,topthema,topthema_id,available_start,zichtbaarheidsdatumtijd,publicatiestatus,timestamp";

    @Autowired
    private SolrClient solrClient;

    @Autowired
    @InjectMocks
    private SearchService searchService;

    @Captor
    ArgumentCaptor<SolrQuery> solrQueryCaptor;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @BeforeEach
    void setup() throws SolrServerException, IOException {
        QueryResponse emptyResponse = new QueryResponse();
        NamedList mockedResponse = new NamedList();
        mockedResponse.add("facet_counts", new NamedList());
        mockedResponse.add("response", new SolrDocumentList());
        emptyResponse.setResponse(mockedResponse);

        when(this.solrClient.query(anyString(), any(SolrQuery.class))).thenReturn(emptyResponse);

    }

    @Test
    void emptyQueryShouldSucceed() throws SolrServerException, IOException {
        this.searchService.handleSearchResponse(new SearchRequest()
                .filters(new SearchFilter()));

        Mockito.verify(this.solrClient).query(any(), this.solrQueryCaptor.capture());
        SolrQuery capturedSolrQuery = this.solrQueryCaptor.getValue();

        // fixed query params
        assertEquals(QUERY, capturedSolrQuery.get("q"));
        assertEquals(FIELD_LIST, capturedSolrQuery.get("fl"));
        assertEquals(QUERY_PARAMS, capturedSolrQuery.get("queryFields"));
        assertEquals("*", capturedSolrQuery.get("userQuery"));
        // pagination
        assertEquals("0", capturedSolrQuery.get("start"));
        assertEquals("20", capturedSolrQuery.get("rows"));
        // sort
        assertNull(capturedSolrQuery.get("sort"));
    }

    /*
     * No filter tests
     */
    @Test
    void emptyFilterWithSorteerRelevanceShouldSucceed() throws SolrServerException, IOException {
        this.searchService.handleSearchResponse(new SearchRequest()
                .zoektekst("*")
                .sorteer("relevantie")
                .toonNietGepubliceerd(false)
                .start(10L)
                .aantal(25L)
                .filters(new SearchFilter()));

        Mockito.verify(this.solrClient).query(any(), this.solrQueryCaptor.capture());

        Mockito.verify(this.solrClient).query(any(), this.solrQueryCaptor.capture());
        SolrQuery capturedSolrQuery = this.solrQueryCaptor.getValue();

        // fixed query params
        assertEquals(QUERY, capturedSolrQuery.get("q"));
        assertEquals(FIELD_LIST, capturedSolrQuery.get("fl"));
        assertEquals(QUERY_PARAMS, capturedSolrQuery.get("queryFields"));
        assertEquals("*", capturedSolrQuery.get("userQuery"));
        // pagination
        assertEquals("10", capturedSolrQuery.get("start"));
        assertEquals("25", capturedSolrQuery.get("rows"));
        // sort
        assertNull(capturedSolrQuery.get("sort"));
    }

    @Test
    void emptyFilterWithActualSorteerParametersShouldSucceed() throws SolrServerException, IOException {
        this.searchService.handleSearchResponse(new SearchRequest()
                .zoektekst("*")
                .sorteer("wijzigingsdatum desc")
                .toonNietGepubliceerd(false)
                .start(10L)
                .aantal(25L)
                .filters(new SearchFilter()));

        Mockito.verify(this.solrClient).query(any(), this.solrQueryCaptor.capture());
        SolrQuery capturedSolrQuery = this.solrQueryCaptor.getValue();

        // fixed query params
        assertEquals(QUERY, capturedSolrQuery.get("q"));
        assertEquals(FIELD_LIST, capturedSolrQuery.get("fl"));
        assertEquals(QUERY_PARAMS, capturedSolrQuery.get("queryFields"));
        assertEquals("*", capturedSolrQuery.get("userQuery"));
        // pagination
        assertEquals("10", capturedSolrQuery.get("start"));
        assertEquals("25", capturedSolrQuery.get("rows"));
        // sort
        // TODO: "wijzigingsdatum" mapped to "timestamp" because it's not in solr index
        assertEquals("timestamp desc", capturedSolrQuery.get("sort"));
    }

    /*
     * Filter tests
     */
    @Test
    void filtersWithSingleEntryShouldSucceed() throws SolrServerException, IOException {
        this.searchService.handleSearchResponse(new SearchRequest()
                .zoektekst("*")
                .sorteer("openbaarmakingsdatum asc")
                .toonNietGepubliceerd(false)
                .start(10L)
                .aantal(25L)
                .filters(new SearchFilter()
                        .verantwoordelijke(List.of("verantwoordelijkeValue1"))
                        .publisher(List.of("publisherValue1"))
                        .classificatiecollectie(new SearchFilterClassificatiecollectie()
                                .addDocumentsoortenItem("documentSoortValue1")
                                .addThemasItem("themaValue1"))
                        .mutatiedatumtijd(new SearchFilterMutatiedatumtijd()
                                .van(convertToOffsetDateTime("2022-12-01T00:00:00.000Z")))
                        .openbaarmakingsdatum(new SearchFilterOpenbaarmakingsdatum()
                                .van(convertToLocalDate("2022-01-01")))
                        .id("idValue")
                        .plooiIntern(new SearchFilterPlooiIntern()
                                .sourceLabel("sourceLabelValue")
                                .aanbieder("aanbiederValue")
                                .extId("extIdValue"))));

        Mockito.verify(this.solrClient).query(any(), this.solrQueryCaptor.capture());
        SolrQuery capturedSolrQuery = this.solrQueryCaptor.getValue();

        // fixed query params
        assertEquals(QUERY, capturedSolrQuery.get("q"));
        assertEquals(FIELD_LIST, capturedSolrQuery.get("fl"));
        assertEquals(QUERY_PARAMS, capturedSolrQuery.get("queryFields"));
        assertEquals("*", capturedSolrQuery.get("userQuery"));
        // filter queries
        String[] filterQueries = capturedSolrQuery.getFilterQueries();

        assertEquals("verantwoordelijke_id:\"verantwoordelijkeValue1\"", filterQueries[0]);
        assertEquals("publisher_id:\"publisherValue1\"", filterQueries[1]);
        assertEquals("informatiecategorie_id:\"documentSoortValue1\"", filterQueries[2]);
        assertEquals("topthema_id:\"themaValue1\"", filterQueries[3]);
        assertEquals("modified:[2022-12-01T00:00:00Z TO *]", filterQueries[4]);
        assertEquals("openbaarmakingsdatum:[2022-01-01T00:00:00.000Z TO *]", filterQueries[5]);
        assertEquals("(id:\"idValue\" OR id:\"idValue_1\" OR dcn_id:\"idValue\")", filterQueries[6]);
        assertEquals("source_label:\"sourceLabelValue\"", filterQueries[7]);
        assertEquals("aanbieder:\"aanbiederValue\"", filterQueries[8]);
        assertEquals("external_id:\"extIdValue\"", filterQueries[9]);
        // pagination
        assertEquals("10", capturedSolrQuery.get("start"));
        assertEquals("25", capturedSolrQuery.get("rows"));
        // sort
        assertEquals("openbaarmakingsdatum asc", capturedSolrQuery.get("sort"));
    }

    @Test
    void filtersWithFullEntriesShouldSucceed() throws SolrServerException, IOException {
        this.searchService.handleSearchResponse(new SearchRequest()
                .zoektekst("*")
                .sorteer("relevantie")
                .toonNietGepubliceerd(false)
                .start(10L)
                .aantal(25L)
                .filters(new SearchFilter()
                        .verantwoordelijke(List.of("verantwoordelijkeValue1", "verantwoordelijkeValue2"))
                        .publisher(List.of("publisherValue1", "publisherValue2"))
                        .classificatiecollectie(new SearchFilterClassificatiecollectie()
                                .addDocumentsoortenItem("documentSoortValue1")
                                .addDocumentsoortenItem("documentSoortValue2")
                                .addThemasItem("themaValue1")
                                .addThemasItem("themaValue2"))
                        .mutatiedatumtijd(new SearchFilterMutatiedatumtijd()
                                .van(convertToOffsetDateTime("2022-12-01T00:00:00.000Z"))
                                .tot(convertToOffsetDateTime("2022-12-24T00:00:00.000Z")))
                        .openbaarmakingsdatum(new SearchFilterOpenbaarmakingsdatum()
                                .van(convertToLocalDate("2022-01-01"))
                                .tot(convertToLocalDate("2022-01-24")))
                        .id("idValue")
                        .plooiIntern(new SearchFilterPlooiIntern()
                                .sourceLabel("sourceLabelValue")
                                .aanbieder("aanbiederValue")
                                .extId("extIdValue"))));

        Mockito.verify(this.solrClient).query(any(), this.solrQueryCaptor.capture());
        SolrQuery capturedSolrQuery = this.solrQueryCaptor.getValue();

        // fixed query params
        assertEquals(QUERY, capturedSolrQuery.get("q"));
        assertEquals(FIELD_LIST, capturedSolrQuery.get("fl"));
        assertEquals(QUERY_PARAMS, capturedSolrQuery.get("queryFields"));
        assertEquals("*", capturedSolrQuery.get("userQuery"));
        // filter queries
        String[] filterQueries = capturedSolrQuery.getFilterQueries();

        assertEquals("verantwoordelijke_id:(\"verantwoordelijkeValue1\" OR \"verantwoordelijkeValue2\")", filterQueries[0]);
        assertEquals("publisher_id:(\"publisherValue1\" OR \"publisherValue2\")", filterQueries[1]);
        assertEquals("informatiecategorie_id:(\"documentSoortValue1\" OR \"documentSoortValue2\")", filterQueries[2]);
        assertEquals("topthema_id:(\"themaValue1\" OR \"themaValue2\")", filterQueries[3]);
        assertEquals("modified:[2022-12-01T00:00:00Z TO 2022-12-24T00:00:00Z]", filterQueries[4]);
        assertEquals("openbaarmakingsdatum:[2022-01-01T00:00:00.000Z TO 2022-01-24T00:00:00.000Z]", filterQueries[5]);
        assertEquals("(id:\"idValue\" OR id:\"idValue_1\" OR dcn_id:\"idValue\")", filterQueries[6]);
        assertEquals("source_label:\"sourceLabelValue\"", filterQueries[7]);
        assertEquals("aanbieder:\"aanbiederValue\"", filterQueries[8]);
        assertEquals("external_id:\"extIdValue\"", filterQueries[9]);
        // pagination
        assertEquals("10", capturedSolrQuery.get("start"));
        assertEquals("25", capturedSolrQuery.get("rows"));
        // sort
        assertNull(capturedSolrQuery.get("sort"));
    }

    @Test
    void filtersWithFullEntriesWithEscapeCharactersShouldSucceed() throws SolrServerException, IOException {
        this.searchService.handleSearchResponse(new SearchRequest()
                .zoektekst("*")
                .sorteer("relevantie")
                .toonNietGepubliceerd(false)
                .start(10L)
                .aantal(25L)
                .filters(new SearchFilter()
                        .verantwoordelijke(List.of("verantwoordelijkeValue1", "verantwoordelijke\"Value2"))
                        .publisher(List.of("publisherValue1", "publisher\"Value2"))
                        .classificatiecollectie(new SearchFilterClassificatiecollectie()
                                .addDocumentsoortenItem("documentSoortValue1")
                                .addDocumentsoortenItem("documentSoort\"Value2")
                                .addThemasItem("themaValue1")
                                .addThemasItem("thema\"Value2"))
                        .mutatiedatumtijd(new SearchFilterMutatiedatumtijd()
                                .van(convertToOffsetDateTime("2022-12-01T00:00:00.000Z"))
                                .tot(convertToOffsetDateTime("2022-12-24T00:00:00.000Z")))
                        .openbaarmakingsdatum(new SearchFilterOpenbaarmakingsdatum()
                                .van(convertToLocalDate("2022-01-01"))
                                .tot(convertToLocalDate("2022-01-24")))
                        .id("id\"Value")
                        .plooiIntern(new SearchFilterPlooiIntern()
                                .sourceLabel("sourceLabel\"Value")
                                .aanbieder("aanbieder\"Value")
                                .extId("extId\"Value"))));

        Mockito.verify(this.solrClient).query(any(), this.solrQueryCaptor.capture());
        SolrQuery capturedSolrQuery = this.solrQueryCaptor.getValue();

        // fixed query params
        assertEquals(QUERY, capturedSolrQuery.get("q"));
        assertEquals(FIELD_LIST, capturedSolrQuery.get("fl"));
        assertEquals(QUERY_PARAMS, capturedSolrQuery.get("queryFields"));
        assertEquals("*", capturedSolrQuery.get("userQuery"));
        // filter queries
        String[] filterQueries = capturedSolrQuery.getFilterQueries();

        assertEquals("verantwoordelijke_id:(\"verantwoordelijkeValue1\" OR \"verantwoordelijke\\\"Value2\")", filterQueries[0]);
        assertEquals("publisher_id:(\"publisherValue1\" OR \"publisher\\\"Value2\")", filterQueries[1]);
        assertEquals("informatiecategorie_id:(\"documentSoortValue1\" OR \"documentSoort\\\"Value2\")", filterQueries[2]);
        assertEquals("topthema_id:(\"themaValue1\" OR \"thema\\\"Value2\")", filterQueries[3]);
        assertEquals("modified:[2022-12-01T00:00:00Z TO 2022-12-24T00:00:00Z]", filterQueries[4]);
        assertEquals("openbaarmakingsdatum:[2022-01-01T00:00:00.000Z TO 2022-01-24T00:00:00.000Z]", filterQueries[5]);
        assertEquals("(id:\"id\\\"Value\" OR id:\"id\\\"Value_1\" OR dcn_id:\"id\\\"Value\")", filterQueries[6]);
        assertEquals("source_label:\"sourceLabel\\\"Value\"", filterQueries[7]);
        assertEquals("aanbieder:\"aanbieder\\\"Value\"", filterQueries[8]);
        assertEquals("external_id:\"extId\\\"Value\"", filterQueries[9]);
        // pagination
        assertEquals("10", capturedSolrQuery.get("start"));
        assertEquals("25", capturedSolrQuery.get("rows"));
        // sort
        assertNull(capturedSolrQuery.get("sort"));
    }

    /*
     * Bug fixes
     */
    @Test
    void aantalShouldBeLimitedTo1000() throws SolrServerException, IOException {
        this.searchService.handleSearchResponse(new SearchRequest()
                .aantal(Long.valueOf(1001))
                .filters(new SearchFilter()));

        Mockito.verify(this.solrClient).query(any(), this.solrQueryCaptor.capture());
        SolrQuery capturedSolrQuery = this.solrQueryCaptor.getValue();

        // fixed query params
        assertEquals(QUERY, capturedSolrQuery.get("q"));
        assertEquals(FIELD_LIST, capturedSolrQuery.get("fl"));
        assertEquals(QUERY_PARAMS, capturedSolrQuery.get("queryFields"));
        assertEquals("*", capturedSolrQuery.get("userQuery"));
        // pagination
        assertEquals("0", capturedSolrQuery.get("start"));
        assertEquals("1000", capturedSolrQuery.get("rows"));
        // sort
        assertNull(capturedSolrQuery.get("sort"));
    }

    private static OffsetDateTime convertToOffsetDateTime(String date) {
        ZoneId zoneId = ZoneId.of("UTC");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

        ZoneOffset offset = zoneId.getRules().getOffset(dateTime);

        return OffsetDateTime.of(dateTime, offset);
    }

    private static LocalDate convertToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return LocalDate.parse(date, formatter);
    }

}

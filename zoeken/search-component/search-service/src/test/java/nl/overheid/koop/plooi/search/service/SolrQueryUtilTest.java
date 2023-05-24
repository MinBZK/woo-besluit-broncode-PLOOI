package nl.overheid.koop.plooi.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import nl.overheid.koop.plooi.search.model.SearchFilterMutatiedatumtijd;
import nl.overheid.koop.plooi.search.model.SearchFilterOpenbaarmakingsdatum;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SolrQueryUtilTest {

    private static LocalDate VAN_LOCAL_DATE;
    private static LocalDate TOT_LOCAL_DATE;

    private static LocalDateTime VAN_LOCAL_DATE_TIME;
    private static LocalDateTime TOT_LOCAL_DATE_TIME;

    private static OffsetDateTime VAN_OFFSET_DATE_TIME;
    private static OffsetDateTime TOT_OFFSET_DATE_TIME;

    @BeforeAll
    public static void setup() {
        ZoneId zone = ZoneId.of("Europe/Amsterdam");

        VAN_LOCAL_DATE = LocalDate.of(1982, 1, 24);
        TOT_LOCAL_DATE = LocalDate.of(2023, 1, 24);

        VAN_LOCAL_DATE_TIME = LocalDateTime.of(1982, 1, 24, 18, 36, 42, 99);
        TOT_LOCAL_DATE_TIME = LocalDateTime.of(2023, 1, 24, 18, 36, 42, 99);

        VAN_OFFSET_DATE_TIME = OffsetDateTime.of(VAN_LOCAL_DATE_TIME, zone.getRules().getOffset(VAN_LOCAL_DATE_TIME));
        TOT_OFFSET_DATE_TIME = OffsetDateTime.of(TOT_LOCAL_DATE_TIME, zone.getRules().getOffset(TOT_LOCAL_DATE_TIME));
    }

    /*
     * Methods with String based values
     */
    @Test
    void singleValueQueryShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        String fq = SolrQueryUtil.prepareFilterQuery("field", "someValue");
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:\"someValue\"", solrQuery.toString());
    }

    @Test
    void singleValueWithEsacapedCharactersQueryShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        String fq = SolrQueryUtil.prepareFilterQuery("field", "some\"Value");
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:\"some\\\"Value\"", solrQuery.toString());
    }

    @Test
    void dcnIDQueryShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        String fq = SolrQueryUtil.prepareIDFilterQuery("dcnID");
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=(id:\"dcnID\"+OR+id:\"dcnID_1\"+OR+dcn_id:\"dcnID\")", solrQuery.toString());
    }

    @Test
    void dcnIDQueryShouldWorkWithEscapeCharactersBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        String valueWithQuotesInside = "valueWith\"escapeCharacter";
        String valueWithQuotesInsideEscaped = "valueWith\\\"escapeCharacter";
        String fq = SolrQueryUtil.prepareIDFilterQuery(valueWithQuotesInside);
        solrQuery.addFilterQuery(fq);

        assertEquals(
                "q=*:*&fq=(id:\"" + valueWithQuotesInsideEscaped + "\"+OR+id:\"" + valueWithQuotesInsideEscaped + "_1\"+OR+dcn_id:\""
                        + valueWithQuotesInsideEscaped + "\")",
                solrQuery.toString());
    }

    @Test
    void multipleSingleValueQueriesShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        String fq1 = SolrQueryUtil.prepareFilterQuery("field1", "someValue1");
        String fq2 = SolrQueryUtil.prepareFilterQuery("field2", "someValue2");
        solrQuery.addFilterQuery(fq1);
        solrQuery.addFilterQuery(fq2);

        assertEquals("q=*:*&fq=field1:\"someValue1\"&fq=field2:\"someValue2\"", solrQuery.toString());
    }

    @Test
    void multipleSingleValueQueriesWithEscapeCharactersShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        String fq1 = SolrQueryUtil.prepareFilterQuery("field1", "someValue1");
        String fq2 = SolrQueryUtil.prepareFilterQuery("field2", "some\"Value2");
        solrQuery.addFilterQuery(fq1);
        solrQuery.addFilterQuery(fq2);

        assertEquals("q=*:*&fq=field1:\"someValue1\"&fq=field2:\"some\\\"Value2\"", solrQuery.toString());
    }

    @Test
    void multiValuedListQueryShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        List<String> values = List.of("value1", "value2", "value3");
        String fq = SolrQueryUtil.prepareFilterQuery("field", values);
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:(\"value1\"+OR+\"value2\"+OR+\"value3\")", solrQuery.toString());
    }

    @Test
    void multiValuedListQueryWithEscapeCharactersShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        List<String> values = List.of("value1", "val\"ue2", "value3");
        String fq = SolrQueryUtil.prepareFilterQuery("field", values);
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:(\"value1\"+OR+\"val\\\"ue2\"+OR+\"value3\")", solrQuery.toString());
    }

    @Test
    void multiValuedListQueryWithOneItemShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        List<String> values = List.of("value1");
        String fq = SolrQueryUtil.prepareFilterQuery("field", values);
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:\"value1\"", solrQuery.toString());
    }

    /*
     * Date-range methods
     */

    /*
     * Date-range: SearchFilterMutatiedatumtijd
     */
    @Test
    void dateFilterQueryWithTotAndVanForMutatieDatumTijdShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        SearchFilterMutatiedatumtijd mutatieDatumTijd = new SearchFilterMutatiedatumtijd();
        mutatieDatumTijd.setVan(VAN_OFFSET_DATE_TIME);
        mutatieDatumTijd.setTot(TOT_OFFSET_DATE_TIME);

        String fq = SolrQueryUtil.prepareDateFilterQuery("field", mutatieDatumTijd);
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:[1982-01-24T17:36:42.000000099Z+TO+2023-01-24T17:36:42.000000099Z]", solrQuery.toString());
    }

    @Test
    void dateFilterQueryWithTotOnlyForMutatieDatumTijdShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        SearchFilterMutatiedatumtijd mutatieDatumTijd = new SearchFilterMutatiedatumtijd();
        mutatieDatumTijd.setVan(VAN_OFFSET_DATE_TIME);
        mutatieDatumTijd.setTot(null);

        String fq = SolrQueryUtil.prepareDateFilterQuery("field", mutatieDatumTijd);
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:[1982-01-24T17:36:42.000000099Z+TO+*]", solrQuery.toString());
    }

    @Test
    void dateFilterQueryWithVanOnlyForMutatieDatumTijdShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        SearchFilterMutatiedatumtijd mutatieDatumTijd = new SearchFilterMutatiedatumtijd();
        mutatieDatumTijd.setVan(null);
        mutatieDatumTijd.setTot(TOT_OFFSET_DATE_TIME);

        String fq = SolrQueryUtil.prepareDateFilterQuery("field", mutatieDatumTijd);
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:[*+TO+2023-01-24T17:36:42.000000099Z]", solrQuery.toString());
    }

    @Test
    void dateFilterQueryWithNullDatesForMutatieDatumTijdShouldReturnNull() {
        SearchFilterMutatiedatumtijd mutatieDatumTijd = new SearchFilterMutatiedatumtijd();
        mutatieDatumTijd.setVan(null);
        mutatieDatumTijd.setTot(null);

        assertNull(SolrQueryUtil.prepareDateFilterQuery("field", mutatieDatumTijd));
    }

    /*
     * Date-range: SearchFilterMutatiedatumtijd
     */
    @Test
    void dateFilterQueryWithTotAndVanForOpenbaarmakingsdatumShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        SearchFilterOpenbaarmakingsdatum openbaarmakingsdatum = new SearchFilterOpenbaarmakingsdatum();
        openbaarmakingsdatum.setVan(VAN_LOCAL_DATE);
        openbaarmakingsdatum.setTot(TOT_LOCAL_DATE);

        String fq = SolrQueryUtil.prepareDateFilterQuery("field", openbaarmakingsdatum);
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:[1982-01-24T00:00:00.000Z+TO+2023-01-24T00:00:00.000Z]", solrQuery.toString());
    }

    @Test
    void dateFilterQueryWithTotOnlyForOpenbaarmakingsdatumShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        SearchFilterOpenbaarmakingsdatum openbaarmakingsdatum = new SearchFilterOpenbaarmakingsdatum();
        openbaarmakingsdatum.setVan(VAN_LOCAL_DATE);
        openbaarmakingsdatum.setTot(null);

        String fq = SolrQueryUtil.prepareDateFilterQuery("field", openbaarmakingsdatum);
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:[1982-01-24T00:00:00.000Z+TO+*]", solrQuery.toString());
    }

    @Test
    void dateFilterQueryWithVanOnlyForOpenbaarmakingsdatumShouldBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        SearchFilterOpenbaarmakingsdatum openbaarmakingsdatum = new SearchFilterOpenbaarmakingsdatum();
        openbaarmakingsdatum.setVan(null);
        openbaarmakingsdatum.setTot(TOT_LOCAL_DATE);

        String fq = SolrQueryUtil.prepareDateFilterQuery("field", openbaarmakingsdatum);
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=field:[*+TO+2023-01-24T00:00:00.000Z]", solrQuery.toString());
    }

    @Test
    void dateFilterQueryWithNullDatesForOpenbaarmakingsdatumShouldReturnNull() {
        SearchFilterOpenbaarmakingsdatum openbaarmakingsdatum = new SearchFilterOpenbaarmakingsdatum();
        openbaarmakingsdatum.setVan(null);
        openbaarmakingsdatum.setTot(null);

        assertNull(SolrQueryUtil.prepareDateFilterQuery("field", openbaarmakingsdatum));
    }

    @Test
    void zichtbaarheidFilterBeGeneratedSuccessfully() {
        SolrQuery solrQuery = new SolrQuery("*:*");

        String fq = SolrQueryUtil.prepareZichtbaarheidfilter();
        solrQuery.addFilterQuery(fq);

        assertEquals("q=*:*&fq=(*:*+-zichtbaarheidsdatumtijd:[*+TO+*])+OR+(zichtbaarheidsdatumtijd:[*+TO+NOW])", solrQuery.toString());
    }

}

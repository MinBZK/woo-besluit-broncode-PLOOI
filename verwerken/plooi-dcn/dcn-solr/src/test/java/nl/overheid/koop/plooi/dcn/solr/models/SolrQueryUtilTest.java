package nl.overheid.koop.plooi.dcn.solr.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class SolrQueryUtilTest {

    private Pageable page = PageRequest.of(0, 20);
    private String[] fields = { "available_start", "creator", "dcn_id", "type", "topthema", "title" };
    private String[] bulkFields = { "id" };

    @Test
    void createSolrQueryFromTextField() {
        SolrQuery query = new SolrQuery();
        SimpleField simpleField = new SimpleField("title", "EU help");
        SolrSearchRequest searchRequest = new SolrSearchRequest(null, List.of(simpleField), null, null);
        SolrQueryUtil.addSimpleFields(searchRequest, query);
        assertEquals("q=title:EU+help", query.toString());
    }

    @Test
    void createSolrQueryFromFilterWitDefaultDateRange() {
        DateRangeField dateRangeField = new DateRangeField("avaliable", 0, 0);
        SolrSearchRequest searchRequest = new SolrSearchRequest(null, null, null, dateRangeField);
        List<SimpleField> simpleFields = new ArrayList<>();
        SolrQueryUtil.addDateRangeToSimpleFields(searchRequest, simpleFields);
        assertTrue(simpleFields.isEmpty());
    }

    @Test
    void createSolrQueryFromFilterWithDateRange() {
        DateRangeField dateRangeField = new DateRangeField("avaliable", 1637452800000L, 1638057600000L);
        SolrSearchRequest searchRequest = new SolrSearchRequest(null, null, null, dateRangeField);
        List<SimpleField> simpleFields = new ArrayList<>();
        SolrQueryUtil.addDateRangeToSimpleFields(searchRequest, simpleFields);
        assertEquals("[2021-11-21T00:00:00Z TO 2021-11-28T00:00:00Z]", simpleFields.get(0).value());
    }

    @Test
    void createSolrQueryFromFilterOptions() {
        SolrQuery query = new SolrQuery();
        OptionField creators = new OptionField("creator", List.of(new Option("Eerste kamer", 0)));
        OptionField types = new OptionField("type", List.of(new Option("besluit", 0)));
        SolrSearchRequest searchRequest = new SolrSearchRequest(null, null, List.of(creators, types), null);
        SolrQueryUtil.addFilterQueries(searchRequest, query);
        assertEquals("fq=creator:\"Eerste+kamer\"+AND+type:\"besluit\"", query.toString());
    }

    @Test
    void createSolrQueryFromDefaultFilter() {
        String expectedQuery = "q=&start=0&rows=20&fl=available_start,creator,dcn_id,type,topthema,title&sort=modified+desc";
        SolrSearchRequest searchRequest = new SolrSearchRequest(null, new ArrayList<>(), new ArrayList<>(), new DateRangeField("date", 0, 0));
        SolrQuery query = SolrQueryUtil.createFilterSolrQuery(searchRequest, this.page, this.fields);
        assertEquals(expectedQuery, query.toString());
    }

    @Test
    void createSolrQueryFromCompleteFilter() {
        String expected = "fq=type:\"besluit\"&q=title:\\:+AND+date:[2021-11-21T00:00:00Z+TO+2021-11-28T00:00:00Z]&start=0&rows=20&fl=available_start,creator,dcn_id,type,topthema,title&sort=modified+desc";
        OptionField types = new OptionField("type", List.of(new Option("besluit", 0)));
        List<SimpleField> simpleFieldList = new ArrayList<>();
        simpleFieldList.add(new SimpleField("title", ":"));
        SolrSearchRequest searchRequest = new SolrSearchRequest(null, simpleFieldList, List.of(types),
                new DateRangeField("date", 1637452800000L, 1638057600000L));
        SolrQuery query = SolrQueryUtil.createFilterSolrQuery(searchRequest, this.page, this.fields);
        assertEquals(expected, query.toString());
    }

    @Test
    void createSolrQueryForBullkAction() {
        String expected = "fq=type:\"besluit\"&q=title:\\:+AND+date:[2021-11-21T00:00:00Z+TO+2021-11-28T00:00:00Z]&fl=id&cursorMark=*&rows=1000&sort=identifier+desc";
        OptionField types = new OptionField("type", List.of(new Option("besluit", 0)));
        List<SimpleField> simpleFieldList = new ArrayList<>();
        simpleFieldList.add(new SimpleField("title", ":"));
        SolrSearchRequest searchRequest = new SolrSearchRequest(null, simpleFieldList,
                List.of(types), new DateRangeField("date", 1637452800000L, 1638057600000L));
        SolrQuery query = SolrQueryUtil.createSolrQueryForBulk(searchRequest, this.bulkFields, "*");
        assertEquals(expected, query.toString());
    }
}

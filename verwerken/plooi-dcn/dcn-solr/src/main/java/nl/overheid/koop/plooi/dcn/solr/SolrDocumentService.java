package nl.overheid.koop.plooi.dcn.solr;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import nl.overheid.koop.plooi.dcn.solr.models.Option;
import nl.overheid.koop.plooi.dcn.solr.models.OptionField;
import nl.overheid.koop.plooi.dcn.solr.models.SolrQueryUtil;
import nl.overheid.koop.plooi.dcn.solr.models.SolrSearchRequest;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SolrDocumentService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String solrUrl;
    private String solrCollection;
    private SolrClient solrClient;

    public SolrDocumentService(@Value("${dcn.solr.url}") String url, @Value("${dcn.solr.collection}") String collection) {
        this.solrUrl = url;
        this.solrCollection = collection;
        this.solrClient = new HttpSolrClient.Builder(this.solrUrl).build();
    }

    public ResponseEntity getDocument(String queryParameters) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.set("q", queryParameters);

        return queryDocuments(solrQuery, null);
    }

    public ResponseEntity getDocuments(SolrSearchRequest filter, Pageable page, String[] fields) {
        SolrQuery solrQuery = SolrQueryUtil.createFilterSolrQuery(filter, page, fields);
        return queryDocuments(solrQuery, page);
    }

    public ResponseEntity getFilterLists() {
        return this.getSolrFilterOptionLists();
    }

    private ResponseEntity queryDocuments(SolrQuery solrQuery, Pageable page) {
        this.logger.debug("Search Solr documents with query {}", solrQuery);
        try {
            SolrDocumentList solrDocuments = this.solrClient.query(this.solrCollection, solrQuery).getResults();
            if (page == null) {
                return new ResponseEntity(solrDocuments, HttpStatus.OK);
            } else {
                return new ResponseEntity(new PageImpl<>(solrDocuments, page, solrDocuments.getNumFound()), HttpStatus.OK);
            }
        } catch (SolrException e) {
            this.logger.debug("SolrException occurred while querying server. ", e);
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(e.code()));
        } catch (SolrServerException e) {
            this.logger.error("SolrServerException occurred while querying server. ", e);
            return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            this.logger.error("IOException occurred while querying server. ", e);
            return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public QueryResponse getPageableDocument(SolrSearchRequest query, String cursorMark, String[] fields) throws SolrServerException, IOException {
        SolrQuery solrQuery = SolrQueryUtil.createSolrQueryForBulk(query, fields, cursorMark);
        try {
            return this.solrClient.query(this.solrCollection, solrQuery);
        } catch (SolrException e) {
            this.logger.debug("SolrException occurred while querying server. ", e);
            return new QueryResponse();
        } catch (SolrServerException e) {
            this.logger.error("SolrServerException occurred while querying server. ", e);
            return new QueryResponse();
        } catch (IOException e) {
            this.logger.error("IOException occurred while querying server. ", e);
            return new QueryResponse();
        }
    }

    public List<String> getDcnIds(String id, String[] fields) {
        String query = SolrQueryUtil.createSolrFilterIdQuery(id, fields);
        SolrSearchRequest searchRequest = new SolrSearchRequest(query, null, null, null);
        SolrQuery solrQuery = SolrQueryUtil.createFilterSolrQuery(searchRequest, fields);
        return getIdsLists(fields[1], solrQuery);
    }

    public Stream<SolrDocument> getIndexedDocument(String id, String[] fields) {
        String query = SolrQueryUtil.createSolrFilterIdQuery(id, fields);
        SolrSearchRequest searchRequest = new SolrSearchRequest(query, null, null, null);
        SolrQuery solrQuery = SolrQueryUtil.createFilterSolrQuery(searchRequest, fields);
        try {
            var response = this.solrClient.query(this.solrCollection, solrQuery);
            return response.getResults().stream();
        } catch (SolrException e) {
            this.logger.debug("SolrException occurred while querying server. ", e);
            return Stream.empty();
        } catch (SolrServerException e) {
            this.logger.error("SolrServerException occurred while querying server. ", e);
            return Stream.empty();
        } catch (IOException e) {
            this.logger.error("IOException occurred while querying server. ", e);
            return Stream.empty();
        }
    }

    private List<String> getIdsLists(String field, SolrQuery solrQuery) {
        var response = queryDocuments(solrQuery, null);
        var solrList = (SolrDocumentList) response.getBody();
        return Objects.requireNonNull(solrList)
                .stream()
                .map(c -> c.getFieldValue(field))
                .map(Object::toString)
                .toList();
    }

    private ResponseEntity getSolrFilterOptionLists() {
        SolrQuery query = new SolrQuery();
        query.setFacet(true);
        query.addFacetField("creator");
        query.addFacetField("type");
        query.setFacetLimit(-1);
        query.set("q", "*:*");

        try {
            List<OptionField> filters = this.solrClient.query(this.solrCollection, query)
                    .getFacetFields()
                    .stream()
                    .map(facetField -> new OptionField(facetField.getName(), facetField.getValues()
                            .stream()
                            .map(count -> new Option(count.getName(), count.getCount()))
                            .sorted(Comparator.comparing(Option::name, String.CASE_INSENSITIVE_ORDER))
                            .toList()))
                    .toList();
            return new ResponseEntity(filters, HttpStatus.OK);
        } catch (SolrException e) {
            this.logger.debug("SolrException occurred while querying server. ", e);
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(e.code()));
        } catch (SolrServerException e) {
            this.logger.error("SolrServerException occurred while querying server. ", e);
            return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            this.logger.error("IOException occurred while querying server. ", e);
            return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

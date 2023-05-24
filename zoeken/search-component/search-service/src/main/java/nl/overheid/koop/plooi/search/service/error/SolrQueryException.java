package nl.overheid.koop.plooi.search.service.error;

import java.io.IOException;
import org.apache.solr.client.solrj.SolrServerException;

public class SolrQueryException extends RuntimeException {

    private static final long serialVersionUID = 216147634048068552L;

    public SolrQueryException(SolrServerException e) {
        super("SolrServerException occurred while querying server.", e.getCause());
    }

    public SolrQueryException(IOException e) {
        super("IOException occurred while querying server.", e.getCause());
    }

}

package nl.overheid.koop.plooi.dcn.solr;

public class SolrDocumentIndexingException extends RuntimeException {

    private static final long serialVersionUID = -6666768848187273486L;

    public SolrDocumentIndexingException(String message, Throwable cause) {
        super(message, cause);
    }
}

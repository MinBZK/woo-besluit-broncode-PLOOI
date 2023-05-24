package nl.overheid.koop.plooi.dcn.solr;

import java.io.IOException;
import java.util.Objects;
import nl.overheid.koop.plooi.dcn.component.types.ObjectProcessing;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Writes batches of {@link SolrInputDocument}s to Solr, using {@link ConcurrentUpdateSolrClient}. */
@Service
public class SolrDocumentIndexing implements ObjectProcessing<SolrInputDocument> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String solrCollection;
    private final SolrClient solrClient;

    public SolrDocumentIndexing(ConcurrentUpdateSolrClient solr, @Value("${dcn.solr.collection:plooi}") String collection) {
        this.solrCollection = collection;
        this.solrClient = solr;
    }

    @Override
    public String process(SolrInputDocument solrDoc) {
        var dcnId = solrDoc.getFieldValue("dcn_id").toString();
        try {
            this.logger.debug("Indexing Solr document for {}", dcnId);
            Objects.requireNonNull(this.solrClient, "Solr client is not set. Is dcn.solr.url properly configured?")
                    .add(this.solrCollection, solrDoc);
        } catch (SolrServerException e) {
            throw new SolrDocumentIndexingException("Error while indexing solr file for document " + dcnId, e.getRootCause());
        } catch (IOException e) {
            throw new SolrDocumentIndexingException("Error while indexing solr file for document " + dcnId, e);
        }
        return dcnId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " in " + this.solrClient + "/" + this.solrCollection;
    }
}

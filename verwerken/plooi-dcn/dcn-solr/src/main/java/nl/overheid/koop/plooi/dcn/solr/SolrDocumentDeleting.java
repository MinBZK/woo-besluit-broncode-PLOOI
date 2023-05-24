package nl.overheid.koop.plooi.dcn.solr;

import java.io.IOException;
import java.util.Objects;
import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Deletes a document from Solr.
 */
@Service
public class SolrDocumentDeleting implements EnvelopeProcessing<Envelope> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String solrCollection;
    private final SolrClient solrClient;

    public SolrDocumentDeleting(ConcurrentUpdateSolrClient client, @Value("${dcn.solr.collection:plooi}") String collection) {
        this.solrCollection = collection;
        this.solrClient = client;
    }

    @Override
    public Envelope process(Envelope target) {
        try {
            this.logger.debug("Deleting Solr document with ID {}", target.getPlooiIntern().getDcnId());
            Objects.requireNonNull(this.solrClient, "Solr client is not set. Is dcn.solr.url properly configured?")
                    .deleteByQuery(this.solrCollection, "dcn_id:" + target.getPlooiIntern().getDcnId());
        } catch (SolrServerException e) {
            throw new SolrDocumentIndexingException("Error while deleting solr file for document " + target.getPlooiIntern().getDcnId(), e.getRootCause());
        } catch (IOException e) {
            throw new SolrDocumentIndexingException("Error while deleting solr file for document " + target.getPlooiIntern().getDcnId(), e);
        }
        return target;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " in " + this.solrClient + "/" + this.solrCollection;
    }
}

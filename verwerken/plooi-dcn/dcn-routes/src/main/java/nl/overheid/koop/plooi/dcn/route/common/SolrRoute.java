package nl.overheid.koop.plooi.dcn.route.common;

import nl.overheid.koop.dcn.publishingstate.PublishingStateUpdateProcessing;
import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.PublishingStateRepository;
import nl.overheid.koop.plooi.dcn.repository.store.PlooiManifestReading;
import nl.overheid.koop.plooi.dcn.route.processor.EnvelopeProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.IdProcessor;
import nl.overheid.koop.plooi.dcn.route.processor.ObjectProcessor;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentBuilder;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentDeleting;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentIndexing;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Routes dealing with Solr updates.
 */
@Component
public class SolrRoute extends RouteBuilder {

    public static final String SOLR_INDEX_ROUTE = "dcn-solr-index";
    public static final String SOLR_DELETE_ROUTE = "dcn-solr-delete";

    private final PublicatieClient publicatieClient;
    private final SolrDocumentIndexing solrIndexer;
    private final SolrDocumentDeleting solrDeleter;
    private final PublishingStateRepository publishingStateRepository;

    public SolrRoute(PublicatieClient publClient, SolrDocumentIndexing solrDocIndexing, SolrDocumentDeleting solrDocDeleting,
            PublishingStateRepository publStateRepos) {
        this.publicatieClient = publClient;
        this.solrIndexer = solrDocIndexing;
        this.solrDeleter = solrDocDeleting;
        this.publishingStateRepository = publStateRepos;
    }

    @Override
    public void configure() {
        new ErrorHandling(this).configure();
        from(Routing.external(SOLR_INDEX_ROUTE)).routeId(SOLR_INDEX_ROUTE)
                .process(new IdProcessor(new SolrDocumentBuilder(this.publicatieClient)))
                .process(new ObjectProcessor<>(this.solrIndexer))
                .process(new IdProcessor(new PublishingStateUpdateProcessing(this.publishingStateRepository, PublishingState.State.INPROGRESS)));
        from(Routing.external(SOLR_DELETE_ROUTE)).routeId(SOLR_DELETE_ROUTE)
                .process(new IdProcessor(new PlooiManifestReading(this.publicatieClient)))
                .process(new EnvelopeProcessor<>(this.solrDeleter));
    }
}

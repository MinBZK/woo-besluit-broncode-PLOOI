package nl.overheid.koop.dcn.publishingstate;

import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.PublishingStateRepository;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

/**
 * this service is used as job and responsible on updating the publishing state recorded with status in progress into
 * status OK.
 */
@Service
public class PublishingStateService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PublishingStateRepository publishingStateRepository;
    private final SolrDocumentService solrDocumentService;
    protected static final String[] FIND_ID_FIELD = { "dcn_id" };

    public PublishingStateService(PublishingStateRepository publishingStateRepository, SolrDocumentService solrDocumentService) {
        this.publishingStateRepository = publishingStateRepository;
        this.solrDocumentService = solrDocumentService;
    }

    /**
     * check  if the publishing state database contains records with state in progress, if yes verify it in Solr
     * and update publish state to Ok.
     */
    @Transactional
    public void processPublishingStates() {
        try (Stream<String> stream = this.publishingStateRepository.getInProgressPublishingState()) {
            logger.debug("publishing state Job started");
            stream.forEach(dcnId ->
                    this.solrDocumentService.getIndexedDocument(dcnId, FIND_ID_FIELD)
                            .findFirst()
                            .ifPresent(solrDoc -> {
                                logger.debug("update publishing state document {} to OK", dcnId);
                                this.publishingStateRepository.updateAndSave(dcnId, PublishingState.State.OK);
                            }));
        }
    }
}

package nl.overheid.koop.plooi.dcn.admin;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import nl.overheid.koop.plooi.dcn.admin.model.BulkActionRequest;
import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.PublishingStateRepository;
import nl.overheid.koop.plooi.dcn.route.common.CommonRoute;
import nl.overheid.koop.plooi.dcn.route.common.Routing;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.registration.model.Severity;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    public static final String JSON = ".json";
    public static final String MIME_JSON = "application/json";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ProducerTemplate producerTemplate;
    private final String portalUrl;
    private final PublishingStateRepository publishingStateRepository;
    private final RegistrationClient registrationClient;

    public AdminService(CamelContext camelContext, @Value("${open.portal.url}") String portalUrl, RegistrationClient regClient,
            PublishingStateRepository publStateRepos) {
        this.producerTemplate = camelContext.createProducerTemplate();
        this.portalUrl = portalUrl;
        this.publishingStateRepository = publStateRepos;
        this.registrationClient = regClient;
    }

    public void redoDocumentsWithMappingErrors(String sourceName, String targetElementName, String sourceLabel, Severity severity) {
        this.registrationClient
                .getVerwerkingMetMappingFouten(sourceName, targetElementName, sourceLabel, severity)
                .forEach(this::processDocument);
    }

    public void redoDocument(String internalId) {
        this.logger.debug("Send redo message for document {}", internalId);
        this.producerTemplate.sendBody(Routing.external(CommonRoute.DCN_UNDELETE_DOCUMENT), internalId);
    }

    public void unpublishDocument(String internalId) {
        this.logger.debug("Send unpublish message for document {}", internalId);
        this.producerTemplate.sendBody(Routing.external(CommonRoute.DCN_DELETE_DOCUMENT), internalId);
    }

    public void processDocument(String internalId) {
        this.logger.debug("Send process message for document {}", internalId);
        this.producerTemplate.sendBody(Routing.external(CommonRoute.DCN_PROCESS_DOCUMENT), internalId);
    }

    public Boolean redoSourceDocuments(String sourceName) {
        List<String> sourceDocumentEventInternalIds = this.registrationClient.getDcnIdsByBron(sourceName);
        if (sourceDocumentEventInternalIds.isEmpty()) {
            this.logger.info("No documents retrieved for the source: {}", sourceName);
            return false;
        } else {
            this.logger.info("Redo operation for the source: {}", sourceName);
            for (String internalId : sourceDocumentEventInternalIds) {
                redoDocument(internalId);
            }
            this.logger.info("Redo is sent for the source: {}", sourceName);
            return true;
        }
    }

    public void processBulkDocuments(BulkActionRequest bulkActionRequest) {
        this.logger.debug("Send {} message for query {}", bulkActionRequest.triggerType(), bulkActionRequest.filter());
        var proces = this.registrationClient.createProces("", bulkActionRequest.triggerType().toString(), bulkActionRequest.reason());
        this.producerTemplate.sendBodyAndHeaders(Routing.external(CommonRoute.COMMON_BULK_ROUTER), null,
                Map.of("dcn_executionid", proces.getId(),
                        "query", bulkActionRequest.filter(),
                        "action", bulkActionRequest.action()));
    }

    public String getPortalUrl() {
        return this.portalUrl;
    }

    Page<PublishingState> getPublishingStateSummaryOlderThanDate(OffsetDateTime dateTime, Pageable page) {
        return this.publishingStateRepository.getExecutionsWithNotIndexedPublishingStates(dateTime, page);
    }
}

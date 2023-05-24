package nl.overheid.koop.plooi.dcn.admin;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import nl.overheid.koop.plooi.dcn.admin.model.BulkActionRequest;
import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentService;
import nl.overheid.koop.plooi.dcn.solr.models.SolrSearchRequest;
import nl.overheid.koop.plooi.registration.model.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    protected static final String[] ADMIN_DOCUMENT_FIELDS = { "available_start", "creator", "dcn_id", "type", "topthema", "title", "identifier", "timestamp" };
    protected static final String[] FIND_ID_FIELD = { "identifier", "dcn_id", "id" };
    private static final String PAGE_SIZE = "25";
    public static final int MAX_MINUTES_NOT_PROCESSED = 30;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AdminService adminService;
    private final SolrDocumentService solrDocumentService;

    public AdminController(AdminService adminService,
            SolrDocumentService solrDocumentService) {
        this.adminService = adminService;
        this.solrDocumentService = solrDocumentService;
    }

    @PostMapping("redo-mapping-error-docs")
    public void redoMappingErrorDocuments(@RequestParam("sourceName") String sourceName,
            @RequestParam("targetElementName") String targetElementName,
            @RequestParam("sourceLabel") String sourceLabel,
            @RequestParam("severity") Severity severity) {
        this.adminService.redoDocumentsWithMappingErrors(sourceName, targetElementName, sourceLabel, severity);
    }

    @PostMapping("/redo/{internalId}")
    @ResponseBody
    public void redoDocument(@PathVariable String internalId) {
        this.adminService.redoDocument(internalId);
    }

    @PostMapping("/redo/source/{sourceName}")
    @ResponseBody
    public Boolean redoAllSourceDocuments(@PathVariable String sourceName) {
        return this.adminService.redoSourceDocuments(sourceName);
    }

    @PostMapping("/unpublish/{internalId}")
    @ResponseBody
    public void unpublishDocument(@PathVariable String internalId) {
        this.adminService.unpublishDocument(internalId);
    }

    @PostMapping("/process/{internalId}")
    @ResponseBody
    public void publishDocument(@PathVariable String internalId) {
        this.adminService.processDocument(internalId);
    }

    @GetMapping("/solrDocument/{query}")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity getSolrDocument(@PathVariable String query) {
        return this.solrDocumentService.getDocument(query);
    }

    @PostMapping("/solrDocuments")
    @ResponseBody
    @SuppressWarnings("rawtypes")
    public ResponseEntity getSolrDocuments(@RequestBody SolrSearchRequest filter,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) int size) {
        return this.solrDocumentService.getDocuments(filter, PageRequest.of(page, size), ADMIN_DOCUMENT_FIELDS);
    }

    @GetMapping("/solrSources")
    @SuppressWarnings("rawtypes")
    public ResponseEntity getSolrSources() {
        this.logger.debug("get solr sources list");
        return this.solrDocumentService.getFilterLists();
    }

    @PostMapping("/bulkAction")
    public void applyBulkAction(@RequestBody BulkActionRequest bulkActionRequest) {
        this.adminService.processBulkDocuments(bulkActionRequest);
    }

    @GetMapping("/portalUrl")
    public String getPortalUrl() {
        return this.adminService.getPortalUrl();
    }

    @GetMapping("/getPublishingStates")
    public Page<PublishingState> getPublishingStates(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = PAGE_SIZE) int size) {
        var maxTime = OffsetDateTime.now(ZoneId.of("UTC")).minusMinutes(MAX_MINUTES_NOT_PROCESSED);
        return this.adminService.getPublishingStateSummaryOlderThanDate(maxTime, PageRequest.of(page, size));
    }
}

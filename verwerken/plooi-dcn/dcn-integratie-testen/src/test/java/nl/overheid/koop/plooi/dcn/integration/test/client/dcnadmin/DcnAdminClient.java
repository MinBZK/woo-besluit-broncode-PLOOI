package nl.overheid.koop.plooi.dcn.integration.test.client.dcnadmin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.google.gson.Gson;
import java.net.http.HttpResponse;
import nl.overheid.koop.plooi.dcn.admin.model.BulkActionRequest;
import nl.overheid.koop.plooi.dcn.integration.test.util.ConnectionUtils;
import nl.overheid.koop.plooi.dcn.solr.models.SolrSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class DcnAdminClient {

    private static final String AUTHORIZATION_HEADER = "authorization";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${integration.dcn-admin-tools.root-url}")
    public String dcnAdminToolsRootUrl;

    @Value("${integration.dcn-admin-tools.root-url}/authorization/login")
    public String dcnAuthorizationEndpoint;

    @Value("${integration.dcn-admin-tools.root-url}/admin/solrDocument/")
    public String dcnAdminToolsControllerSolrDocumentQueryUrl;

    @Value("${integration.registration-service-url}/processen")
    public String registrationProcesUrl;

    @Value("${integration.registration-service-url}/verwerkingen/?dcnId=%s")
    public String registrationVerwerkingByDcnIdUrlTemplate;

    @Value("${integration.registration-service-url}/verwerkingen/%s")
    public String registrationVerwerkingUrl;

    @Value("${integration.dcn-admin-tools.root-url}/admin/duplications/")
    public String dcnAdminToolsControllerDuplications;

    @Value("${integration.dcn-admin-tools.root-url}/admin/solrDocuments/")
    public String dcnAdminToolsControllerSolrDocumentsFilterRequests;

    @Value("${integration.dcn-admin-tools.root-url}/admin/bulkAction/")
    public String dcnAdminToolsControllerBulkAction;

    @Value("${integration.dcn-admin-tools.root-url}/verwerken/%s/%s")
    public String processDocumentAction;

    private DcnLoginResponse dcnLoginResponse;

    @Autowired
    private Gson gson;

    public void login(String username, String password) {
        this.logger.debug("Logging in as {} at {}", username, this.dcnAdminToolsRootUrl);
        var request = new DcnLoginRequest(username, password);
        var requestBody = this.gson.toJson(request);
        var loginResponse = ConnectionUtils.getHttpPostResponse(
                this.dcnAuthorizationEndpoint,
                requestBody,
                "Content-Type", "application/json").body();
        this.dcnLoginResponse = this.gson.fromJson(loginResponse, DcnLoginResponse.class);
    }

    public HttpResponse<String> getSolrDocumentBySearchParam(String searchParam, String searchValue) {
        this.logger.debug("Getting Solr document for {}:{} at {}", searchParam, searchValue, this.dcnAdminToolsRootUrl);
        return handleQueryResponseWithHeader(this.dcnAdminToolsControllerSolrDocumentQueryUrl + searchParam + ":" + searchValue);
    }

    public HttpResponse<String> getVerwerkingenBydcnId(String dcnId) {
        this.logger.debug("Getting Verwerkingen for document {} at {}", dcnId, this.dcnAdminToolsRootUrl);
        return handleQueryResponseWithHeader(String.format(this.registrationVerwerkingByDcnIdUrlTemplate, dcnId));
    }

    public HttpResponse<String> getVerwerkingById(String verwerkingId) {
        this.logger.debug("Getting Diagnosis for {} at {}", verwerkingId, this.dcnAdminToolsRootUrl);
        return handleQueryResponseWithHeader(String.format(this.registrationVerwerkingUrl, verwerkingId));
    }

    public HttpResponse<String> getDuplicationByDcnId(String dcnId) {
        this.logger.debug("Getting duplication for {} at {}", dcnId, this.dcnAdminToolsRootUrl);
        return handleQueryResponseWithHeader(this.dcnAdminToolsControllerDuplications + dcnId);
    }

    public HttpResponse<String> createProces(String triggerType) {
        this.logger.debug("create {} proces", triggerType);
        return ConnectionUtils.getHttpPostResponse(
                this.registrationProcesUrl,
                String.format("{\"triggerType\":\"%s\",\"trigger\":\"cuking\"}", triggerType),
                "Content-Type", "application/json");
    }

    public HttpResponse<String> applyProcessAction(String action, String procesId, String dcnId) {
        this.logger.debug("apply {} on document with id {}", action, dcnId);
        return ConnectionUtils.getHttpPostResponse(
                String.format(this.processDocumentAction, action, procesId),
                String.format("[\"%s\"]", dcnId),
                "Content-Type", "application/json");
    }

    public HttpResponse<String> getSolrDocumentBySearchRequest(SolrSearchRequest searchRequest) {
        this.logger.debug("Getting Solr document for query {}", searchRequest);
        return ConnectionUtils.getHttpPostResponse(
                this.dcnAdminToolsControllerSolrDocumentsFilterRequests,
                this.gson.toJson(searchRequest),
                AUTHORIZATION_HEADER, this.dcnLoginResponse.getToken(),
                "Content-Type", "application/json");
    }

    public HttpResponse<String> applyBulkAction(BulkActionRequest bulkActionRequest) {
        return ConnectionUtils.getHttpPostResponse(
                this.dcnAdminToolsControllerBulkAction,
                this.gson.toJson(bulkActionRequest),
                AUTHORIZATION_HEADER, this.dcnLoginResponse.getToken(),
                "Content-Type", "application/json");
    }

    private HttpResponse<String> handleQueryResponseWithHeader(String query) {
        assertNotNull(this.dcnLoginResponse, "Not logged in Dcn Admin Tools yet");
        return ConnectionUtils.getHttpGetResponse(
                query,
                AUTHORIZATION_HEADER, this.dcnLoginResponse.getToken());
    }
}

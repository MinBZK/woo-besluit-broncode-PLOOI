package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minidev.json.JSONArray;
import nl.overheid.koop.plooi.dcn.integration.test.client.actuator.ActuatorClient;
import nl.overheid.koop.plooi.dcn.integration.test.client.dcnadmin.DcnAdminClient;
import nl.overheid.koop.plooi.dcn.integration.test.client.solr.SolrClient;
import nl.overheid.koop.plooi.dcn.integration.test.util.Retry;
import nl.overheid.koop.plooi.dcn.solr.models.DateRangeField;
import nl.overheid.koop.plooi.dcn.solr.models.SimpleField;
import nl.overheid.koop.plooi.dcn.solr.models.SolrSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class GenericSteps {

    public static final int DOC_UPDATE_TIME = 22000;

    @Autowired
    private DcnAdminClient dcnAdminClient;
    @Autowired
    private ActuatorClient actuatorClient;
    @Autowired
    private SolrClient solrClient;
    @Autowired
    private Retry retry;
    @Value("${integration.continues-suffix}")
    public String continuesSuffix;

    private String context;
    private Map<String, String> verwerkingIds;
    private String searchParam;
    private String documentId;
    private SolrSearchRequest solrSearchRequest;
    private String action;
    private String procesId;
    private String routeName;
    private List<String> deletedDocuments = new ArrayList<>();

    @Given("environment for {string} {string} up and running")
    public void environment_for_up_and_running(String context, String routeType) {
        var healthy = this.actuatorClient.waitDcnIsHealthy() && this.solrClient.waitSolrIsHealthy();
        this.context = context;
        this.actuatorClient.checkRouteIsUp(context, routeType);
    }

    @And("the route is triggered")
    public void route_is_triggered() {
        this.actuatorClient.triggerIngressRoute(this.context);
    }

    @When("user searches for the {string} in the Zoek documenten pane of the DCN Admin application")
    public void user_searches_for_the_bron_and_in_the_zoek_documenten_pane_of_the_dcn_admin_application(String searchParam) {
        this.searchParam = searchParam;
    }

    @When("user searches for document with id {string} in the Zoek documenten pane of the DCN Admin application")
    public void user_searches_for_document_in_the_zoek_documenten_pane_of_the_dcn_admin_application(String documentId) {
        this.documentId = documentId;
    }

    @When("user searches for document with {string} is {string} in the Zoek documenten pane of the DCN Admin application")
    public void user_searches_for_document_with_id_in_the_zoek_documenten_pane_of_the_dcn_admin_application(String searchParam, String documentId) {
        this.searchParam = searchParam;
        this.documentId = documentId;
    }

    @When("click on toon duplicaten open table will contain the following data")
    public void click_on_toon_duplicaten_open_table_with_contain_the_following_data(DataTable dataTable) {
        this.retry.handle(dataTable, this::handleRelationnFields, "Did not find Solr entry");
    }

    @Then("the Solr fields contains the following data")
    public void the_solr_fields_contains_the_following_data(DataTable dataTable) {
        this.retry.handle(dataTable, this::handleSolrFieldCheck, "Did not find Solr entry");
    }

    @Then("Latest verwerking entries contain the following data")
    public void latest_verwerking_entries_contain_the_following_data(DataTable dataTable) {
        this.retry.handle(dataTable, this::handleVerwerkingFields, "Did not find verwerkingen");
    }

    @Then("Diagnoses entries contain the following data")
    public void diagnoses_entries_contain_the_following_data(DataTable dataTable) {
        this.retry.handle(dataTable, this::handleDiagnosesFields, "Did not find Diagnoses");
    }

    @Then("Exceptie contain the following data")
    public void processing_errors_entries_contain_the_following_data(DataTable dataTable) {
        this.retry.handle(dataTable, this::handleExceptieFields, "Did not find Excepties");
    }

    @And("he clicks on the {string} action")
    public void click_on_action(String selectedAction) {
        this.action = selectedAction;
    }

    @When("a {string} proces is created")
    public void verwerking_creates_proces(String triggerType) {
        DocumentActionReport documentActionReport = new DocumentActionReport();
        this.procesId = createProces(documentActionReport, triggerType);
        assertTrue(documentActionReport.documentsAreUpdated());
    }

    @When("the {string} field in the Solr document details is updated")
    public void verwerking_updates_solr_field(String fieldName) {
        DocumentActionReport documentActionReport = new DocumentActionReport();
        getSolrDocumentFieldValue(documentActionReport, fieldName);
        applyProcessAction(documentActionReport);
        getSolrDocumentFieldValue(documentActionReport, fieldName);
        assertTrue(documentActionReport.documentsAreUpdated());
    }

    @When("user search documents by index and type {string} in {string} search field")
    public void user_search_documents_by_index_and_type_term(String searchTerm, String searchField) {
        SolrSearchRequest searchRequest = new SolrSearchRequest(
                null,
                List.of(new SimpleField(searchField, searchTerm)),
                new ArrayList<>(),
                new DateRangeField("available_start", 0, 0));
        this.solrSearchRequest = searchRequest;
    }

//    @And("user select action button (verwerk opnieuw)")
//    public void select_action_button() {
//        triggerType = TriggerType.REPROCESS;
//        this.routeName = CommonRoute.DCN_PROCESS_DOCUMENT;
//    }
//
//    @When("all these documents will be re-processed in bulk and the timestamp of these documents will be updated in solr")
//    public void user_search_for_documents_by_query() {
//        DocumentActionReport updateActionReport = new DocumentActionReport();
//        getSolrDocumentIdAndTimeStamp(updateActionReport);
//
//        applyBulkAction(updateActionReport);
//        getSolrDocumentIdAndTimeStamp(updateActionReport);
//
//        assertTrue(updateActionReport.documentsAreUpdated());
//    }
//
//    @When("click on opnieuw publiceer action button in de document details page will trigger {string} and update document {string} in solr")
//    public void click_on_publiceer_action_in_document_details_page(String action, String field) {
//        DocumentActionReport documentActionReport = new DocumentActionReport();
//        applyDocumentAction(documentActionReport, action);
//        getSolrDocumentFieldValue(documentActionReport, field);
//
//        assertTrue(documentActionReport.documentsAreUpdated());
//    }
//
//    @And("user select action button intrekken")
//    public void select_intrekken_action_button() {
//        triggerType = TriggerType.DELETION;
//        this.routeName = CommonRoute.DCN_DELETE_DOCUMENT;
//    }
//
//    @When("all these documents will be deleted in bulk from solr")
//    public void click_on_intrekken_action_in_document_list_page() {
//        DocumentActionReport documentActionReport = new DocumentActionReport();
//
//        getSolrDocumentIdAndTimeStamp(documentActionReport);
//        this.deletedDocuments = documentActionReport.getDocuments().stream().map(DocumentAction::getDocumentId).toList();
//        applyBulkAction(documentActionReport);
//        getSolrDocumentIdAndTimeStamp(documentActionReport);
//
//        assertTrue(documentActionReport.documentsAreUpdated());
//    }

    private static final String TIME_SUFFIX = "T00:00:00.000+00:00";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean handleSolrFieldCheck(DataTable dataTable) {
        dataTable = dataTable.transpose();
        TestReport testReport = new TestReport();
        List<String> solrFieldNames = dataTable.row(0);
        for (int i = 2; i < dataTable.height(); i++) {
            List<String> row = dataTable.row(i);
            String testName = row.get(0);
            String internalId = row.get(2); // internalId is same with id in solr, double checking on both sides
            HttpResponse<String> response = this.dcnAdminClient.getSolrDocumentBySearchParam(this.searchParam, internalId);
            if (response.statusCode() != 200) {
                return false;
            } else if (!response.body().isBlank() && JsonPath.parse(response.body()).read("$.length()").equals(0)) {
                return false;
            } else {
                String solrResponseJSON = response.body();
                for (int j = 1; j < row.size(); j++) {
                    String field = solrFieldNames.get(j);
                    String expectedValue = row.get(j) == null ? "" : row.get(j).trim();
                    String actualValue;
                    try {
                        var solrValueAsObject = JsonPath.parse(solrResponseJSON).read("$[0]['" + field + "']");
                        var solrValueStr = (solrValueAsObject instanceof List solrList) ? String.join(", ", solrList) : solrValueAsObject.toString();
                        if (expectedValue.endsWith(this.continuesSuffix)) {
                            expectedValue = expectedValue.substring(0, expectedValue.trim().lastIndexOf(this.continuesSuffix)).trim();
                            actualValue = !solrValueStr.isBlank() && solrValueStr.length() > expectedValue.length()
                                    ? solrValueStr.substring(0, expectedValue.length())
                                    : solrValueStr;
                        } else if (solrValueStr.endsWith(TIME_SUFFIX)) {
                            actualValue = solrValueStr.replace(TIME_SUFFIX, "");
                        } else {
                            actualValue = solrValueStr;
                        }
                    } catch (JsonPathException e) {
                        actualValue = "";
                    }
                    testReport.add(testName, field, expectedValue, actualValue);
                }
            }
        }
        assertTrue(testReport.isPassed(), testReport.toString());
        return true;
    }

    private boolean handleVerwerkingFields(DataTable dataTable) {
        TestReport testReport = new TestReport();
        List<String> verwerkingFieldNames = dataTable.row(0);
        this.verwerkingIds = new HashMap<String, String>();
        for (int i = 1; i < dataTable.height(); i++) {
            List<String> row = dataTable.row(i);
            String testName = row.get(0);
            String dcnId = row.get(2);
            HttpResponse<String> response = this.dcnAdminClient.getVerwerkingenBydcnId(dcnId);
            if (response.statusCode() != 200) {
                return false;
            } else if (!response.body().isBlank() && JsonPath.parse(response.body()).read("$['totalElements']").equals(0)) {
                return false;
            } else {
                String verwerkingenJSON = response.body();
                String verwerkingFiltered = filterJSONResult(verwerkingenJSON, verwerkingFieldNames, row);
                String verwerkingId = JsonPath.parse(verwerkingFiltered).read("$[0]['id']");
                this.verwerkingIds.put(testName, verwerkingId);
                for (int j = 1; j < row.size(); j++) {
                    String field = verwerkingFieldNames.get(j);
                    String expectedValue = row.get(j) == null ? "" : row.get(j).trim();
                    String actualValue = null;
                    if (field.equals("extIds")) {
                        JSONArray extIdsArray = JsonPath.parse(verwerkingFiltered).read("$[0]['" + field + "']");
                        actualValue = extIdsArray.stream().map(Object::toString).collect(Collectors.joining(", "));
                    } else {
                        actualValue = JsonPath.parse(verwerkingFiltered).read("$[0]['" + field + "']");
                    }
                    testReport.add(testName, field, expectedValue, actualValue);
                }
            }
        }
        assertTrue(testReport.isPassed(), testReport.toString());
        return true;
    }

    private boolean handleDiagnosesFields(DataTable dataTable) {
        TestReport testReport = new TestReport();
        List<String> diagnosisFieldNames = dataTable.row(0);
        for (int i = 1; i < dataTable.height(); i++) {
            List<String> row = new ArrayList<>(dataTable.row(i));
            String verwerkingId = this.verwerkingIds.get(row.get(1));
            row.set(1, verwerkingId);
            String testName = row.get(0);
            HttpResponse<String> response = this.dcnAdminClient.getVerwerkingById(verwerkingId);
            if (response.statusCode() != 200) {
                return false;
            } else {
                String diagnosisJSON = response.body();
                String diagnosisFiltered = filterJSONVerwerkingDataResult(diagnosisJSON, diagnosisFieldNames, row, "diagnoses");
                for (int j = 2; j < row.size(); j++) {
                    String field = diagnosisFieldNames.get(j);
                    String expectedValue = row.get(j) == null ? "" : row.get(j).trim();
                    String actualValue = JsonPath.parse(diagnosisFiltered).read("$[0]['" + field + "']");
                    if (expectedValue.endsWith(this.continuesSuffix)) {
                        expectedValue = expectedValue.substring(0, expectedValue.trim().lastIndexOf(this.continuesSuffix)).trim();
                        actualValue = !actualValue.isBlank() ? actualValue.substring(0, expectedValue.length()) : actualValue;
                    }
                    testReport.add(testName, field, expectedValue, actualValue);
                }
            }
        }

        assertTrue(testReport.isPassed(), testReport.toString());
        return true;
    }

    private boolean handleExceptieFields(DataTable dataTable) {
        TestReport testReport = new TestReport();
        List<String> processingFieldNames = dataTable.row(0);
        for (int i = 1; i < dataTable.height(); i++) {
            List<String> row = new ArrayList<>(dataTable.row(i));
            String testName = row.get(0);
            String verwerkingId = this.verwerkingIds.get(row.get(0));
            HttpResponse<String> response = this.dcnAdminClient.getVerwerkingById(verwerkingId);
            if (response.statusCode() != 200) {
                return false;
            } else {
                String processingErrorsJSON = response.body();
                String processingErrorsFiltered = filterJSONVerwerkingDataResult(processingErrorsJSON, processingFieldNames, row, "exceptie");
                for (int j = 1; j < row.size(); j++) {
                    String field = processingFieldNames.get(j);
                    String expectedValue = row.get(j) == null ? "" : row.get(j).trim();
                    String actualValue = JsonPath.parse(processingErrorsFiltered).read("$[0]['" + field + "']");
                    if (expectedValue.endsWith(this.continuesSuffix)) {
                        expectedValue = expectedValue.substring(0, expectedValue.trim().lastIndexOf(this.continuesSuffix)).trim();
                        actualValue = !actualValue.isBlank() ? actualValue.substring(0, expectedValue.length()) : actualValue;
                    }
                    testReport.add(testName, field, expectedValue, actualValue);
                }
            }
        }
        assertTrue(testReport.isPassed(), testReport.toString());
        return true;
    }

    private boolean handleRelationnFields(DataTable dataTable) {
        TestReport testReport = new TestReport();
        List<String> relationFieldsNames = dataTable.row(0);
        for (int i = 1; i < dataTable.height(); i++) {
            List<String> row = new ArrayList<>(dataTable.row(i));
            String testName = row.get(0);
            HttpResponse<String> response = this.dcnAdminClient.getDuplicationByDcnId(this.documentId);
            if (response.statusCode() != 200) {
                return false;
            } else {
                String duplicationJSON = response.body();
                for (int j = 1; j < row.size(); j++) {
                    String field = relationFieldsNames.get(j);
                    String expectedValue = row.get(j) == null ? "" : row.get(j).trim();
                    String actualValue = JsonPath.parse(duplicationJSON).read("$[0]['" + field + "']");
                    if (expectedValue.endsWith(this.continuesSuffix)) {
                        expectedValue = expectedValue.substring(0, expectedValue.trim().lastIndexOf(this.continuesSuffix)).trim();
                        actualValue = !actualValue.isBlank() ? actualValue.substring(0, expectedValue.length()) : actualValue;
                    }
                    testReport.add(testName, field, expectedValue, actualValue);
                }
            }
        }
        assertTrue(testReport.isPassed(), testReport.toString());
        return true;
    }

    /*
     * Returns a JSONArray with a single value.
     *
     * Note: It's not possible to return a single JSONObject with a JSONPath filter
     */
    private String filterJSONResult(String jsonResult, List<String> fieldNames, List<String> row) {
        StringBuilder builder = new StringBuilder();
        builder.append("$['content'][?(");
        for (int j = 1; j < row.size(); j++) {
            String field = fieldNames.get(j);
            String expectedValue = row.get(j) == null ? "" : row.get(j).trim();
            String filter = "";
            if (expectedValue.endsWith(this.continuesSuffix)) {
                expectedValue = expectedValue.substring(0, expectedValue.trim().lastIndexOf(this.continuesSuffix)).trim();
                filter = "@." + field + " =~ /" + expectedValue + ".*?/i ";
            } else if (field.equals("extIds")) {
                filter = "@." + field + " == " + "[" + expectedValue + "]";
            } else {
                filter = "@." + field + " == " + "'" + expectedValue + "' ";
            }
            if (j != 1) {
                builder.append(" && ");
            }
            builder.append(filter);
        }
        builder.append(")]");
        String filter = builder.toString();
        JSONArray jarray = JsonPath.parse(jsonResult).read(filter);
        assertFalse(jarray.isEmpty(), "No entry is found in the reponse matching with expected values. Filter: " + filter);
        assertTrue(jarray.size() < 2, "Entry by filter has duplicate entries. Filter: " + filter);
        return jarray.toJSONString();
    }

    private String filterJSONVerwerkingDataResult(String jsonResult, List<String> fieldNames, List<String> row, String data) {
        StringBuilder builder = new StringBuilder();
        builder.append("$['").append(data).append("'][?(");
        for (int j = 2; j < row.size(); j++) {
            String field = fieldNames.get(j);
            String expectedValue = row.get(j) == null ? "" : row.get(j).trim();
            String filter = "";
            if (expectedValue.endsWith(this.continuesSuffix)) {
                expectedValue = expectedValue.substring(0, expectedValue.trim().lastIndexOf(this.continuesSuffix)).trim();
                filter = "@." + field + " =~ /" + expectedValue + ".*?/i ";
            } else {
                filter = "@." + field + " == " + "'" + expectedValue + "' ";
            }
            if (j != 2) {
                builder.append(" && ");
            }
            builder.append(filter);
        }
        builder.append(")]");
        String filter = builder.toString();
        JSONArray jarray = JsonPath.parse(jsonResult).read(filter);
        assertFalse(jarray.isEmpty(), "No entry is found in the reponse matching with expected values. Filter: " + filter);
        assertTrue(jarray.size() < 2, "Entry by filter has duplicate entries. Filter: " + filter);
        return jarray.toJSONString();
    }

    private String createProces(DocumentActionReport utr, String triggerType) {
        HttpResponse<String> response = this.dcnAdminClient.createProces(triggerType);
        utr.getTestReport().add("", String.format("Create proces %s ", triggerType), "200", String.valueOf(response.statusCode()));
        return JsonPath.parse(response.body()).read("$.id", String.class);
    }

//    private void applyBulkAction(DocumentActionReport updateActionReport, String triggerType) {
//        BulkActionRequest bulkActionRequest = new BulkActionRequest(TriggerType.valueOf(triggerType), this.solrSearchRequest, triggerType, this.routeName);
//        HttpResponse<String> response = this.dcnAdminClient.applyBulkAction(bulkActionRequest);
//        updateActionReport.getTestReport().add("", String.format("%s Bulk action", triggerType), "200", String.valueOf(response.statusCode()));
//        Retry.sleep(DOC_UPDATE_TIME);
//    }

    private void applyProcessAction(DocumentActionReport utr) {
        HttpResponse<String> response = this.dcnAdminClient.applyProcessAction(this.action, this.procesId, this.documentId);
        utr.getTestReport().add("", String.format("Apply document %s ", this.action), "200", String.valueOf(response.statusCode()));
        Retry.sleep(DOC_UPDATE_TIME);
    }

    private void getSolrDocumentFieldValue(DocumentActionReport documentActionReport, String field) {
        String response = getSolrDocument(documentActionReport);
        String timeStamp = response != null ? JsonPath.parse(response).read("$[0]['" + field + "']") : null;
        documentActionReport.add(this.documentId, timeStamp, this.action);
    }

    private String getSolrDocument(DocumentActionReport documentActionReport) {
        HttpResponse<String> response = this.dcnAdminClient.getSolrDocumentBySearchParam(this.searchParam, this.documentId);
        documentActionReport.getTestReport().add("", "Get solr document", "200", String.valueOf(response.statusCode()));
        if (response.statusCode() != 200) {
            return null;
        } else if (!response.body().isBlank() && JsonPath.parse(response.body()).read("$.length()").equals(0)) {
            return null;
        } else {
            return response.body();
        }
    }

    private void getSolrDocumentIdAndTimeStamp(DocumentActionReport updateActionReport, String action) {
        List<String> ids = getFieldFromDocument(updateActionReport, new StringBuilder().append("$['content']..dcn_id"));
        List<String> timeStamps = getFieldFromDocument(updateActionReport, new StringBuilder().append("$['content']..timestamp"));
        IntStream.range(0, ids.size()).forEach(i -> updateActionReport.add(ids.get(i), timeStamps.get(i), action));
    }

    private List<String> getFieldFromDocument(DocumentActionReport updateActionReport, StringBuilder filter) {
        HttpResponse<String> response = this.dcnAdminClient.getSolrDocumentBySearchRequest(this.solrSearchRequest);
        updateActionReport.getTestReport()
                .add("", String.format("Get solr documents by search request %s", this.solrSearchRequest.toString()), "200",
                        String.valueOf(response.statusCode()));
        if (response.statusCode() != 200) {
            return List.of();
        } else if (!response.body().isBlank() && JsonPath.parse(response.body()).read("$.length()").equals(0)) {
            return List.of();
        } else {
            return JsonPath.parse(response.body()).read(filter.toString());
        }
    }
}

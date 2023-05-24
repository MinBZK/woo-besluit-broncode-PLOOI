package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import com.google.common.primitives.Bytes;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonArray;
import nl.overheid.koop.plooi.dcn.integration.test.util.ConnectionUtils;
import nl.overheid.koop.plooi.dcn.integration.test.util.Retry;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;

public class ServerHandleSteps {

    private static final String MOCK_SERVICE_PLACEHOLDER = "__MOCK_SERVICE__";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Properties urlmappings = new Properties();

    @Value("${integration.mock.server.internal}")
    private String mockServerInternal;
    @Value("${integration.mock.server.external}")
    private String mockServerExternal;
    @Value("${integration.test.source-folder}")
    private String testSourceFolder;
    @Value("${integration.test.source-folder}/aanlevering-api/")
    private String plooiApiSourceFolder;
    @Value("${integration.dcn-admin-tools.root-url}/dcn/api/")
    private String aanleveringApiBaseUrl;

    @Autowired
    private Retry retry;

    private String aanleveringGUID;
    private int aanleveringStatusCode;

    @PostConstruct
    public void init() throws IOException {
        try (var urlmappingStream = Files.newInputStream(new File(this.testSourceFolder, "/urlmapping.properties").toPath())) {
            this.urlmappings.load(urlmappingStream);
        }
    }

    @Given("documents are available in the {string} server")
    public void documents_are_available_in_the_directory(String directory) {
        var mockSourceDir = new File(this.testSourceFolder, directory);
        for (File mockFile : FileUtils.listFiles(mockSourceDir, FileFileFilter.INSTANCE, DirectoryFileFilter.DIRECTORY)) {
            var mockPath = mockFile.getPath().substring(mockSourceDir.toString().length() + 1).replace("\\", "/");
            this.logger.debug("Mocking request to {}", mockPath);
            var response = ConnectionUtils.getHttpPutResponse("http://"
                    + Objects.requireNonNull(StringUtils.trimToNull(this.mockServerExternal), "External mockserver URL is required")
                    + "/mockserver/expectation",
                    buildRequest("/" + this.urlmappings.getProperty(mockPath, mockPath), mockFile));
            assertEquals(201, response.statusCode(), "Unexpected response while mocking " + mockPath);
        }
    }

    @Given("document {string} is sent to the Aanlevering API, with parts")
    public void document_is_sent_to_the_aanlevering_api_with_parts(String guid, DataTable dataTable) {
        this.aanleveringGUID = guid;
        List<String> fieldNames = dataTable.row(0);
        List<Map<String, String>> params = new ArrayList<>();

        for (int i = 1; i < dataTable.height(); i++) {
            List<String> row = dataTable.row(i);
            Map<String, String> valueContents = new HashMap<>();
            for (int j = 0; j < row.size(); j++) {
                valueContents.put(fieldNames.get(j), row.get(j));
            }
            params.add(valueContents);
        }
        handlePostToAanleveringAPI(params);
    }

    private void handlePostToAanleveringAPI(List<Map<String, String>> values) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        values.forEach(partMap -> entityBuilder.addPart(partMap.get("manifestLabel"),
                new FileBody(new File(this.plooiApiSourceFolder + partMap.get("fileName")), ContentType.create(partMap.get("contentType")))));
        postDocumentsToAanleveringAPI(this.aanleveringGUID, entityBuilder.build());
    }

    private void postDocumentsToAanleveringAPI(String guid, HttpEntity entity) {
        String url = this.aanleveringApiBaseUrl + guid;
        try {
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);
            var response = HttpClientBuilder.create().build().execute(request);

            this.aanleveringStatusCode = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            fail("Cant POST request to: " + url);
        }
    }

    @Then("status code is {int}")
    public void for_document_the_following_parts_can_be_retrieved(int statusCode) {
        assertEquals(statusCode, this.aanleveringStatusCode, "Cannot successfully POST to Aanlevering API");
    }

    @And("for document {string} the following parts can be retrieved")
    public void for_document_the_following_parts_can_be_retrieved(String guid, DataTable dataTable) {
        this.retry.handle(dataTable, this::handleAanleveringAPIFields, "Did not find aanlevering response");
    }

    private boolean handleAanleveringAPIFields(DataTable dataTable) {
        TestReport testReport = new TestReport();
        List<String> fieldNames = dataTable.row(0);

        for (int i = 1; i < dataTable.height(); i++) {
            List<String> row = dataTable.row(i);

            String testName = row.get(0);
            String manifestLabel = row.get(1);
            String expectedValue = row.get(2);

            /*SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS*/
            Retry.sleep(1000);

            String url = this.aanleveringApiBaseUrl + manifestLabel + "/" + this.aanleveringGUID;
            try {
                HttpGet request = new HttpGet(url);
                var response = HttpClientBuilder.create().build().execute(request);

                assertEquals(200, response.getStatusLine().getStatusCode(), url + " status not 200 OK");
                String actualValue = getFileNameFromContentDispHeader(response);

                testReport.add(testName, fieldNames.get(i), expectedValue, actualValue);
            } catch (IOException e) {
                fail("Cant GET request from: " + url);
            }
        }

        assertTrue(testReport.isPassed(), testReport.toString());
        return true;
    }

    private String getFileNameFromContentDispHeader(CloseableHttpResponse docResponse) {
        return ContentDisposition.parse(docResponse.getFirstHeader(HttpHeaders.CONTENT_DISPOSITION).getValue()).getFilename();
    }

    public String buildRequest(String request, File filePath) {
        try {
            var fileContent = Files.readAllBytes(filePath.toPath());
            if (Bytes.indexOf(fileContent, MOCK_SERVICE_PLACEHOLDER.getBytes(StandardCharsets.UTF_8)) > 0) {
                fileContent = new String(fileContent, StandardCharsets.UTF_8)
                        .replaceAll(
                                MOCK_SERVICE_PLACEHOLDER,
                                Objects.requireNonNull(StringUtils.trimToNull(this.mockServerInternal), "Internal mockserver URL is required"))
                        .getBytes(StandardCharsets.UTF_8);
            }
            return buildRequestObject(
                    request.indexOf('?') < 0 ? request : request.substring(0, request.indexOf('?')),
                    request.indexOf('?') < 0 ? null : request.substring(request.indexOf('?') + 1).split("\\&"),
                    getContentTypeByFileName(FilenameUtils.getExtension(filePath.toString())),
                    Base64.encodeBase64String(fileContent)).toString();
        } catch (IOException e) {
            throw new AssertionFailedError("Cannot read file " + filePath, e);
        }
    }

    private JsonArray buildRequestObject(String path, String[] parameters, String contentType, String body) {
        var httpRequestBuilder = Json.createObjectBuilder().add("path", path);
        if (parameters != null && parameters.length > 0) {
            var queryStringParametersBuilder = Json.createObjectBuilder();
            for (String parameter : parameters) {
                int splitPos = parameter.indexOf('=');
                queryStringParametersBuilder.add(parameter.substring(0, splitPos), Json.createArrayBuilder().add(parameter.substring(splitPos + 1)));
            }
            httpRequestBuilder.add("queryStringParameters", queryStringParametersBuilder);
        }
        return Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                        .add("httpRequest", httpRequestBuilder.build())
                        .add("httpResponse", Json.createObjectBuilder()
                                .add("statusCode", 200)
                                .add("headers", Json.createObjectBuilder()
                                        .add("Content-Type", Json.createArrayBuilder().add(contentType)))
                                .add("body", Json.createObjectBuilder()
                                        .add("base64Bytes", body))))
                .build();
    }

    private String getContentTypeByFileName(String extension) {
        return switch (extension) {
            case "xml" -> "application/xml;charset=UTF-8";
            case "html" -> "text/html;charset=utf-8";
            case "pdf" -> "application/pdf;charset=UTF-8";
            default -> "application/x-unknown-content-type;charset=UTF-8";
        };
    }

}

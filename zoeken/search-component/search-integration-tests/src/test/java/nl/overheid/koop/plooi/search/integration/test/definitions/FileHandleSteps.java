package nl.overheid.koop.plooi.search.integration.test.definitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import io.cucumber.java.en.And;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class FileHandleSteps {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${integration.test.source-folder}")
    private String testSourceFolder;

    @Value("${integration.solr.core.url}")
    private String solrCoreUrl;

    @And("{string} is loaded in Solr")
    public void solr_data_is_uploaded(String testData) throws IOException {
        this.logger.info("Uploading file \"{}\" to Solr instance", testData);
        File testDataFile = new File(this.testSourceFolder + "/" + testData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.solrCoreUrl + "/update?commit=true"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(Files.readAllBytes(testDataFile.toPath())))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), String.format("Cannot upload file '%s' to Solr: '%s'", testData, this.solrCoreUrl));
        } catch (IOException | InterruptedException e) {
            fail(String.format("Cannot upload file \"%s\" to Solr, Exception: %s\n", testData, e.toString()));
        }

    }

}

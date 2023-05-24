package nl.overheid.koop.plooi.dcn.api.service;

import java.io.File;
import java.io.IOException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;

public class ApiHttpTester {

    private static final String TEST_URL = "http://localhost:8080/dcn/api/5afce6ec-a4ed-4c62-a6d5-168a59247678";
    private static final File META = new File("src/test/resources/nl/overheid/koop/plooi/dcn/api/service/test_api_plooi.json");
    private static final File DOC = new File("src/test/java/nl/overheid/koop/plooi/dcn/api/service/ApiHttpTester.java");

    public static void main(String[] args) throws IOException {
        var client = HttpClientBuilder.create().build();
        var postRequest = new HttpPost(TEST_URL);
        postRequest.setEntity(MultipartEntityBuilder.create()
                .addPart(ApiServiceController.META, new FileBody(META, ContentType.APPLICATION_JSON))
                .addPart("document", new FileBody(DOC, ContentType.create("application/pdf")))
                .build());
        client.execute(postRequest);
        client.execute(new HttpDelete(TEST_URL));
    }
}

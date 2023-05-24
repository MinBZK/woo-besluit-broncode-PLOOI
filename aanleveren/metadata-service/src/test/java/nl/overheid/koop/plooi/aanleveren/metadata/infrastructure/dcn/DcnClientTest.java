package nl.overheid.koop.plooi.aanleveren.metadata.infrastructure.dcn;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DcnClientTest {
    private static final String UUID = "efe9aa58-20f4-4d2c-aca5-d86e3dda92c3";
    private MockWebServer mockWebServer;
    private DcnClient dcnClient;

    @BeforeEach
    public void set() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        this.dcnClient = new DcnClient(WebClient.builder()
                .baseUrl("http://localhost:" + mockWebServer.getPort()).build());
    }

    @Test
    void testValidUuid() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("Test message")
                .addHeader("Content-Type", "application/json"));

        dcnClient.searchMetadataByUUID(UUID);

        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/dcn/api/metadata_v1/" + UUID, request.getPath());
    }

    @Test
    void testInvalidUuid() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Test message")
                .addHeader("Content-Type", "application/json"));

        assertThrows(NotFoundException.class, () -> dcnClient.searchMetadataByUUID("1234"));

        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("GET", request.getMethod());
        assertEquals("/dcn/api/metadata_v1/1234", request.getPath());
    }
}

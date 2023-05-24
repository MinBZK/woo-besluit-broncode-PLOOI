package nl.overheid.koop.plooi.repository.client;

import java.io.File;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiTestClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final AanleverenClient aanleverenClient = new AanleverenClient(new ApiClient());

    public static void main(String[] args) throws Exception {
        var testClient = new ApiTestClient();
        testClient.aanleveringMetadata();
        testClient.postAanleveringDocument();
        testClient.wijzigMetadata();
        testClient.wijzigDocument();
        testClient.intrekking();
    }

    public void aanleveringMetadata() throws ClientException {
        this.logger.debug("Recieved: {}", this.aanleverenClient
                .createRequest("""
                        {
                          "oorzaak" : "aanlevering"
                        }
                        """,
                        "plooi-api",
                        "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e")
                .addPart("metadata_v1", """
                        { "document" : ... }
                        """.getBytes(StandardCharsets.UTF_8))
                .post());
    }

    public void postAanleveringDocument() throws ClientException {
        this.logger.debug("Recieved: {}", this.aanleverenClient
                .createRequest("""
                        {
                          "oorzaak" : "aanlevering"
                        }
                        """,
                        "plooi-api",
                        "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e")
                .addPart("document", "PDF".getBytes(StandardCharsets.UTF_8))
                .post());
    }

    public void wijzigMetadata() throws ClientException {
        this.logger.debug("Recieved: {}", this.aanleverenClient
                .createRequest("""
                        {
                          "oorzaak" : "wijziging",
                          "redenVerwijderingVervanging" : "Titel deugde niet"
                        }
                        """,
                        "plooi-api",
                        "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e")
                .addPart("metadata_v1", """
                        { "document" : .... }
                        """.getBytes(StandardCharsets.UTF_8))
                .post());
    }

    public void wijzigDocument() throws ClientException {
        this.logger.debug("Recieved: {}", this.aanleverenClient
                .createRequest("""
                        {
                          "oorzaak" : "wijziging",
                          "redenVerwijderingVervanging" : "Tiepfout aangepast"
                        }
                        """,
                        "plooi-api",
                        "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e")
                .addPart("document", TestClient.fileInputStreamSupplier(new File(
                        "src/test/java/nl/overheid/koop/plooi/repository/client/ApiTestClient.java")))
                .post());
    }

    public void intrekking() throws ClientException {
        this.logger.debug("Recieved: {}", this.aanleverenClient
                .createRequest("""
                        {
                          "oorzaak" : "intrekking",
                          "redenVerwijderingVervanging" : "Mag niet meer"
                        }
                        """,
                        "plooi-api",
                        "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e")
                .post());
    }
}

package nl.overheid.koop.plooi.repository.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.Versie.OorzaakEnum;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClient {

    static final String META_FILE = "src/test/resources/test_api_plooi.json";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ApiClient apiClient = new ApiClient();
    private final AanleverenClient aanleverenClient = new AanleverenClient(this.apiClient);
    private final PublicatieClient publicatieClient = new PublicatieClient(this.apiClient);
    private final DocumentenClient documentenClient = new DocumentenClient(this.apiClient);

    public static void main(String[] args) throws Exception {
        var testClient = new TestClient();
        testClient.postAanlevering();
        testClient.postRonl();
        testClient.getManifest();
        testClient.getMetadataFile();
        testClient.postPlooi();
        testClient.getText();
        testClient.getLatestVersion();
        testClient.getDocumentFile();
    }

    public void postAanlevering() throws ClientException, IOException {
        this.logger.debug("Recieved: {}", this.aanleverenClient
                .createRequest(
                        new Versie().oorzaak(OorzaakEnum.AANLEVERING),
                        "plooi-api",
                        "guid")
                .addPart("metadata", TestClient.fileInputStreamSupplier(new File(META_FILE)))
                .addPart("document", "".getBytes(StandardCharsets.UTF_8))
                .post());
    }

    public void postRonl() throws ClientException, IOException {
        this.logger.debug("Recieved: {}", new AanleverenClient(new ApiClient( /*SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS*/ ))
                .createRequest(
                        new Versie()
                                .oorzaak(OorzaakEnum.AANLEVERING)
                                .bestanden(List.of(
                                        new Bestand()
                                                .gepubliceerd(true)
                                                .label("pdf")
                                                .mimeType("application/pdf")
                                                .bestandsnaam("TK Bijlage 1 Voortgangsrapportage NCSA.pdf"),
                                        new Bestand()
                                                .gepubliceerd(false)
                                                .label("xml")
                                                .mimeType("application/xml")
                                                .bestandsnaam("ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e.xml"))),
                        "ronl",
                        "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e")
                .addPart("xml", TestClient.fileInputStreamSupplier(new File(
                        "../tmp/plooi-repos/0a/0a00/ronl-0a00fdbd5add2e921acfd3e2c5d3c2d84e0fbc2c/1/ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e.xml")))
                .addPart("pdf", TestClient.fileInputStreamSupplier(new File(
                        "../tmp/plooi-repos/0a/0a00/ronl-0a00fdbd5add2e921acfd3e2c5d3c2d84e0fbc2c/1/TK Bijlage 1 Voortgangsrapportage NCSA.pdf")))
                .post());
    }

    public void getManifest() {
        this.logger.debug("getManifest recieved: {}", this.publicatieClient
                .getManifest(
                        "plooi-api-962308f20ff8e0701061a86115938e62ecedb216"));
    }

    public void getMetadataFile() {
        this.logger.debug("getFileMetadata recieved: {}", this.publicatieClient
                .getVersionedFile(
                        "plooi-api-962308f20ff8e0701061a86115938e62ecedb216",
                        "1",
                        "metadata"));
    }

    public void postPlooi() throws IOException {
        this.publicatieClient
                .postPlooi(
                        "plooi-api-962308f20ff8e0701061a86115938e62ecedb216",
                        PlooiBindings.plooiBinding().unmarshalFromFile(META_FILE));
    }

    public void getText() {
        this.logger.debug("getText recieved: {}", this.documentenClient
                .getText(
                        "plooi-api-962308f20ff8e0701061a86115938e62ecedb216"));
    }

    public void getLatestVersion() {
        this.logger.debug("getLatestVersion recieved: {}", this.documentenClient
                .getLatestVersion(
                        "plooi-api-962308f20ff8e0701061a86115938e62ecedb216"));
    }

    public void getDocumentFile() throws IOException {
        this.logger.debug("getFileContent recieved: {}", new String(this.documentenClient
                .getFileContent(
                        "plooi-api-962308f20ff8e0701061a86115938e62ecedb216",
                        "document")
                .readAllBytes(), StandardCharsets.UTF_8));
    }

    public static Supplier<InputStream> fileInputStreamSupplier(File file) {
        return () -> {
            try {
                return new FileInputStream(file);
            } catch (IOException e) {
                throw ClientException.getClientException("receiving", e);
            }
        };
    }
}

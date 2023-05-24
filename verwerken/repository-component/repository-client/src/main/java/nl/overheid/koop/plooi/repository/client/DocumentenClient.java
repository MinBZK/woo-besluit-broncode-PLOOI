package nl.overheid.koop.plooi.repository.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.repository.data.InterneLabels;
import nl.overheid.koop.plooi.repository.data.PlooiManifest;

public class DocumentenClient {

    private final ApiClient client;

    public DocumentenClient(ApiClient apiClient) {
        this.client = apiClient;
    }

    public PlooiManifest getManifest(String id) {
        return new PlooiManifest(PlooiBindings.plooiBinding().unmarshalFromStream(getStream("getManifest", id, InterneLabels.MANIFEST.toString())));
    }

    public Versie getLatestVersion(String id) {
        return PlooiBindings.versionBinding().unmarshalFromStream(getStream("getLatestVersion", id, InterneLabels.VERSIE.toString()));
    }

    public Plooi getPlooi(String id) {
        return PlooiBindings.plooiBinding().unmarshalFromStream(getStream("getPlooi", id, InterneLabels.PLOOI.toString()));
    }

    public List<Relatie> getRelations(String id) {
        return PlooiBindings.relationsBinding().unmarshalFromStream(getStream("getRelations", id, InterneLabels.RELATIES.toString()));
    }

    public String getOwms(String id) {
        return getString("getOwms", getStream("getOwms", id, InterneLabels.OWMS.toString()));
    }

    public String getText(String id) {
        return getString("getText", getStream("getText", id, InterneLabels.TEXT.toString()));
    }

    public InputStream getFileContent(String id, String label) {
        return getStream("getFile", id, label);
    }

    private InputStream getStream(String func, String id, String label) {
        return this.client.sendAndVerify(func, HttpRequest.newBuilder()
                .uri(this.client.getUri("/documenten/{id}/{label}"
                        .replace("{id}", ApiClient.urlEncode(ApiClient.validate(id, ApiClient.ID, func)))
                        .replace("{label}", ApiClient.urlEncode(ApiClient.validate(label, ApiClient.LBL, func)))))
                .header("Accept", "application/json")
                .GET());
    }

    static String getString(String func, InputStream stream) {
        try (var bodyStream = stream) {
            return new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw ClientException.getClientException(func, e);
        }
    }
}

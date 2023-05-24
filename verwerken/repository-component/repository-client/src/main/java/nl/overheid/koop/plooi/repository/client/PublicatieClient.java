package nl.overheid.koop.plooi.repository.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.Versie.OorzaakEnum;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.repository.data.InterneLabels;
import nl.overheid.koop.plooi.repository.data.PlooiManifest;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicatieClient {

    public static final String LATEST = "-";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ApiClient client;

    public PublicatieClient(ApiClient apiClient) {
        this.client = apiClient;
    }

    public PlooiManifest getManifest(String id) {
        return new PlooiManifest(PlooiBindings.plooiBinding().unmarshalFromStream(getStream(id, InterneLabels.MANIFEST.toString(), "")));
    }

    public Plooi getPlooi(String id) {
        return PlooiBindings.plooiBinding().unmarshalFromStream(getStream(id, InterneLabels.PLOOI.toString(), ""));
    }

    public Versie getLatestVersion(String id) {
        return PlooiBindings.versionBinding().unmarshalFromStream(getStream(id, InterneLabels.VERSIE.toString(), ""));
    }

    public List<Relatie> getRelations(String id, RelationType... types) {
        return PlooiBindings.relationsBinding()
                .unmarshalFromStream(getStream(id, InterneLabels.RELATIES.toString(), types == null
                        ? ""
                        : Stream.of(types)
                                .map(RelationType::getUri)
                                .map(ApiClient::urlEncode)
                                .map(type -> "types=" + type)
                                .collect(Collectors.joining("&"))));
    }

    public String getText(String id) {
        return DocumentenClient.getString("getText", getStream(id, InterneLabels.TEXT.toString(), ""));
    }

    private InputStream getStream(String id, String label, String queryParams) {
        var func = "getMetaFile";
        return this.client.sendAndVerify(func, HttpRequest.newBuilder()
                .uri(this.client.getUri("/publicatie/{id}/{label}?{queryParams}"
                        .replace("{id}", ApiClient.urlEncode(ApiClient.validate(id, ApiClient.ID, func)))
                        .replace("{label}", ApiClient.urlEncode(ApiClient.validate(label, ApiClient.LBL, func)))
                        .replace("{queryParams}", queryParams)))
                .GET());
    }

    public InputStream getVersionedFile(String id, String versieNummer, String label) {
        var func = "getVersionedFile";
        return this.client.sendAndVerify(func, HttpRequest.newBuilder()
                .uri(this.client.getUri("/publicatie/{id}/{versieNummer}/{label}"
                        .replace("{id}", ApiClient.validate(id, ApiClient.ID, func))
                        .replace("{versieNummer}", ApiClient.validate(versieNummer, ApiClient.NR, func))
                        .replace("{label}", ApiClient.validate(label, ApiClient.LBL, func)))));
    }

    public void delete(String id, OorzaakEnum oorzaak, String reden, String... versieNummer) {
        var func = "delete";
        try (var response = this.client.sendAndVerify(oorzaak.toString(), HttpRequest.newBuilder()
                .uri(this.client.getUri("/publicatie/{id}?"
                        .replace("{id}", ApiClient.urlEncode(ApiClient.validate(id, ApiClient.ID, oorzaak.toString())))
                        .concat(versieNummer.length == 0 || StringUtils.isBlank(versieNummer[0]) ? "" : ("versieNummer=" + versieNummer[0]))
                        .concat(StringUtils.isBlank(reden) ? "" : ("&reden=" + ApiClient.urlEncode(reden)))))
                .method(OorzaakEnum.INTREKKING == oorzaak ? "DELETE" : "POST", HttpRequest.BodyPublishers.noBody()))) {
            this.logger.debug("Handled {} of {}", oorzaak, id);
        } catch (IOException e) {
            throw ClientException.getClientException(func, e);
        }
    }

    public void postPlooi(String id, Plooi plooi) {
        doPostMetadata(id, InterneLabels.PLOOI, PlooiBindings.plooiBinding().marshalToString(plooi), "");
    }

    public void postRelations(String id, String stage, List<Relatie> relations) {
        if (relations != null && !relations.isEmpty()) {
            doPostMetadata(id, InterneLabels.RELATIES, PlooiBindings.relationsBinding().marshalToString(relations), stage);
        }
    }

    public void postText(String id, String text) {
        doPostMetadata(id, InterneLabels.TEXT, text, "");
    }

    private void doPostMetadata(String id, InterneLabels label, String body, String stage) {
        String func = "postMetadata";
        try (var response = this.client.sendAndVerify(func, HttpRequest.newBuilder()
                .uri(this.client.getUri("/publicatie/{id}/{label}/{stage}"
                        .replace("{id}", ApiClient.urlEncode(ApiClient.validate(id, ApiClient.ID, func)))
                        .replace("{label}", ApiClient.urlEncode(ApiClient.validate(label.toString(), ApiClient.LBL, func)))
                        .replace("{stage}", ApiClient.urlEncode(stage))))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)))) {
            this.logger.debug("Posted {} for {}", label, id);
        } catch (IOException e) {
            throw ClientException.getClientException(func, e);
        }
    }
}

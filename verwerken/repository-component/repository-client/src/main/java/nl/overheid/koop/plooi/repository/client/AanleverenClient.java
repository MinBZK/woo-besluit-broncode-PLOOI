package nl.overheid.koop.plooi.repository.client;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.repository.data.InterneLabels;
import org.apache.commons.lang3.StringUtils;

public class AanleverenClient {

    private final ApiClient client;

    public AanleverenClient(ApiClient apiClient) {
        this.client = apiClient;
    }

    public Request createRequest(Versie versie, String sourceLabel, String... extIds) {
        return new Request(null, versie, sourceLabel, extIds);
    }

    public Request createRequest(String versieJson, String sourceLabel, String... extIds) {
        return new Request(versieJson, null, sourceLabel, extIds);
    }

    private static final String FUNC = "postVersion";
    private static final String NEWLINE = "\r\n";
    private static final String SEP = "--";
    private static final String QUOTE = "\"";

    private static final String MIME_MULTIPART = "multipart/form-data;boundary=";
    private static final String MIME_JSON = "application/json";
    private static final String MIME_UNKNOWN = "application/octet-stream";

    private static final String CONTENT_DISP = NEWLINE + "Content-Disposition: form-data; name=";
    private static final String FILENAME = "; filename=";
    private static final String CONTENT_TYPE = NEWLINE + "Content-Type: ";
    private static final String ENCODING = NEWLINE + "Content-Transfer-Encoding: binary";

    public class Request {

        private final List<BodyPublisher> contents = new ArrayList<>();
        private final HttpRequest.Builder requestBuilder;
        private final Map<String, Bestand> bestanden;
        private final String boundarySeparator;

        private Request(String versieJson, Versie versie, String sourceLabel, String... extIds) {
            if (versie == null && StringUtils.isBlank(versieJson)) {
                throw ApiClient.illegalArg("versie", AanleverenClient.FUNC);
            } else if (StringUtils.isBlank(sourceLabel)) {
                throw ApiClient.illegalArg("sourceLabel", AanleverenClient.FUNC);
            } else if (StringUtils.isAnyBlank(extIds) || extIds.length == 0) {
                throw ApiClient.illegalArg("extIds", AanleverenClient.FUNC);
            }
            try {
                var boundary = new BigInteger(256, new Random()).toString();
                this.boundarySeparator = SEP + boundary;
                this.requestBuilder = HttpRequest.newBuilder()
                        .uri(AanleverenClient.this.client
                                .getUri("/aanleveren/" + sourceLabel + "/"
                                        + Arrays.stream(extIds).map(id -> id.replace("/", "%2F")).map(ApiClient::urlEncode).collect(Collectors.joining("/"))))
                        .header("Content-Type", MIME_MULTIPART + boundary)
                        .header("Accept", MIME_JSON);
                var bestandenList = (versie == null ? PlooiBindings.versionBinding().unmarshalFromString(versieJson) : versie).getBestanden();
                this.bestanden = bestandenList == null ? Map.of() : bestandenList.stream().collect(Collectors.toMap(Bestand::getLabel, Function.identity()));
                addSeparator(InterneLabels.VERSIE.toString(), null, MIME_JSON, false)
                        .add(BodyPublishers.ofString(versieJson == null ? PlooiBindings.versionBinding().marshalToString(versie) : versieJson));
            } catch (RuntimeException e) {
                throw ClientException.getClientException(FUNC, e);
            }
        }

        public Request addPart(String label, String content) {
            return doAddPart(label, false, BodyPublishers.ofString(content));
        }

        public Request addPart(String label, byte[] content) {
            return doAddPart(label, true, BodyPublishers.ofByteArray(content));
        }

        public Request addPart(String label, Supplier<? extends InputStream> contentSupplier) {
            return doAddPart(label, true, BodyPublishers.ofInputStream(contentSupplier));
        }

        private Request doAddPart(String label, boolean binary, BodyPublisher contentPublisher) {
            var file = this.bestanden.get(label);
            addSeparator(label, file == null ? null : file.getBestandsnaam(), file == null ? null : file.getMimeType(), binary).add(contentPublisher);
            return this;
        }

        private List<BodyPublisher> addSeparator(String label, String filename, String contentType, boolean binary) {
            var separator = this.contents.isEmpty() ? new StringBuilder() : new StringBuilder(NEWLINE);
            separator.append(this.boundarySeparator).append(CONTENT_DISP).append(QUOTE).append(label).append(QUOTE);
            if (StringUtils.isNotBlank(filename)) {
                separator.append(FILENAME).append(QUOTE).append(filename).append(QUOTE);
            }
            separator.append(CONTENT_TYPE).append(StringUtils.defaultIfBlank(contentType, MIME_UNKNOWN));
            if (binary) {
                separator.append(ENCODING);
            }
            separator.append(NEWLINE).append(NEWLINE);
            this.contents.add(BodyPublishers.ofString(separator.toString()));
            return this.contents;
        }

        public HttpRequest.Builder build() {
            this.contents.add(BodyPublishers.ofString(NEWLINE + this.boundarySeparator + SEP + NEWLINE));
            return this.requestBuilder.POST(BodyPublishers.concat(this.contents.toArray(new BodyPublisher[this.contents.size()])));
        }

        public Optional<Plooi> post() {
            var response = AanleverenClient.this.client.send(FUNC, build());
            if (response.statusCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                return Optional.empty();
            }
            try (var stream = AanleverenClient.this.client.verify(FUNC, response).body()) {
                return Optional.of(PlooiBindings.plooiBinding().unmarshalFromStream(stream));
            } catch (IOException e) {
                throw ClientException.getClientException(FUNC, e);
            }
        }
    }
}

package nl.overheid.koop.plooi.search.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HttpClient.Builder builder;
    private final String baseUrl;

    public ApiClient(String base) {
        this(HttpClient.newBuilder(), base);
    }

    private ApiClient(HttpClient.Builder builder, String base) {
        this.builder = builder;
        this.baseUrl = StringUtils.defaultIfBlank(base, "http://localhost:8030");
        this.logger.debug("Created Search Service client for {}", this.baseUrl);
    }

    URI getUri(String path) {
        return URI.create(this.baseUrl + path);
    }

    InputStream sendAndVerify(String method, HttpRequest.Builder requestBuilder) {
        return verify(method, send(method, requestBuilder)).body();
    }

    HttpResponse<InputStream> send(String method, HttpRequest.Builder requestBuilder) {
        try {
            var req = requestBuilder.build();
            this.logger.debug("Sending {} request to {}", req.method(), req.uri());
            return this.builder.build().send(req, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException e) {
            throw ClientException.getClientException(method, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw ClientException.getClientException(method, e);
        }
    }

    HttpResponse<InputStream> verify(String method, HttpResponse<InputStream> response) {
        try {
            if (response.statusCode() / 100 == 2) {
                return response;
            } else {
                throw ClientException.getClientException(method, response);
            }
        } catch (IOException e) {
            throw ClientException.getClientException(method, e);
        }
    }

    static String validate(String val, String param, String func) {
        if (StringUtils.isBlank(val)) {
            throw illegalArg(param, func);
        } else {
            return val;
        }
    }

    static IllegalArgumentException illegalArg(String param, String func) {
        return new IllegalArgumentException("Parameter '" + param + "' is required when calling " + func);
    }

    private static final Pattern SPACE_PTTRN = Pattern.compile("\\+");
    private static final String ESCAPED_SPACE = "%20";

    static String urlEncode(String s) {
        return SPACE_PTTRN.matcher(URLEncoder.encode(s, UTF_8)).replaceAll(ESCAPED_SPACE);
    }
}

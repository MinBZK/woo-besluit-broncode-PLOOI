package nl.overheid.koop.plooi.registration.client;

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

public class ApiClient {

    private static final String DEFAULT_BASE_PATH = "http://localhost:8020";
    private final HttpClient.Builder builder;
    private final String baseUrl;

    public ApiClient(HttpClient.Builder builder, String base) {
        this.builder = builder;
        this.baseUrl = StringUtils.defaultIfBlank(base, DEFAULT_BASE_PATH);
    }

    public URI getUri(String path) {
        return URI.create(this.baseUrl + path);
    }

    InputStream send(String method, HttpRequest.Builder requestBuilder) {
        try {
            var response = this.builder.build().send(requestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() / 100 == 2) {
                return response.body();
            } else {
                throw ClientException.getClientException(method, response);
            }
        } catch (IOException e) {
            throw new ClientException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ClientException(e);
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

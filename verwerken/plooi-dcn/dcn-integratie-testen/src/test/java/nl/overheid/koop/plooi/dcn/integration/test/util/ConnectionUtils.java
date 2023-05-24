package nl.overheid.koop.plooi.dcn.integration.test.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionUtils.class);

    public static HttpResponse<String> getHttpPutResponse(String url, String body) {
        return exchange(buildHttpRequest(url, "PUT", HttpRequest.BodyPublishers.ofString(body)));
    }

    public static HttpResponse<String> getHttpPostResponse(String url, String body, String... headers) {
        return exchange(buildHttpRequest(url, "POST", HttpRequest.BodyPublishers.ofString(body), headers));
    }

    public static HttpResponse<String> getHttpGetResponse(String url, String... headers) {
        return exchange(buildHttpRequest(url, "GET", HttpRequest.BodyPublishers.noBody(), headers));
    }

    private static HttpRequest buildHttpRequest(String url, String method, HttpRequest.BodyPublisher bodyPublisher, String... headers) {
        try {
            var reqBuilder = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .method(method, bodyPublisher);
            if (headers.length > 0) {
                reqBuilder.headers(headers);
            }
            return reqBuilder.build();
        } catch (URISyntaxException e) {
            throw new AssertionFailedError("Cannot parse url " + url, e);
        }
    }

    private static HttpResponse<String> exchange(HttpRequest request) {
        try {
            var response = HttpClient.newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            logger.trace("    {} response on {}", response.statusCode(), request.uri());
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException e) {
            logger.trace("    IOException on {}: {}", request.uri(), e.getMessage());
            throw new AssertionFailedError("Cannot call url " + request.uri(), e);
        }
    }
}

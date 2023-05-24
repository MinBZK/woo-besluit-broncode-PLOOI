package nl.overheid.koop.plooi.repository.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;

public class ClientException extends RuntimeException {

    private static final long serialVersionUID = -8104159937379294519L;

    private final int statusCode;

    public int getStatusCode() {
        return this.statusCode;
    }

    public static ClientException getClientException(String operationId, HttpResponse<InputStream> response) throws IOException {
        String body = response.body() == null ? null : new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
        return new ClientException(response.statusCode(), formatExceptionMessage(operationId, response.statusCode(), body));
    }

    public static ClientException getClientException(String operationId, Exception exception) {
        return exception instanceof ClientException clientException
                ? clientException
                : new ClientException(0, formatExceptionMessage(operationId, 0, exception.getMessage()));
    }

    private ClientException(int status, String message) {
        super(message);
        this.statusCode = status;
    }

    private static String formatExceptionMessage(String operationId, int statusCode, String body) {
        var message = new StringBuilder().append(operationId).append(" call failed");
        if (statusCode > 0) {
            message.append(" with: ").append(statusCode);
        }
        return message.append(" - ").append(StringUtils.defaultIfBlank(body, "[no body]")).toString();
    }
}

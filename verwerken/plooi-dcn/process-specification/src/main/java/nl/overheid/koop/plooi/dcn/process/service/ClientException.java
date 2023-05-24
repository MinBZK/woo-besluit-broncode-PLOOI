package nl.overheid.koop.plooi.dcn.process.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

public class ClientException extends RuntimeException {

    private static final long serialVersionUID = 5316771373682116180L;
    private final int statusCode;

    public int getStatusCode() {
        return this.statusCode;
    }

    public static ClientException getClientException(String operationId, Response response) {
        String body = null;
        if (response.getEntity() != null) {
            try (var bodyStream = (InputStream) response.getEntity()) {
                body = new String((bodyStream).readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                // Too bad...
            }
        }
        return new ClientException(response.getStatus(), formatExceptionMessage(operationId, response.getStatus(), body));
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

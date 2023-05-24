package nl.overheid.koop.plooi.registration.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

public class ClientException extends RuntimeException {

    private static final long serialVersionUID = -8104159937379294519L;

    public static ClientException getClientException(String operationId, HttpResponse<InputStream> response) throws IOException {
        String body = response.body() == null ? null : new String(response.body().readAllBytes());
        String message = formatExceptionMessage(operationId, response.statusCode(), body);
        return new ClientException(message);
    }

    private static String formatExceptionMessage(String operationId, int statusCode, String body) {
        if (body == null || body.isEmpty()) {
            body = "[no body]";
        }
        return operationId + " call failed with: " + statusCode + " - " + body;
    }

    public ClientException() {
        super();
    }

    public ClientException(Throwable throwable) {
        super(throwable);
    }

    public ClientException(String message) {
        super(message);
    }
}

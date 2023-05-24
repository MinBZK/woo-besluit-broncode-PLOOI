package nl.overheid.koop.plooi.dcn.integration.test.client.actuator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JolokiaArguments {

    private final String route;
    private final String body;
    private final Map<String, Object> headers = new HashMap<String, Object>();

    public JolokiaArguments(String route, String body) {
        this.route = route == null ? "" : route;
        this.body = body == null ? "" : body;
    }

    public String getRoute() {
        return this.route;
    }

    public String getBody() {
        return this.body;
    }

    public void addHeader(String headerName, Object headerValue) {
        this.headers.put(headerName, headerValue);
    }

    public void addHeaders(Map<String, Object> headers) {
        this.headers.putAll(headers);
    }

    public List<Object> convertToListOfObject() {
        return List.of(this.route, this.body, this.headers);
    }
}

package nl.overheid.koop.plooi.dcn.integration.test.client.actuator;

import java.util.Map;

public class JolokiaExecuteRequest extends JolokiaRequest {
    
    private static final String EXECUTE_TYPE = "exec";
    private static final String SEND_BODY_AND_HEADERS_OPERATION = "sendBodyAndHeaders(java.lang.String, java.lang.Object, java.util.Map)";
    
    public JolokiaExecuteRequest(String route, String body, Map<String, Object> headers) {
        super.type = EXECUTE_TYPE;
        super.operation = SEND_BODY_AND_HEADERS_OPERATION;
        
        JolokiaArguments arguments = new JolokiaArguments(route, body);
        
        if (headers!= null && !headers.isEmpty()) {
            arguments.addHeaders(headers);
        }
        
        super.setArguments(arguments.convertToListOfObject());
    }
    
    public JolokiaExecuteRequest(String route, String body, String header, String value) {
        super.type = EXECUTE_TYPE;
        super.operation = SEND_BODY_AND_HEADERS_OPERATION;
        
        JolokiaArguments arguments = new JolokiaArguments(route, body);
        
        if (header != null) {
            arguments.addHeader(header, value);
        }
        
        super.setArguments(arguments.convertToListOfObject());
    }
    
}

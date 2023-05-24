package nl.overheid.koop.plooi.dcn.route.common.httpexception;

import org.apache.camel.Exchange;

public abstract class AbstractHttpOperationFailedException extends Exception implements HttpOperationResponse {

    private final String uri;
    private final int statusCode;
    private final String statusText;

    protected AbstractHttpOperationFailedException(Exchange exchange) {
        super(buildExceptionMessage(exchange));
        this.uri = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        this.statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        this.statusText = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_TEXT, String.class);
    }

    @Override
    public String getUri() {
        return this.uri;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getStatusText() {
        return this.statusText;
    }

    public static String buildExceptionMessage(Exchange exchange) {
        return String.format("Http Response Status [URI: %s, statusCode: %s, statusText: %s]",
                                exchange.getIn().getHeader(Exchange.HTTP_URI, String.class),
                                exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class),
                                exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_TEXT, String.class)
                );
    }

}

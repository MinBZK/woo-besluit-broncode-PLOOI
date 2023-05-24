package nl.overheid.koop.plooi.dcn.route.common.httpexception;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class HttpOperationFailedProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws AbstractHttpOperationFailedException {
        int httpResponseCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        if (httpResponseCode >= 500 || httpResponseCode == 429 || httpResponseCode == 425) {
            throw new HttpRecoverableException(exchange); // redelivery marked
        } else if (httpResponseCode >= 400) {
            throw new HttpNotRecoverableException(exchange); // no redelivery
        }
    }
}

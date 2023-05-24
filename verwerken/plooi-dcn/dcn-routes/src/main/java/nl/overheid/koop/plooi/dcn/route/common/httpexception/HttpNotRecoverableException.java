package nl.overheid.koop.plooi.dcn.route.common.httpexception;

import org.apache.camel.Exchange;

public class HttpNotRecoverableException extends AbstractHttpOperationFailedException {

	private static final long serialVersionUID = 2143099857607752372L;

	public HttpNotRecoverableException(Exchange exchange) {
		super(exchange);
	}

}

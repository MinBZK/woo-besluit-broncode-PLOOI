package nl.overheid.koop.plooi.dcn.route.common.httpexception;

import org.apache.camel.Exchange;

public class HttpRecoverableException extends AbstractHttpOperationFailedException {

	private static final long serialVersionUID = 386611458665532890L;

	public HttpRecoverableException(Exchange exchange) {
		super(exchange);
	}

}

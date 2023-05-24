package nl.overheid.koop.plooi.dcn.route.common.httpexception;

public interface HttpOperationResponse {

	String getUri();
	int getStatusCode();
	String getStatusText();

}

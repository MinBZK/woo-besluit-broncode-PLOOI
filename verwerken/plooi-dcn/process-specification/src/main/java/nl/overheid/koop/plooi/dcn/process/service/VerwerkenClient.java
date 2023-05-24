package nl.overheid.koop.plooi.dcn.process.service;

import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class VerwerkenClient implements VerwerkenApi {

    private final String baseUrl;
    private final Client client = ClientBuilder.newClient();

    public VerwerkenClient() {
        this.baseUrl = StringUtils.defaultIfBlank(System.getenv("PROCESS_URL"), "http://localhost:8080");
    }

    @Override
    public void process(VerwerkingActies actie, String procesId, List<String> dcnIds) {
        try {
            var response = this.client
                    .target(this.baseUrl)
                    .path("verwerken")
                    .path(actie.name())
                    .path(procesId)
                    .request()
                    .post(Entity.entity(dcnIds, MediaType.APPLICATION_JSON));
            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                throw ClientException.getClientException("verwerken/" + actie, response);
            }
        } catch (RuntimeException e) {
            throw ClientException.getClientException("verwerken/" + actie, e);
        }
    }

    @Override
    public String toString() {
        return new StringBuilder().append(getClass().getSimpleName()).append(" for ").append(this.baseUrl).toString();
    }
}

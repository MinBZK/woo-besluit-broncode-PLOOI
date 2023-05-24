package nl.overheid.koop.plooi.search.integration.test.definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import nl.overheid.koop.plooi.test.util.Retry;
import org.springframework.beans.factory.annotation.Value;

public class HealthSteps {

    @Value("${integration.solr.core.url}")
    public String solrCoreUrl;

    @Value("${integration.search.actuator.base.url}")
    public String searchActuatorBaseUrl;

    @Given("solr is up and running")
    public void solr_is_up_and_running() {
        Retry.waitForSuccess(this.solrCoreUrl + "/admin/ping");
    }

    @And("search service is up and running")
    public void search_service_is_up_and_running() {
        Retry.waitForSuccess(this.searchActuatorBaseUrl + "/actuator/health/ping");
    }

}

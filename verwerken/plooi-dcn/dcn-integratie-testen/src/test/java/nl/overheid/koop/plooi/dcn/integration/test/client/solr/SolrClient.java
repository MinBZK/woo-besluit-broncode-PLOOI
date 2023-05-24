package nl.overheid.koop.plooi.dcn.integration.test.client.solr;

import nl.overheid.koop.plooi.dcn.integration.test.util.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * TODO Once we have a search service, we'll check health on that service and move query calls in here.
 */
public class SolrClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${integration.solr-core-url}")
    public String solrCoreUrl;

    private boolean solrUp;

    /**
     * @return True if we have seen Solr up and running. False if it was already healthy, so we can assume everything else
     *         is healthy too
     */
    public boolean waitSolrIsHealthy() {
        this.logger.debug("Checking if Solr is healthy on {}", this.solrCoreUrl);
        if (this.solrUp) {
            return true;
        } else {
            Retry.waitForSuccess(this.solrCoreUrl + "/admin/ping");
            this.solrUp = true; // No need to try this again once we've seen Solr is healthy
            return false;
        }
    }
}

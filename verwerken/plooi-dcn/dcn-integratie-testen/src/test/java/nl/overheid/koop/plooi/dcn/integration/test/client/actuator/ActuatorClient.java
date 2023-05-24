package nl.overheid.koop.plooi.dcn.integration.test.client.actuator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import nl.overheid.koop.plooi.dcn.integration.test.util.ConnectionUtils;
import nl.overheid.koop.plooi.dcn.integration.test.util.Retry;
import nl.overheid.koop.plooi.dcn.route.common.Routing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class ActuatorClient {

    private static final int AFTER_WAIT = 1_000;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${integration.spring-actuator-root-url}/actuator")
    public String actuatorRootUrl;

    @Autowired
    private Gson gson;

    private boolean dcnUp;

    /**
     * @return True if we have seen DCN up and running. False if it was already healthy, so we can assume everything else is
     *         healthy too
     */
    public boolean waitDcnIsHealthy() {
        this.logger.debug("Checking if DCN is healthy on {}", this.actuatorRootUrl);
        if (this.dcnUp) {
            return false;
        } else {
            Retry.waitForSuccess(this.actuatorRootUrl + "/health");
            this.dcnUp = true; // No need to try this again once we've seen DCN is healthy
            Retry.sleep(AFTER_WAIT);
            return true;
        }
    }

    public void checkRouteIsUp(String context, String routeType) {
        this.logger.debug("Checking whether {} route for {} is up on {}", context, routeType, this.actuatorRootUrl);

        var routeName = switch (routeType) {
            case "ingress" -> Routing.ingress(context);
            case "process" -> Routing.process(context);
            default -> fail("Not implemented route type:" + routeType);
        };

        assertNotNull(routeName, "routeName does not exist");

        Integer statusResponse = JsonPath.parse(ConnectionUtils
                .getHttpGetResponse(
                        String.format("%s/jolokia/read/org.apache.camel:context=*,type=routes,name=%%22%s%%22", this.actuatorRootUrl, routeName))
                .body()).read("$['status']");
        assertNotNull(statusResponse);
        assertEquals(200, statusResponse);
    }

    public void triggerIngressRoute(String context) {
        this.logger.debug("Triggering INGRESS route for {} on {}", context, this.actuatorRootUrl);
        JolokiaExecuteRequest jolokiaRequest = new JolokiaExecuteRequest(Routing.internal(Routing.ingress(context)), null, null);
        String responseBody = ConnectionUtils.getHttpPostResponse(this.actuatorRootUrl + "/hawtio/jolokia", this.gson.toJson(jolokiaRequest)).body();
        assertNotNull(responseBody);
        Integer statusResponse = JsonPath.parse(responseBody).read("$['status']");
        assertEquals(200, statusResponse);
    }
}

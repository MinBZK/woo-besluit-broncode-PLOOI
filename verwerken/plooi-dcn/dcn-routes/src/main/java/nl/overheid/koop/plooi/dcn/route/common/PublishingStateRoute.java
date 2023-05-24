package nl.overheid.koop.plooi.dcn.route.common;

import nl.overheid.koop.dcn.publishingstate.PublishingStateService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Routes for performing update on publish state table.
 * <p>
 */
@Component
public class PublishingStateRoute extends RouteBuilder {

    static final String JOB_NAME = "publishingstate";

    @Override
    public void configure() {
        from(Routing.scheduleEndpoint(JOB_NAME)).routeId(Routing.schedule(JOB_NAME))
                .bean(PublishingStateService.class);
    }
}

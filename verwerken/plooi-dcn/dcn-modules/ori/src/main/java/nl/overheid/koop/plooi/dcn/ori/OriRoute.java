package nl.overheid.koop.plooi.dcn.ori;

import nl.overheid.koop.plooi.dcn.route.common.ErrorHandling;
import nl.overheid.koop.plooi.dcn.route.common.Routing;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel routes for processing ORI content are not (yet) implemented.
 */
@Component
public class OriRoute extends RouteBuilder {

    static final String SOURCE_LABEL = "ori";

    @Override
    public void configure() {
        new ErrorHandling(this).configure();

        from(Routing.external(Routing.process(SOURCE_LABEL))).routeId(Routing.process(SOURCE_LABEL))
                .log(LoggingLevel.DEBUG, this.log, "ORI processing is not implemented");
    }
}

package nl.overheid.koop.plooi.dcn.route.common;

import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.dcn.repository.store.PlooiManifestReading;
import nl.overheid.koop.plooi.dcn.route.prep.IngressExecutionPrep;
import nl.overheid.koop.plooi.dcn.route.prep.StagePrep;
import nl.overheid.koop.plooi.dcn.route.processor.IdProcessor;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Contains utility method that build standard route names.
 */
public final class Routing {

    private static final String SINGLE_PROCESS_ENVVAR = "dcn.route.singleprocess";
    private static final boolean SINGLE_PROCESS = Boolean.getBoolean(SINGLE_PROCESS_ENVVAR);

    private Routing() {
    }

    /** Create a (Quartz) schedule route id from the source label. */
    public static String schedule(String label) {
        return label.concat("-quartz");
    }

    /** Create a (Quartz) schedule ingress endpoint from the source label, using the dcn.schedule.SRC property. */
    public static String scheduleEndpoint(String label) {
        return String.format("quartz:ingress/%1$s?cron={{dcn.schedule.%1$s}}", label);
    }

    /** Create an ingress route id from the source label. */
    public static String ingress(String label) {
        return label.concat("-ingress");
    }

    /** Create an process route id from the source label. */
    public static String process(String label) {
        return label.concat("-process");
    }

    /** Creates an internal endpoint from a route id, which is implemented as a Camel direct endpoint. */
    public static String internal(String route) {
        return "direct:".concat(route);
    }

    /**
     * Creates an external endpoint from a route id, which is implemented as a Camel seda endpoint.
     * <p>
     * When the {@value #SINGLE_PROCESS_ENVVAR} is set however, a direct endpoint is created. This makes debugging and
     * tracing durng development easier since everything runs in a single thread.
     */
    public static String external(String route) {
        return SINGLE_PROCESS ? internal(route) : "seda:".concat(route);
    }

    /** Convenience method which creates a Processor that reads back an input PlooiEnvelope from the repository. */
    public static Processor restoreFromManifest(PublicatieClient publicatieClient) {
        return new Processor() {

            private final Processor idProcessor = new IdProcessor(new PlooiManifestReading(publicatieClient));

            @Override
            public void process(Exchange exchange) throws Exception {
                this.idProcessor.process(exchange);
                exchange.getIn()
                        .getMandatoryBody(PlooiEnvelope.class)
                        .stage(exchange.getIn().getHeader(StagePrep.DCN_STAGE_HEADER, Stage.class))
                        .procesId(exchange.getIn().getHeader(IngressExecutionPrep.DCN_EXECUTION_HEADER, String.class));
            }
        };
    }
}

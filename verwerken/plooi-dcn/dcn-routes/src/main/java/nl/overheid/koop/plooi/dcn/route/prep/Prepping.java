package nl.overheid.koop.plooi.dcn.route.prep;

import java.util.Map;
import nl.overheid.koop.plooi.dcn.route.processor.PreppingProcessor;

/**
 * Prepares a DCN route, setting some message headers needed in there. Implementations are executed in a route by
 * {@link PreppingProcessor}.
 */
public interface Prepping {

    String DCN_SINCE_HEADER = "dcn_sincedate";

    /**
     * Prepares Camel message headers for the route
     *
     * @param  headers Existing headers
     * @return         the prepared headers
     */
    Map<String, Object> prepare(Map<String, Object> headers);
}

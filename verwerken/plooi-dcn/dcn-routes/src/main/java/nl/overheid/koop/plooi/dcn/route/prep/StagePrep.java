package nl.overheid.koop.plooi.dcn.route.prep;

import java.util.Map;
import nl.overheid.koop.plooi.dcn.model.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets the DCN stage in the {@value #DCN_STAGE_HEADER} header, to make it available to
 * {@link nl.overheid.koop.plooi.dcn.route.processor.register register} processors.
 */
public class StagePrep implements Prepping {

    public static final String DCN_STAGE_HEADER = "dcn_stage";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Stage stage;

    public StagePrep(Stage stg) {
        this.stage = stg;
    }

    @Override
    public Map<String, Object> prepare(Map<String, Object> headers) {
        this.logger.trace("Initiated Stage {}", this.stage);
        headers.put(DCN_STAGE_HEADER, this.stage);
        return headers;
    }
}

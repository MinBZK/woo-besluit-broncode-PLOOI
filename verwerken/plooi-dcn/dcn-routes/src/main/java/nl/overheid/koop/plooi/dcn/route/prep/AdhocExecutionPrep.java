package nl.overheid.koop.plooi.dcn.route.prep;

import java.util.Map;
import nl.overheid.koop.plooi.dcn.model.TriggerType;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates an ad hoc {@link nl.overheid.koop.plooi.registration.model.Proces}, if the headers do no yet refer an
 * execution, and adds its identifier to the headers.
 */
public class AdhocExecutionPrep implements Prepping {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String source;
    private final TriggerType triggerType;
    private final String trigger;
    private final RegistrationClient registrationClient;

    public AdhocExecutionPrep(RegistrationClient regClient, TriggerType trgrTp) {
        this(regClient, "", trgrTp, "ID dropped on queue");
    }

    public AdhocExecutionPrep(RegistrationClient regClient, String src, TriggerType trgrTp, String trgr) {
        this.source = StringUtils.defaultIfBlank(src, "");
        this.registrationClient = regClient;
        this.triggerType = trgrTp;
        this.trigger = trgr;
    }

    @Override
    public Map<String, Object> prepare(Map<String, Object> headers) {
        headers.computeIfAbsent(IngressExecutionPrep.DCN_EXECUTION_HEADER, key -> {
            var execution = this.registrationClient.createProces(this.source, this.triggerType.toString(), this.trigger);
            this.logger.trace("Prepared {}", execution);
            return execution.getId();
        });
        return headers;
    }
}

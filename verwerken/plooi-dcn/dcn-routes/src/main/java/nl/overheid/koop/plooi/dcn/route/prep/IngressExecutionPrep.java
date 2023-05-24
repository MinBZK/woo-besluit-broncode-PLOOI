package nl.overheid.koop.plooi.dcn.route.prep;

import java.util.Map;
import nl.overheid.koop.plooi.dcn.model.TriggerType;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates an ingress {@link nl.overheid.koop.plooi.registration.model.Proces} and adds its identifier to the headers.
 */
public class IngressExecutionPrep implements Prepping {

    public static final String DCN_EXECUTION_HEADER = "dcn_executionid";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RegistrationClient registrationClient;
    private final String source;
    private final String triggerHeader;

    public IngressExecutionPrep(RegistrationClient regClient, String src, String trgrHdr) {
        this.registrationClient = regClient;
        this.source = src;
        this.triggerHeader = trgrHdr;
    }

    @Override
    public Map<String, Object> prepare(Map<String, Object> headers) {
        var proces = this.registrationClient.createProces(this.source, TriggerType.INGRESS.toString(),
                headers.getOrDefault(this.triggerHeader, "unknown").toString());
        headers.put(DCN_EXECUTION_HEADER, proces.getId());
        this.logger.info("Starting {}", proces);
        return headers;
    }
}

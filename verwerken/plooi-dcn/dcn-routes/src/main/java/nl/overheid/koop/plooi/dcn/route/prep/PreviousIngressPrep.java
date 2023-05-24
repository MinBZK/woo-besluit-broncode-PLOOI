package nl.overheid.koop.plooi.dcn.route.prep;

import java.util.Map;
import java.util.function.Predicate;
import nl.overheid.koop.plooi.dcn.model.TriggerType;
import nl.overheid.koop.plooi.registration.client.RegistrationClient;
import nl.overheid.koop.plooi.registration.model.Proces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

/**
 * Checks the previous ingress and sets its timestamp in the {@link Prepping#DCN_SINCE_HEADER} header. Also, an optional
 * predicate can be provided to set the previous ingress's id in the headers. This construct can be used to group events
 * for documents that "do not arrive together".
 */
public class PreviousIngressPrep implements Prepping {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String source;
    private final String triggerTypeStr;
    private final Predicate<Proces> keepLastExecutionId;
    private final RegistrationClient registrationClient;

    public PreviousIngressPrep(String src, RegistrationClient regClient) {
        this(regClient, src, null);
    }

    public PreviousIngressPrep(RegistrationClient regClient, String src, Predicate<Proces> keepLastId) {
        this(regClient, src, TriggerType.INGRESS, keepLastId);
    }

    public PreviousIngressPrep(RegistrationClient regClient, String src, TriggerType triggerType, Predicate<Proces> keepLastId) {
        this.registrationClient = regClient;
        this.source = src;
        this.triggerTypeStr = triggerType.toString();
        this.keepLastExecutionId = keepLastId;
    }

    @Override
    public Map<String, Object> prepare(Map<String, Object> headers) {
        var previousIngress = this.registrationClient.getLastSuccessful(PageRequest.of(0, 20), this.source, this.triggerTypeStr);
        if (previousIngress == null) {
            this.logger.trace("No previous {} ingress", this.source);
        } else {
            headers.putIfAbsent(DCN_SINCE_HEADER, previousIngress.getTimeCreated());
            if (this.keepLastExecutionId != null && this.keepLastExecutionId.test(previousIngress)) {
                headers.put(IngressExecutionPrep.DCN_EXECUTION_HEADER, previousIngress.getId());
            }
            this.logger.trace("Last ingress was {}", previousIngress);
        }
        return headers;
    }
}

package nl.overheid.koop.plooi.dcn.component.types;

import java.nio.charset.StandardCharsets;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import org.slf4j.LoggerFactory;

/**
 * Processes a {@link PlooiFile}, somehow modifying or enhancing it.
 */
public interface FileProcessing extends EnvelopeProcessing<DeliveryEnvelope> {

    /**
     * Processes a {@link PlooiFile}.
     *
     * @param  file The {@link PlooiFile} to adapt
     * @return      The adapted (modified) PlooiFile
     */
    PlooiFile process(PlooiFile file);

    /**
     * Populate a {@link PlooiFile} from an input string.
     *
     * @param  source The string to populate the PlooiFile from
     * @return        The populated PlooiFile
     */
    default PlooiFile populate(String source) {
        return populate(source.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Populate a {@link PlooiFile} from an input byte array.
     *
     * @param  source The byte array to populate the PlooiFile from
     * @return        The populated PlooiFile
     */
    default PlooiFile populate(byte[] source) {
        return process(new PlooiFile().content(source));
    }

    /**
     * Convenience {@link EnvelopeProcessing} implementation which iterates and adapts all
     * {@link DeliveryEnvelope#getPlooiFiles() files} in a {@link DeliveryEnvelope}.
     *
     * @param  target The {@link DeliveryEnvelope} with files to adapt
     * @return        The DeliveryEnvelope with adapted files
     */
    @Override
    default DeliveryEnvelope process(DeliveryEnvelope target) {
        if (!target.getPlooiFiles().isEmpty()) {
            for (var plooiFile : target.getPlooiFiles()) {
                try {
                    process(plooiFile);
                } catch (RuntimeException e) {
                    target.status().addDiagnose(DiagnosticCode.INTEGRATION, e);
                }
            }
        } else {
            LoggerFactory.getLogger(getClass()).debug("Nothing to process for {}", target);
        }
        return target;
    }
}

package nl.overheid.koop.plooi.document.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;

public interface PlooiDocumentMapping {

    /**
     * Populate a {@link PlooiEnvelope} from an input file.
     *
     * @param  source The {@link File} to populate the PlooiDocument from
     * @param  target The PlooiDocument to populate
     * @return        The populated PlooiDocument
     */
    default PlooiEnvelope populate(File source, PlooiEnvelope target) {
        try (InputStream contentStrm = new FileInputStream(source)) {
            populate(contentStrm, target);
        } catch (IOException e) {
            target.status().addDiagnose(DiagnosticCode.CANT_PARSE, e);
        }
        return target;
    }

    /**
     * Populate a {@link PlooiEnvelope} from an input stream.
     *
     * @param  source The {@link InputStream} to populate the PlooiDocument from
     * @param  target The PlooiDocument to populate
     * @return        The populated PlooiDocument
     */
    PlooiEnvelope populate(InputStream srcStrm, PlooiEnvelope target);
}

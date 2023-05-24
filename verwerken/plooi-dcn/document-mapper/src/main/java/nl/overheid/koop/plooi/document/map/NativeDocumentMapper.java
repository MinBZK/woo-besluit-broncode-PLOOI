package nl.overheid.koop.plooi.document.map;

import java.io.InputStream;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.util.PlooiBindingException;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.registration.model.Severity;

/**
 * Unmarshals a PLOOI json file using the OpenAPI mapper. This mapper just replaces an existing {@link Plooi#document}
 * object, dropping everything that might already be in there. So if you need to map individual properties using another
 * mapper, apply that mapper <em>after</em> this one.
 */
public class NativeDocumentMapper implements PlooiDocumentMapping {

    @Override
    public PlooiEnvelope populate(InputStream srcStrm, PlooiEnvelope target) {
        try {
            target.replacePlooi(PlooiBindings.plooiBinding().unmarshalFromStream(srcStrm));
            if (target.getPlooi().getDocumentrelaties() != null) {
                target.getPlooi().getDocumentrelaties().forEach(target::addRelation);
            }
        } catch (PlooiBindingException e) {
            target.status().addDiagnose(DiagnosticCode.CANT_PARSE, Severity.ERROR, e);
        }
        return target;
    }
}

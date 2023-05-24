package nl.overheid.koop.plooi.dcn.ronl.api;

import java.nio.charset.StandardCharsets;
import java.util.List;
import nl.overheid.koop.plooi.dcn.component.common.DefaultDocumentCollector;
import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.DocumentCollecting;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;

/**
 * This DocumentCollector is used to detect RONL files containing links to PLOOI documents and discard it
 */
public class RonlDocumentCollector extends DefaultDocumentCollector {

    public RonlDocumentCollector(String label, boolean split) {
        super(label, split);
    }

    @Override
    public List<DeliveryEnvelope> collect(PlooiFile file) {
        if (file.getChildren().isEmpty()) {
            // Get file content and initiate strings to search for
            String content = new String(file.getContent(), StandardCharsets.UTF_8);
            String compare1 = "<introduction>";
            String compare2 = "<a href=\"https://open.overheid.nl/repository/";

            // file must contain an introduction box and a href inside it
            if (content.contains(compare1) && content.contains(compare2)) {
                var plooiDocument = DocumentCollecting.createDeliveryEnvelope(file, this.sourceLabel, file.getFile().getId());
                plooiDocument.status().discard().addDiagnose(DiagnosticCode.DISCARD, "Discarding document with plooi reference");
                return DocumentCollecting.createDocumentListWith(plooiDocument);
            }
        }
        return super.collect(file);
    }
}

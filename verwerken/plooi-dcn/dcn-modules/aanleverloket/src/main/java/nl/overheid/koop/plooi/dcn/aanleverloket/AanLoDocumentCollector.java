package nl.overheid.koop.plooi.dcn.aanleverloket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;
import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.DocumentCollecting;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import org.apache.commons.compress.utils.FileNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This DocumentCollector is used to collect files from Aanleverloket
 */
public class AanLoDocumentCollector implements DocumentCollecting {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Pattern pattern = Pattern.compile(".*_(plooicb.*)\\.zip");

    private final String label;

    public AanLoDocumentCollector(String srcLabel) {
        this.label = srcLabel;
    }

    @Override
    public List<DeliveryEnvelope> collect(PlooiFile file) {
        return mapAndCollect(file.getContent(), file.getFile().getBestandsnaam());
    }

    List<DeliveryEnvelope> mapAndCollect(byte[] zipBytes, String fileName) {
        DeliveryEnvelope envelope = new DeliveryEnvelope(this.label, extractExternalId(fileName));
        try (var zipStream = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            // Iterate zipStream entries containing internal docs (pdf and xml)
            for (var zipEntry = zipStream.getNextEntry(); zipEntry != null; zipEntry = zipStream.getNextEntry()) {
                this.logger.debug("Iterating ZipEntry: {}", zipEntry);
                var file = new PlooiFile(zipEntry.getName(), FileNameUtils.getExtension(zipEntry.getName())).content(zipStream.readAllBytes());
                file.getFile().setGepubliceerd("pdf".equalsIgnoreCase(file.getFile().getLabel()));
                envelope.addPlooiFile(file);
            }
        } catch (IOException e) {
            envelope.status().addDiagnose(DiagnosticCode.INTEGRATION, e);
        }
        return DocumentCollecting.createDocumentListWith(envelope);
    }

    String extractExternalId(String fileName) {
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches() ? matcher.group(1) : fileName;
    }
}

package nl.overheid.koop.plooi.dcn.component.common;

import java.util.List;
import java.util.Objects;
import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.DocumentCollecting;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDocumentCollector implements DocumentCollecting {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String sourceLabel;
    protected final boolean splitBundle;
    private PublicatieClient publicatieClient;

    public DefaultDocumentCollector withPublicatieClient(PublicatieClient publClient) {
        this.publicatieClient = publClient;
        return this;
    }

    public DefaultDocumentCollector(String label, boolean split) {
        this.sourceLabel = label;
        this.splitBundle = split;
    }

    @Override
    public List<DeliveryEnvelope> collect(PlooiFile file) {
        var plooiDocument = DocumentCollecting.createDeliveryEnvelope(file, this.sourceLabel, file.getFile().getId());
        var collectedDocuments = DocumentCollecting.createDocumentListWith(plooiDocument);
        if (file.getChildren().size() == 1) {
            var child = file.getChildren().get(0);
            plooiDocument.addPlooiFile(child);
        } else if (file.getChildren().size() > 1) {
            for (PlooiFile child : file.getChildren()) {
                if (this.splitBundle) {
                    var partDocument = DocumentCollecting.createDeliveryEnvelope(
                            new PlooiFile(file).content(file.getContent()), this.sourceLabel, file.getFile().getId(), child.getFile().getId())
                            .addPlooiFile(child);
                    partDocument.addRelation(new Relatie()
                            .role(RelationType.BUNDEL.getUri())
                            .relation(plooiDocument.getPlooiIntern().getDcnId())
                            .titel(StringUtils.defaultIfBlank(file.getFile().getTitel(), file.getFile().getBestandsnaam())));
                    plooiDocument.addRelation(new Relatie()
                            .role(RelationType.ONDERDEEL.getUri())
                            .relation(partDocument.getPlooiIntern().getDcnId())
                            .titel(StringUtils.defaultIfBlank(child.getFile().getTitel(), child.getFile().getBestandsnaam())));
                    collectedDocuments.add(partDocument);
                } else {
                    plooiDocument.addPlooiFile(child);
                }
            }
        }
        this.logger.debug("Collected {} into {}", file, collectedDocuments);
        return collectedDocuments;
    }

    @Override
    public PlooiEnvelope process(PlooiEnvelope target) {
        this.logger.debug("PostCollecting {}", target);
        target.fixBundlePartTitle(Objects.requireNonNull(this.publicatieClient, "publicatieClient is required for processing"));
        return target;
    }
}

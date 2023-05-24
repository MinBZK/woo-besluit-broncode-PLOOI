package nl.overheid.koop.plooi.dcn.repository.store;

import java.util.ArrayList;
import java.util.List;
import nl.overheid.koop.plooi.dcn.component.types.ObjectProcessing;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Versie.OorzaakEnum;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deletes a PLOOI document from the document repository. (Actually, it keeps everything but renames the manifest, so
 * the files are no longer found.)
 */
public class PlooiRepositoryDeleting implements ObjectProcessing<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PublicatieClient publicatieClient;
    private final OorzaakEnum oorzaak;

    public PlooiRepositoryDeleting(PublicatieClient updateClient, OorzaakEnum action) {
        this.publicatieClient = updateClient;
        this.oorzaak = action;
    }

    @Override
    public List<String> process(String dcnId) {
        this.logger.debug("Processing {} of {}", this.oorzaak, dcnId);
        var movingIds = new ArrayList<String>();
        movingIds.add(dcnId);
        movingIds.addAll(this.publicatieClient.getRelations(dcnId, RelationType.ONDERDEEL).stream().map(Relatie::getRelation).toList());
        movingIds.forEach(id -> this.publicatieClient.delete(id, this.oorzaak, ""));
        return movingIds;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " applying " + this.publicatieClient;
    }
}

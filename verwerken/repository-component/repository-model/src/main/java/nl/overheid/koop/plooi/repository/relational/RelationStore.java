package nl.overheid.koop.plooi.repository.relational;

import java.util.List;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Relatie;

public interface RelationStore {

    void store(List<Relatie> relationsForStage, String dcnId, String origin);

    Plooi populate(Plooi target);

    List<Relatie> relations(String dcnId, List<String> types);

    List<Relatie> related(String dcnId);
}

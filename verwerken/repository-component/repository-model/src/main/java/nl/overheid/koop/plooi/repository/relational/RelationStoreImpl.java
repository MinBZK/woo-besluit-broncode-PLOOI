package nl.overheid.koop.plooi.repository.relational;

import java.util.List;
import javax.transaction.Transactional;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.springframework.stereotype.Component;

@Component
public class RelationStoreImpl implements RelationStore {

    private final RelationRepository relationRepos;

    public RelationStoreImpl(RelationRepository relationRepository) {
        this.relationRepos = relationRepository;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void store(List<Relatie> relationsForStage, String dcnId, String stage) {
        RelationStoreImpl.this.relationRepos.deleteByFromIdAndOrigin(dcnId, stage);
        RelationStoreImpl.this.relationRepos.saveAll(
                relationsForStage.stream().map(r -> new Relation(dcnId, RelationType.forUri(r.getRole()), r.getRelation(), r.getTitel(), stage)).toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public List<Relatie> relations(String dcnId, List<String> types) {
        try (var relations = types == null || types.isEmpty()
                ? this.relationRepos.getByFromId(dcnId)
                : this.relationRepos.getByFromIdAndRelationTypeIn(dcnId, types.stream().map(RelationType::forUri).toList())) {
            return relations.map(rel -> new Relatie().role(rel.getRelationType().getUri()).relation(rel.getToId()).titel(rel.getTitle())).toList();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Plooi populate(Plooi target) {
        return target.documentrelaties(relations(target.getPlooiIntern().getDcnId(), null));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public List<Relatie> related(String dcnId) {
        try (var relations = this.relationRepos.getByToId(dcnId)) {
            return relations.map(rel -> new Relatie().role(rel.getRelationType().getUri()).relation(rel.getFromId()).titel(rel.getTitle())).toList();
        }
    }
}

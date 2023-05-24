package nl.overheid.koop.plooi.repository.relational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.stream.Stream;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RelationStoreTest {

    private static final String FROM_ID = "test-df7bca4e52463ce7d7abfde71cdbeb25161eb3ae";
    private static final String TO_ID = "identiteit";
    private static final Relatie RELATIE = new Relatie().role(RelationType.IDENTITEITS_GROEPEN.getUri()).relation(TO_ID);
    private static final Relatie INV_RELATIE = new Relatie().role(RelationType.IDENTITEITS_GROEPEN.getUri()).relation(FROM_ID);

    @Mock(lenient = true)
    private RelationRepository relationRepository;
    private RelationStore relationStore;

    @BeforeEach
    void init() {
        var relationStream = Stream.of(new Relation(FROM_ID, RelationType.IDENTITEITS_GROEPEN, TO_ID, null, "test"));
        when(this.relationRepository.getByFromId(FROM_ID)).thenReturn(relationStream);
        when(this.relationRepository.getByFromIdAndRelationTypeIn(FROM_ID, List.of(RelationType.IDENTITEITS_GROEPEN))).thenReturn(relationStream);
        when(this.relationRepository.getByToId(TO_ID)).thenReturn(relationStream);
        this.relationStore = new RelationStoreImpl(this.relationRepository);
    }

    @Test
    void store_IdentityGroupExistNotSameToIdsTest() {
        this.relationStore.store(List.of(RELATIE), FROM_ID, "test");
        verify(this.relationRepository).deleteByFromIdAndOrigin(FROM_ID, "test");
        verify(this.relationRepository).saveAll(argThat(relations -> {
            var rel = relations.iterator().next();
            return rel.getFromId().equals(FROM_ID) && rel.getToId().equals(TO_ID);
        }));
    }

    @Test
    void relations_ByFromId() {
        assertEquals(List.of(RELATIE), this.relationStore.relations(FROM_ID, null));
    }

    @Test
    void relations_ByFromIdAndRelationType() {
        assertEquals(List.of(RELATIE), this.relationStore.relations(FROM_ID, List.of(RelationType.IDENTITEITS_GROEPEN.getUri())));
    }

    @Test
    void populate() {
        assertEquals(List.of(RELATIE), this.relationStore.populate(new Plooi().plooiIntern(new PlooiIntern().dcnId(FROM_ID))).getDocumentrelaties());
    }

    @Test
    void related() {
        assertEquals(List.of(INV_RELATIE), this.relationStore.related(TO_ID));
    }
}

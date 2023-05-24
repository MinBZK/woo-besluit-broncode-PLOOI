package nl.overheid.koop.plooi.repository.relational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = TestConfiguration.class)
@PropertySource("classpath:application-repos.properties")
class RelationRepositoryTest {

    @Autowired
    private RelationRepository relationRepository;

    @BeforeEach
    void init() {
        Relation relation1 = new Relation("fromId1", RelationType.IDENTITEITS_GROEPEN, "toId1", null, "dedup");
        relation1 = this.relationRepository.save(relation1);
        Relation relation2 = new Relation("fromId2", RelationType.IDENTITEITS_GROEPEN, "toId1", null, "dedup");
        relation2 = this.relationRepository.save(relation2);
        Relation relation3 = new Relation("fromId3", RelationType.IDENTITEITS_GROEPEN, "toId1", null, "dedup");
        relation3 = this.relationRepository.save(relation3);
    }

    @Test
    void getByFromId() {
        var relationResult = this.relationRepository.getByFromId("fromId1").toList();
        assertEquals(1, relationResult.size());
        assertEquals("toId1", relationResult.get(0).getToId());
        assertEquals(RelationType.IDENTITEITS_GROEPEN, relationResult.get(0).getRelationType());
        assertEquals("fromId1", relationResult.get(0).getFromId());
    }

    @Test
    void getByFromIdAndRelationType_match() {
        var relationResult = this.relationRepository.getByFromIdAndRelationTypeIn("fromId1",
                List.of(RelationType.IDENTITEITS_GROEPEN, RelationType.BIJLAGE)).toList();
        assertEquals(1, relationResult.size());
        assertEquals("toId1", relationResult.get(0).getToId());
        assertEquals(RelationType.IDENTITEITS_GROEPEN, relationResult.get(0).getRelationType());
        assertEquals("fromId1", relationResult.get(0).getFromId());
    }

    @Test
    void getByFromIdAndRelationType_noMatch() {
        var relationResult = this.relationRepository.getByFromIdAndRelationTypeIn("fromId1",
                List.of(RelationType.BIJLAGE)).toList();
        assertTrue(relationResult.isEmpty());
    }

    @Test
    void getDcnIdListWithRelationsTest() {
        var relationResult1 = this.relationRepository.getByToId("toId1").toList();
        assertEquals(3, relationResult1.size());
    }

    @Test
    void deleteByFromIdAndOrigin() {
        this.relationRepository.deleteByFromIdAndOrigin("fromId1", "dedup");
        var relationResult1 = this.relationRepository.getByFromId("fromId1").toList();
        assertTrue(relationResult1.isEmpty());
        var relationResult2 = this.relationRepository.getByFromId("fromId2").toList();
        assertEquals(1, relationResult2.size());
    }
}

package nl.overheid.koop.plooi.repository.service;

import java.util.List;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.repository.data.RelationType;
import nl.overheid.koop.plooi.repository.idmapping.IdentifierMapping;
import nl.overheid.koop.plooi.repository.relational.RelationStore;
import nl.overheid.koop.plooi.repository.storage.FilesystemStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TestConfiguration {

    private static final Relatie TEST_RELATION = new Relatie()
            .relation("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS")
            .role(RelationType.IDENTITEITS_GROEPEN.getUri());

    @Bean
    FilesystemStorage storage() {
        return new FilesystemStorage(TestHelper.REPOS_DIR, false);
    }

    @Bean
    StorageAccess storageAccess(FilesystemStorage storage) {
        return new StorageAccess(storage, identifierMapping());
    }

    @Bean
    IdentifierMapping identifierMapping() {
        return new IdentifierMapping(null);
    }

    @Bean
    RelationStore relationStore() {
        return new RelationStore() {

            @Override
            public void store(List<Relatie> relationsForStage, String dcnId, String origin) {
            }

            @Override
            public Plooi populate(Plooi target) {
                return target.addDocumentrelatiesItem(TEST_RELATION);
            }

            @Override
            public List<Relatie> relations(String dcnId, List<String> types) {
                return List.of(TEST_RELATION);
            }

            @Override
            public List<Relatie> related(String dcnId) {
                return List.of(TEST_RELATION);
            }
        };
    }
}

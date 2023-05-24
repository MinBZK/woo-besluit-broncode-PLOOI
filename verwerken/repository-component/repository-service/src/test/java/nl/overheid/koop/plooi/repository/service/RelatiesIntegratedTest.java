package nl.overheid.koop.plooi.repository.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.transaction.Transactional;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.util.PlooiBinding;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.repository.data.RelationType;
import nl.overheid.koop.plooi.repository.idmapping.IdentifierMapping;
import nl.overheid.koop.plooi.repository.relational.RelationRepository;
import nl.overheid.koop.plooi.repository.relational.RelationStoreImpl;
import nl.overheid.koop.plooi.repository.storage.FilesystemStorage;
import nl.overheid.koop.plooi.repository.storage.Storage;
import nl.overheid.koop.plooi.service.error.HttpStatusExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = { PublicatieController.class, DocumentenController.class, RepositoryExceptionHandler.class, HttpStatusExceptionHandler.class,
        RelationStoreImpl.class, RelationRepository.class })
@ContextConfiguration(classes = IntegratedTestConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RelatiesIntegratedTest {

    @Autowired
    private MockMvc mockMvc;

    private FilesystemStorage repositoryStore = new FilesystemStorage(TestHelper.REPOS_DIR, false);
    private PlooiBinding<List<Relatie>> relationsBinding = PlooiBindings.relationsBinding();

    @BeforeAll
    void init() throws IOException {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
        FileUtils.deleteDirectory(new File(TestHelper.REPOS_DIR));
        this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                TestHelper.getManifest(1, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID));
        this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.json",
                TestHelper.getPlooi(1, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID));
    }

    @BeforeEach
    void initRelation() throws Exception {
        this.mockMvc
                .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_relaties/test")
                        .contentType(Storage.MIME_JSON)
                        .content(this.relationsBinding.marshalToString(List.of(
                                new Relatie().role(RelationType.IDENTITEITS_GROEPEN.getUri()).relation("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"),
                                new Relatie().role(RelationType.BIJLAGE.getUri()).relation("oep-14145d83864b4ea1233c0b01135b8a3f729c0b87")))))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void postRelations() throws Exception {
        this.mockMvc
                .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_relaties/test")
                        .contentType(Storage.MIME_JSON)
                        .content(this.relationsBinding.marshalToString(List.of(
                                new Relatie().role(RelationType.IDENTITEITS_GROEPEN.getUri()).relation("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"),
                                new Relatie().role(RelationType.BIJLAGE.getUri()).relation("oep-14145d83864b4ea1233c0b01135b8a3f729c0b87")))))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void getPlooi() throws Exception {
        this.mockMvc
                .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentrelaties[0].role").value(RelationType.IDENTITEITS_GROEPEN.getUri()))
                .andExpect(jsonPath("$.documentrelaties[0].relation").value("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"))
                .andExpect(jsonPath("$.documentrelaties[1].role").value(RelationType.BIJLAGE.getUri()))
                .andExpect(jsonPath("$.documentrelaties[1].relation").value("https://open.overheid.nl/documenten/oep-14145d83864b4ea1233c0b01135b8a3f729c0b87"))
                .andExpect(jsonPath("$.documentrelaties[2].role").doesNotExist());
    }

    @Test
    void getPlooi_viaPublicationController() throws Exception {
        this.mockMvc
                .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentrelaties").doesNotExist());
    }

    @Test
    @Transactional
    void getPlooi_afterUpdate() throws Exception {
        this.mockMvc
                .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_relaties/test")
                        .contentType(Storage.MIME_JSON)
                        .content(this.relationsBinding.marshalToString(List.of(
                                new Relatie().role(RelationType.BIJLAGE.getUri()).relation("oep-14145d83864b4ea1233c0b01135b8a3f729c0b87")))))
                .andExpect(status().isOk());
        this.mockMvc
                .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentrelaties[0].role").value(RelationType.BIJLAGE.getUri()))
                .andExpect(jsonPath("$.documentrelaties[0].relation").value("https://open.overheid.nl/documenten/oep-14145d83864b4ea1233c0b01135b8a3f729c0b87"))
                .andExpect(jsonPath("$.documentrelaties[1].role").doesNotExist());
    }

    @Test
    @Transactional
    void getPlooi_afterUpdate_differentStage() throws Exception {
        this.mockMvc
                .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_relaties/not_test")
                        .contentType(Storage.MIME_JSON)
                        .content(this.relationsBinding.marshalToString(List.of(
                                new Relatie().role(RelationType.BIJLAGE.getUri()).relation("oep-14145d83864b4ea1233c0b01135b8a3f729c0b87")))))
                .andExpect(status().isOk());
        this.mockMvc
                .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documentrelaties[0].role").value(RelationType.IDENTITEITS_GROEPEN.getUri()))
                .andExpect(jsonPath("$.documentrelaties[0].relation").value("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"))
                .andExpect(jsonPath("$.documentrelaties[1].role").value(RelationType.BIJLAGE.getUri()))
                .andExpect(jsonPath("$.documentrelaties[1].relation").value("https://open.overheid.nl/documenten/oep-14145d83864b4ea1233c0b01135b8a3f729c0b87"))
                .andExpect(jsonPath("$.documentrelaties[2].role").value(RelationType.BIJLAGE.getUri()))
                .andExpect(jsonPath("$.documentrelaties[2].relation").value("https://open.overheid.nl/documenten/oep-14145d83864b4ea1233c0b01135b8a3f729c0b87"))
                .andExpect(jsonPath("$.documentrelaties[3].role").doesNotExist());
    }

    @Test
    @Transactional
    void getRelaties() throws Exception {
        this.mockMvc
                .perform(get("/documenten/oep-14145d83864b4ea1233c0b01135b8a3f729c0b87/_relaties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].role").value(RelationType.BIJLAGE.getUri()))
                .andExpect(jsonPath("$.[0].relation").value("https://open.overheid.nl/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                .andExpect(jsonPath("$.[1]").doesNotExist());
        this.mockMvc
                .perform(get("/documenten/3c82b4053a77f958a51b2fb4783f28c8cb66d10d/_relaties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].role").value(RelationType.IDENTITEITS_GROEPEN.getUri()))
                // TODO This relation is lacking the prefix, because we don't handle inverted relations well
                .andExpect(jsonPath("$.[0].relation").value("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                .andExpect(jsonPath("$.[1]").doesNotExist());
        this.mockMvc
                .perform(get("/documenten/unknown/_relaties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").doesNotExist());
    }

    @Test
    void ronlBundle() throws Exception {
        var dcnId = "ronl-228036432630c580dcdddefd9c3a6de8e2587c1b";
        this.repositoryStore.store(dcnId, null, "manifest.json",
                TestHelper.getManifest(1, "ronl", "0acb97b8-b3cf-4654-92c2-0fa4199c100c"));
        this.repositoryStore.store(dcnId, null, "plooi.json",
                TestHelper.getPlooi(1, "ronl", "0acb97b8-b3cf-4654-92c2-0fa4199c100c"));

        this.mockMvc
                .perform(post("/publicatie/ronl-228036432630c580dcdddefd9c3a6de8e2587c1b/_relaties/test")
                        .contentType(Storage.MIME_JSON)
                        .content(this.relationsBinding.marshalToString(List.of(
                                new Relatie().role(RelationType.IDENTITEITS_GROEPEN.getUri())
                                        .relation("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"),
                                new Relatie().role(RelationType.ONDERDEEL.getUri())
                                        .relation("ronl-684333ccbc1afcdb275529c54261dff4061180ea")
                                        .titel("Akte van aanstelling"),
                                new Relatie().role(RelationType.ONDERDEEL.getUri())
                                        .relation("ronl-a83c9806d87ce33e632c253eb2166cc937f0bef1")
                                        .titel("Supplement akte van aanstelling")))))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(get("/documenten/ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3/_plooi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document.pid").value("https://open.overheid.nl/documenten/ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3"))
                .andExpect(jsonPath("$.plooiIntern.dcnId").value(dcnId))
                .andExpect(jsonPath("$.documentrelaties[0].relation").value("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"))
                .andExpect(jsonPath("$.documentrelaties[1].relation").value("https://open.overheid.nl/documenten/ronl-26cd903f-53da-4d64-8ca6-74e91b6baaca"))
                .andExpect(jsonPath("$.documentrelaties[2].relation").value("https://open.overheid.nl/documenten/ronl-98043096-0324-4e14-9736-bb97f82fac78"))
                .andExpect(jsonPath("$.documentrelaties[3].relation").doesNotExist());
        this.mockMvc
                .perform(get("/documenten/ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3/_owms"))
                .andExpect(status().isOk())
                .andExpect(xpath("//owmskern/identifier").string("ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3_1"))
                .andExpect(xpath("//plooiipm/identifier").string(dcnId))
                .andExpect(xpath("//owmsmantel/hasPart[1]").string("ronl-26cd903f-53da-4d64-8ca6-74e91b6baaca_1"))
                .andExpect(xpath("//documenten/document[1]/ref").string("ronl-26cd903f-53da-4d64-8ca6-74e91b6baaca_1"))
                .andExpect(xpath("//owmsmantel/hasPart[2]").string("ronl-98043096-0324-4e14-9736-bb97f82fac78_1"))
                .andExpect(xpath("//documenten/document[2]/ref").string("ronl-98043096-0324-4e14-9736-bb97f82fac78_1"))
                .andExpect(xpath("//owmsmantel/hasPart[3]").doesNotExist())
                .andExpect(xpath("//documenten/document[3]/ref").doesNotExist());
    }
}

@Configuration
@EnableAutoConfiguration
@PropertySource("classpath:application-it.properties")
@EnableJpaRepositories(basePackages = "nl.overheid.koop.plooi.repository.relational")
@EntityScan(basePackages = "nl.overheid.koop.plooi.repository.relational")
@ComponentScan(basePackages = "nl.overheid.koop.plooi.repository.relational")
class IntegratedTestConfiguration {

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
}

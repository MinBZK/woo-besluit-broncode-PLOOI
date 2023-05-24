package nl.overheid.koop.plooi.repository.service;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.Versie.OorzaakEnum;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.repository.storage.FilesystemStorage;
import nl.overheid.koop.plooi.service.error.HttpStatusExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = { TestConfiguration.class, DocumentenController.class, RepositoryExceptionHandler.class, HttpStatusExceptionHandler.class, })
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private FilesystemStorage repositoryStore = new FilesystemStorage(TestHelper.REPOS_DIR, false);

    @BeforeEach
    void init() throws IOException {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "false");
        FileUtils.deleteDirectory(new File(TestHelper.REPOS_DIR));
    }

    @Nested
    class GetLabels {

        @Test
        void getLabels_unknown() throws Exception {
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getLabels() throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("""
                            {\
                            "meta":"/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/meta",\
                            "document":"/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/document",\
                            "_manifest":"/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_manifest",\
                            "_relaties":"/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_relaties",\
                            "_owms":"/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_owms",\
                            "_plooi":"/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi",\
                            "_blokkades":"/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades",\
                            "_versie":"/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_versie",\
                            "_text":"/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_text"\
                            }"""));
        }

        @Test
        void getLabels_noBlocks() throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    new ByteArrayInputStream(
                            PlooiBindings.plooiBinding()
                                    .marshalToString(new Plooi().addVersiesItem(new Versie().nummer(1)
                                            .oorzaak(OorzaakEnum.AANLEVERING)))
                                    .getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isOk());
        }

        @Test
        void getLabels_delayedPast() throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    new ByteArrayInputStream(
                            PlooiBindings.plooiBinding()
                                    .marshalToString(new Plooi().addVersiesItem(new Versie().nummer(1)
                                            .oorzaak(OorzaakEnum.AANLEVERING)
                                            .zichtbaarheidsdatumtijd(OffsetDateTime.now().minusDays(1))))
                                    .getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isOk());
        }

        @Test
        void getLabels_delayedFuture() throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    new ByteArrayInputStream(
                            PlooiBindings.plooiBinding()
                                    .marshalToString(new Plooi().addVersiesItem(new Versie().nummer(1)
                                            .oorzaak(OorzaakEnum.AANLEVERING)
                                            .zichtbaarheidsdatumtijd(OffsetDateTime.now().plusDays(1))))
                                    .getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getLabels_olderVersionBlocked() throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    new ByteArrayInputStream(
                            PlooiBindings.plooiBinding()
                                    .marshalToString(new Plooi()
                                            .addVersiesItem(new Versie().nummer(1)
                                                    .oorzaak(OorzaakEnum.AANLEVERING)
                                                    .addBlokkadesItem("concept"))
                                            .addVersiesItem(new Versie().nummer(2)
                                                    .oorzaak(OorzaakEnum.WIJZIGING)))
                                    .getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isOk());
        }

        @Test
        void getLabels_blocked() throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    new ByteArrayInputStream(
                            PlooiBindings.plooiBinding()
                                    .marshalToString(new Plooi().addVersiesItem(new Versie().nummer(1)
                                            .oorzaak(OorzaakEnum.AANLEVERING)
                                            .addBlokkadesItem("concept")))
                                    .getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getLabels_deleted() throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getDeletedManifest());
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class GetFile {

        @Test
        void getFile_unknownId() throws Exception {
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/xml"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Unknown file manifest.json for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @ParameterizedTest
        @CsvSource({
                "3, _text,     200, text                                                                        ",
                "3, _manifest, 200, {                                                                           ",
                "3, _plooi,    200, {                                                                           ",
                "3, _owms,     200, <?xml                                                                       ",
                "3, _relaties, 200, [{\"relation\":\"3c82b4053a77f958a51b2fb4783f28c8cb66d10d\",\"role\":\"https",
                "3, _versie,   200, {\"nummer\":3,\"oorzaak\":\"wijziging\",\"bestanden\":[{\"gepubliceerd\":fal",
                "3, _bla,      404, Unknown label _bla for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a   ",
                "4, _manifest, 404, Unknown document plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a         ",
                "4, _text,     404, Unknown document plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a         ",
                "4, _bla,      404, Unknown document plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a         ",
                "5, _manifest, 200, {                                                                           ",
                "5, _text,     200, text                                                                        ",
                "5, _bla,      404, Unknown label _bla for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a   ",
        })
        void getFile_meta(Integer currentVersion, String getLabel, Integer status, String content) throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getManifest(currentVersion, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID));
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.json",
                    TestHelper.getPlooi(currentVersion, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID));
            DocumentenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.txt",
                    new ByteArrayInputStream("text".getBytes(StandardCharsets.UTF_8)));
            var result = DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/" + getLabel))
                    .andExpect(status().is(status));
            if (status < 300) {
                result.andExpect(content().string(startsWith(content)));
            } else if (content != null) {
                result.andExpect(content().string(content));
            }
        }

        @ParameterizedTest
        @CsvSource({
                "1, meta,      200, META v1                                                                     ",
                "1, document,  200, PDF v1                                                                      ",
                "1, zip,       404, Unknown label zip for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a    ",
                "2, meta,      200, META v1                                                                     ",
                "2, document,  200, PDF v2                                                                      ",
                "3, meta,      200, META v2                                                                     ",
                "3, document,  200, PDF v2                                                                      ",
                "4, document,  404, Unknown document plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a         ",
                "5, meta,      200, META v2                                                                     ",
                "5, document,  200, PDF v2                                                                      ",
        })
        void getFile_versions(Integer currentVersion, String getLabel, Integer status, String content) throws Exception {
            var dcnId = "plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a";
            DocumentenControllerTest.this.repositoryStore.store(dcnId, null, "manifest.json",
                    TestHelper.getManifest(currentVersion, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID));
            DocumentenControllerTest.this.repositoryStore.store(dcnId, 1, "document.pdf",
                    new ByteArrayInputStream("PDF v1".getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.repositoryStore.store(dcnId, 1, "metadata.json",
                    new ByteArrayInputStream("META v1".getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.repositoryStore.store(dcnId, 2, "gewijzigd.pdf",
                    new ByteArrayInputStream("PDF v2".getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.repositoryStore.store(dcnId, 3, "gewijzigd.json",
                    new ByteArrayInputStream("META v2".getBytes(StandardCharsets.UTF_8)));
            var result = DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/" + getLabel))
                    .andExpect(status().is(status));
            if (status < 300) {
                result.andExpect(content().string(content));
            } else if (content != null) {
                result.andExpect(content().string(content));
            }
        }

        @Test
        void getFile_api() throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "manifest.json",
                    TestHelper.getManifest(1, "plooi-api", "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"));
            DocumentenControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", 1, "metadata.json",
                    new ByteArrayInputStream("META v1".getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/meta"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("META v1"));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/meta"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("META v1"));
        }

        @Test
        void getFile_ronl_mappedId() throws Exception {
            DocumentenControllerTest.this.repositoryStore.store("ronl-228036432630c580dcdddefd9c3a6de8e2587c1b", null, "manifest.json",
                    TestHelper.getManifest(1, "ronl", "0acb97b8-b3cf-4654-92c2-0fa4199c100c"));
            DocumentenControllerTest.this.repositoryStore.store("ronl-228036432630c580dcdddefd9c3a6de8e2587c1b", 1, "metadata.json",
                    new ByteArrayInputStream("Akte van aanstelling 001474".getBytes(StandardCharsets.UTF_8)));
            DocumentenControllerTest.this.repositoryStore.store("ronl-228036432630c580dcdddefd9c3a6de8e2587c1b", null, "plooi.json",
                    TestHelper.getPlooi(1, "ronl", "0acb97b8-b3cf-4654-92c2-0fa4199c100c"));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/ronl-228036432630c580dcdddefd9c3a6de8e2587c1b/meta"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Akte van aanstelling 001474"));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3/meta"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Akte van aanstelling 001474"));
            DocumentenControllerTest.this.mockMvc
                    .perform(get("/documenten/ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3/_plooi"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.document.pid").value("https://open.overheid.nl/documenten/ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3"))
                    .andExpect(jsonPath("$.plooiIntern.dcnId").value("ronl-228036432630c580dcdddefd9c3a6de8e2587c1b"));
        }
    }
}

package nl.overheid.koop.plooi.repository.service;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.Versie.OorzaakEnum;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.repository.storage.FilesystemStorage;
import nl.overheid.koop.plooi.repository.storage.Storage;
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

@SpringBootTest(classes = { TestConfiguration.class, PublicatieController.class, RepositoryExceptionHandler.class, HttpStatusExceptionHandler.class, })
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicatieControllerTest {

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
            PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getLabels() throws Exception {
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.dcnId").value("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(content().string("""
                            {\
                            "dcnId":"plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a",\
                            "labels":{\
                            "meta":"/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/-/meta",\
                            "document":"/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/-/document",\
                            "_manifest":"/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_manifest",\
                            "_relaties":"/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_relaties",\
                            "_owms":"/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_owms",\
                            "_plooi":"/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi",\
                            "_blokkades":"/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades",\
                            "_versie":"/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_versie",\
                            "_text":"/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_text"\
                            }\
                            }"""));
        }
    }

    @Nested
    class GetMetaFile {

        @Nested
        class GetManifest {

            @Test
            void getManifest_unknown() throws Exception {
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_manifest"))
                        .andExpect(status().isNotFound());
            }

            @Test
            void getManifest() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        TestHelper.getFirstManifest());
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_manifest"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.plooiIntern.dcnId").value("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                        .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                        .andExpect(jsonPath("$.versies[1]").doesNotExist());
            }

            @Test
            void getManifest_api() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "manifest.json",
                        TestHelper.getManifest(1, "plooi-api", "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"));
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/_manifest"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.plooiIntern.dcnId").value("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"))
                        .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                        .andExpect(jsonPath("$.versies[1]").doesNotExist());
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/_manifest"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.plooiIntern.dcnId").value("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"))
                        .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                        .andExpect(jsonPath("$.versies[1]").doesNotExist());
            }
        }

        @Nested
        class GetBlokkades {

            @Test
            void getBlokkades() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "manifest.json",
                        new ByteArrayInputStream(PlooiBindings.plooiBinding()
                                .marshalToString(new Plooi()
                                        .plooiIntern(new PlooiIntern()
                                                .dcnId("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                                        .addVersiesItem(new Versie()
                                                .nummer(1)
                                                .oorzaak(OorzaakEnum.AANLEVERING)
                                                .blokkades(List.of("virus"))
                                                .zichtbaarheidsdatumtijd(OffsetDateTime.of(2099, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))))
                                .getBytes(StandardCharsets.UTF_8)));
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/_blokkades"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[\"virus\",\"_zichtbaarheidsdatumtijd\"]"));
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/_blokkades"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[\"virus\",\"_zichtbaarheidsdatumtijd\"]"));
            }

            @Test
            void getBlokkades_intrekking() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "manifest.json",
                        TestHelper.getDeletedManifest());
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/_blokkades"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[\"_intrekking\"]"));
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/_blokkades"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[\"_intrekking\"]"));
            }
        }

        @Nested
        class GetPlooi {

            @Test
            void getPlooi_unknown() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        TestHelper.getFirstManifest());
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi"))
                        .andExpect(status().isNotFound());
            }

            @Test
            void getPlooi() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        TestHelper.getFirstManifest());
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.json",
                        new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi"))
                        .andExpect(status().isOk());
            }

            @Test
            void getPlooi_api() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "manifest.json",
                        TestHelper.getManifest(1, "plooi-api", "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"));
                PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "plooi.json",
                        new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/_plooi"))
                        .andExpect(status().isOk());
                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/_plooi"))
                        .andExpect(status().isOk());
            }
        }

        @ParameterizedTest
        @CsvSource({
                "_text,     200, text                                                                        ",
                "_manifest, 200, {                                                                           ",
                "_plooi,    200, {                                                                           ",
                "_owms,     303,                                                                             ",
                "_relaties, 200, [{\"relation\":\"3c82b4053a77f958a51b2fb4783f28c8cb66d10d\",\"role\":\"https",
                "_versie,   200, {\"nummer\":1,\"oorzaak\":\"wijziging\",\"bestanden\":[{\"gepubliceerd\":fal",
                "_blokkades,200, []",
                "_bla,      404, Unknown label _bla for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a   ",
        })
        void getMetaFile(String getLabel, Integer status, String content) throws Exception {
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getManifest(1, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID));
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.json",
                    TestHelper.getPlooi(1, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID));
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.txt",
                    new ByteArrayInputStream("text".getBytes(StandardCharsets.UTF_8)));
            var result = PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/" + getLabel))
                    .andExpect(status().is(status));
            if (status < 300) {
                result.andExpect(content().string(startsWith(content)));
            } else if (content != null) {
                result.andExpect(content().string(content));
            }
        }
    }

    @Nested
    class PostMetaFile {

        @Test
        void noBody() throws Exception {
            PublicatieControllerTest.this.mockMvc
                    .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi"))
                    .andExpect(status().isUnsupportedMediaType());
            assertEquals("", TestHelper.reposUpdates());
        }

        @Test
        void unknownId() throws Exception {
            PublicatieControllerTest.this.mockMvc
                    .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_text")
                            .contentType(Storage.MIME_JSON)
                            .content("{\"some\" : \"data\"}".getBytes(StandardCharsets.UTF_8)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Unknown identifier plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
            assertEquals("", TestHelper.reposUpdates());
        }

        @Test
        void unknownLabel() throws Exception {
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            PublicatieControllerTest.this.mockMvc
                    .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/pdf")
                            .contentType(Storage.MIME_JSON)
                            .content("{\"some\" : \"data\"}".getBytes(StandardCharsets.UTF_8)))
                    .andExpect(status().isNotFound());
            assertEquals("manifest.json", TestHelper.reposUpdates());
        }

        @Nested
        class PostText {

            @Test
            void postText() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        TestHelper.getFirstManifest());
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_text")
                                .contentType(Storage.MIME_JSON)
                                .content("{\"some\" : \"data\"}".getBytes(StandardCharsets.UTF_8)))
                        .andExpect(status().isOk());
                assertEquals("manifest.json, plooi.txt", TestHelper.reposUpdates());
            }
        }

        @Nested
        class PostPlooi {

            @Test
            void postPlooi() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        TestHelper.getFirstManifest());
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi")
                                .contentType(Storage.MIME_JSON)
                                .content(TestHelper.getPlooi(1, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID).readAllBytes()))
                        .andExpect(status().isOk());
                assertEquals("manifest.json, plooi.json", TestHelper.reposUpdates());

                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.document.pid").value("https://open.overheid.nl/documenten/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                        .andExpect(jsonPath("$.plooiIntern.dcnId").value("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
            }

            @Test
            void postPlooi_mappedId() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("ronl-228036432630c580dcdddefd9c3a6de8e2587c1b", null, "manifest.json",
                        TestHelper.getManifest(1, "ronl", "0acb97b8-b3cf-4654-92c2-0fa4199c100c"));
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/ronl-228036432630c580dcdddefd9c3a6de8e2587c1b/_plooi")
                                .contentType(Storage.MIME_JSON)
                                .content(TestHelper.getPlooi(1, "ronl", "0acb97b8-b3cf-4654-92c2-0fa4199c100c").readAllBytes()))
                        .andExpect(status().isOk());
                assertEquals("manifest.json, plooi.json", TestHelper.reposUpdates());

                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3/_plooi"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.document.pid").value("https://open.overheid.nl/documenten/ronl-66fd6c85-7678-4b8b-8068-0777b9648fd3"))
                        .andExpect(jsonPath("$.plooiIntern.dcnId").value("ronl-228036432630c580dcdddefd9c3a6de8e2587c1b"));
            }

            @Test
            void postIllegalPlooi() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        TestHelper.getFirstManifest());
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_plooi")
                                .contentType(Storage.MIME_JSON)
                                .content("{\"some\" : \"data\"}".getBytes(StandardCharsets.UTF_8)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(startsWith("UnrecognizedPropertyException: Unrecognized field \"some\"")));
                assertEquals("manifest.json", TestHelper.reposUpdates());
            }
        }

        @Nested
        class PostBlokkades {

            @Test
            void addBlokkade() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        TestHelper.getFirstManifest());
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades")
                                .contentType(Storage.MIME_JSON)
                                .content("[ \"one\", \"two\" ]".getBytes(StandardCharsets.UTF_8)))
                        .andExpect(status().isOk());

                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[\"one\",\"two\"]"));
            }

            @Test
            void addBlokkade_illegal() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        TestHelper.getFirstManifest());
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades")
                                .contentType(Storage.MIME_JSON)
                                .content("[ \"_intrekking\" ]".getBytes(StandardCharsets.UTF_8)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(startsWith("illegal blokkade _intrekking")));
            }

            @Test
            void addBlokkade_addToExisting() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        new ByteArrayInputStream(PlooiBindings.plooiBinding()
                                .marshalToString(new Plooi()
                                        .plooiIntern(new PlooiIntern()
                                                .dcnId("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                                        .addVersiesItem(new Versie()
                                                .nummer(1)
                                                .oorzaak(OorzaakEnum.AANLEVERING)
                                                .blokkades(List.of("one"))))
                                .getBytes(StandardCharsets.UTF_8)));
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades")
                                .contentType(Storage.MIME_JSON)
                                .content("[ \"two\", \"three\" ]".getBytes(StandardCharsets.UTF_8)))
                        .andExpect(status().isOk());

                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[\"one\",\"two\",\"three\"]"));
            }

            @Test
            void addBlokkade_duplicate() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        new ByteArrayInputStream(PlooiBindings.plooiBinding()
                                .marshalToString(new Plooi()
                                        .plooiIntern(new PlooiIntern()
                                                .dcnId("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                                        .addVersiesItem(new Versie()
                                                .nummer(1)
                                                .oorzaak(OorzaakEnum.AANLEVERING)
                                                .blokkades(List.of("one"))))
                                .getBytes(StandardCharsets.UTF_8)));
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades")
                                .contentType(Storage.MIME_JSON)
                                .content("[ \"one\", \"two\" ]".getBytes(StandardCharsets.UTF_8)))
                        .andExpect(status().isOk());

                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[\"one\",\"two\"]"));
            }

            @Test
            void removeBlokkade() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        new ByteArrayInputStream(PlooiBindings.plooiBinding()
                                .marshalToString(new Plooi()
                                        .plooiIntern(new PlooiIntern()
                                                .dcnId("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                                        .addVersiesItem(new Versie()
                                                .nummer(1)
                                                .oorzaak(OorzaakEnum.AANLEVERING)
                                                .blokkades(List.of("one", "two"))))
                                .getBytes(StandardCharsets.UTF_8)));
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades")
                                .contentType(Storage.MIME_JSON)
                                .content("[ \"-one\" ]".getBytes(StandardCharsets.UTF_8)))
                        .andExpect(status().isOk());

                PublicatieControllerTest.this.mockMvc
                        .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("[\"two\"]"));
            }

            @Test
            void removeBlokkade_empty() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        TestHelper.getFirstManifest());
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades")
                                .contentType(Storage.MIME_JSON)
                                .content("[ \"-two\" ]".getBytes(StandardCharsets.UTF_8)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(startsWith("not existing blokkade two")));
            }

            @Test
            void removeBlokkade_notExisting() throws Exception {
                PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                        new ByteArrayInputStream(PlooiBindings.plooiBinding()
                                .marshalToString(new Plooi()
                                        .plooiIntern(new PlooiIntern()
                                                .dcnId("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                                        .addVersiesItem(new Versie()
                                                .nummer(1)
                                                .oorzaak(OorzaakEnum.AANLEVERING)
                                                .blokkades(List.of("one", "two"))))
                                .getBytes(StandardCharsets.UTF_8)));
                PublicatieControllerTest.this.mockMvc
                        .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_blokkades")
                                .contentType(Storage.MIME_JSON)
                                .content("[ \"-three\" ]".getBytes(StandardCharsets.UTF_8)))
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(startsWith("not existing blokkade three")));
            }
        }
    }

    @Nested
    class PostRelations {

        @Test
        void postRelations() throws Exception {
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            PublicatieControllerTest.this.mockMvc
                    .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_relaties/test")
                            .contentType(Storage.MIME_JSON)
                            .content("""
                                    [{
                                        "relation":"3c82b4053a77f958a51b2fb4783f28c8cb66d10d",
                                        "role":"https://identifier.overheid.nl/plooi/def/thes/documentrelatie/identiteitsgroep"
                                    },{
                                        "relation":"oep-14145d83864b4ea1233c0b01135b8a3f729c0b87",
                                        "role":"https://identifier.overheid.nl/plooi/def/thes/documentrelatie/bijlage"
                                    }]
                                    """))
                    .andExpect(status().isOk());
            // Nothing we can really test here
            assertEquals("manifest.json", TestHelper.reposUpdates());
        }
    }

    @Nested
    class Delete {

        @Test
        void deletionOk() throws Exception {
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.json",
                    new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.txt",
                    new ByteArrayInputStream("text".getBytes(StandardCharsets.UTF_8)));
            assertEquals("manifest.json, plooi.json, plooi.txt", TestHelper.reposUpdates());

            PublicatieControllerTest.this.mockMvc
                    .perform(delete("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isOk());
            // Meta data is deleted, so it cannot be processed anymore
            assertEquals("manifest.json", TestHelper.reposUpdates());

            PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_manifest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("intrekking"))
                    .andExpect(jsonPath("$.versies[2]").doesNotExist());
        }

        @Test
        void deletionOk_api() throws Exception {
            PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "manifest.json",
                    TestHelper.getManifest(1, "plooi-api", "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"));
            PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "plooi.json",
                    new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
            PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "plooi.txt",
                    new ByteArrayInputStream("text".getBytes(StandardCharsets.UTF_8)));
            assertEquals("manifest.json, plooi.json, plooi.txt", TestHelper.reposUpdates());

            PublicatieControllerTest.this.mockMvc
                    .perform(delete("/publicatie/ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"))
                    .andExpect(status().isOk());
            // Meta data is deleted, so it cannot be processed anymore
            assertEquals("manifest.json", TestHelper.reposUpdates());

            PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/_manifest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.plooiIntern.dcnId").value("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("intrekking"))
                    .andExpect(jsonPath("$.versies[2]").doesNotExist());
        }
    }

    @Nested
    class Restore {

        @Test
        void republishOk() throws Exception {
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getDeletedManifest());

            PublicatieControllerTest.this.mockMvc
                    .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                    .andExpect(status().isOk());
            // Meta data is deleted, so it cannot be processed anymore
            assertEquals("manifest.json", TestHelper.reposUpdates());

            PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_manifest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[2].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[3].oorzaak").value("intrekking"))
                    .andExpect(jsonPath("$.versies[4].oorzaak").value("herpublicatie"))
                    .andExpect(jsonPath("$.versies[4].nummer").value("3"))
                    .andExpect(jsonPath("$.versies[5]").doesNotExist());
        }

        @Test
        void republishWitVersionOk() throws Exception {
            PublicatieControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getDeletedManifest());

            PublicatieControllerTest.this.mockMvc
                    .perform(post("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a?versieNummer=1"))
                    .andExpect(status().isOk());
            // Meta data is deleted, so it cannot be processed anymore
            assertEquals("manifest.json", TestHelper.reposUpdates());

            PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/_manifest"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[2].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[3].oorzaak").value("intrekking"))
                    .andExpect(jsonPath("$.versies[4].oorzaak").value("herpublicatie"))
                    .andExpect(jsonPath("$.versies[4].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[5]").doesNotExist());
        }
    }

    @Nested
    class GetVersionedFile {

        @Test
        void getVersionedFile_unknownId() throws Exception {
            PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/1/meta"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Unknown file manifest.json for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void getVersionedFile_api() throws Exception {
            PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", null, "manifest.json",
                    TestHelper.getManifest(1, "plooi-api", "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"));
            PublicatieControllerTest.this.repositoryStore.store("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e", 1, "metadata.json",
                    new ByteArrayInputStream("META v1".getBytes(StandardCharsets.UTF_8)));
            PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/1/meta"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("META v1"));
            PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e/1/meta"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("META v1"));
        }

        @ParameterizedTest
        @CsvSource({
                "1, A, meta,     400, version number A",
                "1, 0, meta,     404, Unknown version number 0",
                "1, 1, meta,     200, META v1",
                "1, 1, document, 200, PDF v1",
                "1, 1, zip,      404, Unknown label zip",
                "1, 2, document, 404, Unknown version number 2",
                "1, -, meta,     200, META v1",
                "2, 0, document, 404, Unknown version number 0",
                "2, 1, meta,     200, META v1",
                "2, 1, document, 200, PDF v1",
                "2, 2, meta,     200, META v1",
                "2, 2, document, 200, PDF v2",
                "2, 3, document, 404, Unknown version number 3",
                "2, -, meta,     200, META v1",
                "3, 0, document, 404, Unknown version number 0",
                "3, 1, meta,     200, META v1",
                "3, 1, document, 200, PDF v1",
                "3, 2, meta,     200, META v1",
                "3, 2, document, 200, PDF v2",
                "3, 3, meta,     200, META v2",
                "3, 3, document, 200, PDF v2",
                "3, 4, document, 404, Unknown version number 4",
                "3, -, meta,     200, META v2",
                "4, 0, document, 404, Unknown version number 0",
                "4, 1, meta,     200, META v1",
                "4, 1, document, 200, PDF v1",
                "4, 2, meta,     200, META v1",
                "4, 2, document, 200, PDF v2",
                "4, 3, meta,     200, META v2",
                "4, 3, document, 200, PDF v2",
                "4, 4, document, 404, Unknown version number 4",
                "4, -, meta,     200, META v2",
                "5, 0, document, 404, Unknown version number 0",
                "5, 1, meta,     200, META v1",
                "5, 1, document, 200, PDF v1",
                "5, 2, meta,     200, META v1",
                "5, 2, document, 200, PDF v2",
                "5, 3, meta,     200, META v2",
                "5, 3, document, 200, PDF v2",
                "5, 4, document, 404, Unknown version number 4",
                "5, -, meta,     200, META v2",
                "5, -, document, 200, PDF v2",
        })
        void getVersionedFile(Integer currentVersion, String getVersion, String getLabel, Integer status, String content) throws Exception {
            var dcnId = "plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a";
            PublicatieControllerTest.this.repositoryStore.store(dcnId, null, "manifest.json",
                    TestHelper.getManifest(currentVersion, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID));
            PublicatieControllerTest.this.repositoryStore.store(dcnId, 1, "document.pdf",
                    new ByteArrayInputStream("PDF v1".getBytes(StandardCharsets.UTF_8)));
            PublicatieControllerTest.this.repositoryStore.store(dcnId, 1, "metadata.json",
                    new ByteArrayInputStream("META v1".getBytes(StandardCharsets.UTF_8)));
            PublicatieControllerTest.this.repositoryStore.store(dcnId, 2, "gewijzigd.pdf",
                    new ByteArrayInputStream("PDF v2".getBytes(StandardCharsets.UTF_8)));
            PublicatieControllerTest.this.repositoryStore.store(dcnId, 3, "gewijzigd.json",
                    new ByteArrayInputStream("META v2".getBytes(StandardCharsets.UTF_8)));
            var result = PublicatieControllerTest.this.mockMvc
                    .perform(get("/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a/" + getVersion + "/" + getLabel))
                    .andExpect(status().is(status));
            if (status < 300) {
                result.andExpect(content().string(startsWith(content)));
            } else if (content != null) {
                result.andExpect(content().string(startsWith(content)));
            }
        }
    }
}

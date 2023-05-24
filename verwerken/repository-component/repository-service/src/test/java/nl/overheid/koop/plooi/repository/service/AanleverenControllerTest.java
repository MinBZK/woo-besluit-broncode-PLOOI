package nl.overheid.koop.plooi.repository.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import nl.overheid.koop.plooi.repository.client.ApiClient;
import nl.overheid.koop.plooi.repository.client.ClientException;
import nl.overheid.koop.plooi.repository.storage.FilesystemStorage;
import nl.overheid.koop.plooi.service.error.HttpStatusExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = { TestConfiguration.class, AanleverenController.class, RepositoryExceptionHandler.class, HttpStatusExceptionHandler.class, })
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AanleverenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final FilesystemStorage repositoryStore = new FilesystemStorage(TestHelper.REPOS_DIR, false);
    private final AanleverenClient aanleverenClient = new AanleverenClient(new ApiClient());

    @BeforeEach
    void init() throws IOException {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
        FileUtils.deleteDirectory(new File(TestHelper.REPOS_DIR));
    }

    @Nested
    class Get {

        @Test
        void getLabels() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(get("/aanleveren/plooi-dcn/9495beca-ff69-4005-bb29-111111111111"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string(HttpHeaders.LOCATION, "/publicatie/plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void illegalRequest() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(post("/aanleveren/plooi-dcn/9495beca-ff69-4005-bb29-111111111111")
                            .contentType("application/pdf")
                            .content(new byte[] {}))
                    .andExpect(status().isUnsupportedMediaType());
            assertEquals("", TestHelper.reposUpdates());
        }
    }

    @Nested
    class PostAanlevering {

        @Test
        void noParts() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("At least one file is required for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
            assertEquals("", TestHelper.reposUpdates());
        }

        @Test
        void illegalVersionManifest() throws Exception {
            assertThrows(ClientException.class,
                    () -> AanleverenControllerTest.this.mockMvc
                            .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                                    .createRequest("""
                                            { .. }
                                            """,
                                            "plooi-dcn",
                                            "9495beca-ff69-4005-bb29-111111111111")
                                    .addPart("meta", "{\"some\" : \"data\"}")
                                    .addPart("document", new byte[] {})
                                    .build()))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(content().string("Can't parse version manifest for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
            );
            assertEquals("", TestHelper.reposUpdates());
        }

        @Test
        void newNoParts() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("At least one file is required for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
            assertEquals("", TestHelper.reposUpdates());
        }

        @Test
        void newNoFiles() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("UNKNOWN.meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("UNKNOWN.document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/UNKNOWN.document, 1/UNKNOWN.meta, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void newDelayedAndBlocks() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "blokkades" : [ "blocked" ],
                                        "zichtbaarheidsdatumtijd" : "2023-01-01T00:00:00.000Z"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].blokkades").value("blocked"))
                    .andExpect(jsonPath("$.versies[0].zichtbaarheidsdatumtijd").value("2023-01-01T00:00:00Z"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("UNKNOWN.meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("UNKNOWN.document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/UNKNOWN.document, 1/UNKNOWN.meta, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void newEmptyVersionManifest() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {}
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("UNKNOWN.meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("UNKNOWN.document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/UNKNOWN.document, 1/UNKNOWN.meta, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void newDuplicateLabel() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                          "label" : "meta",
                                          "bestandsnaam" : "metadata.json"
                                        }, {
                                          "label" : "document",
                                          "bestandsnaam" : "document.pdf"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .addPart("document", new byte[] {})
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isCreated()); // Last part is saved
            assertEquals("1/document.pdf, 1/metadata.json, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void newWithFiles() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                            "gepubliceerd" : true,
                                            "label" : "document",
                                            "bestandsnaam" : "echte_naam.pdf"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("echte_naam.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/echte_naam.pdf, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void newInconsistent() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                            "label" : "zip",
                                            "bestandsnaam" : "archief.zip"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Provided version does not have label for part meta for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void newMissingParts() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                            "label" : "meta",
                                            "bestandsnaam" : "metadata.json"
                                        }, {
                                            "label" : "document",
                                            "bestandsnaam" : "document.pdf"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Missing parts for label(s) document for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void newIncompleteVersion() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                            "label" : "meta",
                                            "bestandsnaam" : "metadata.json"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            content().string("Provided version does not have label for part document for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }
    }

    @Nested
    class PostAanleveringUpdate {

        @Test
        void new2callsOk() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610", null, "manifest.json",
                    new ByteArrayInputStream("""
                            {
                              "plooiIntern" : {
                                "dcnId" : "plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610",
                                "extId" : [ "9495beca-ff69-4005-bb29-111111112222" ],
                                "sourceLabel" : "plooi-dcn"
                              },
                              "versies" : [ {
                                "nummer" : 1,
                                "oorzaak" : "aanlevering",
                                "bestanden" : [ {
                                  "label" : "meta",
                                  "bestandsnaam" : "metadata.json",
                                  "hash" : "1db4d3ef91e3b7ab15089c0d08493fb1a8dc163c"
                                } ]
                              } ]
                            }
                            """.getBytes(StandardCharsets.UTF_8)));
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                            "label" : "document",
                                            "bestandsnaam" : "document.pdf"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111112222")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/document.pdf, manifest.json", TestHelper.reposUpdates()); // 1/meta.json isn't saved in this test
        }

        @Test
        void new2callsWithBlockOk() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610", null, "manifest.json",
                    new ByteArrayInputStream("""
                            {
                              "plooiIntern" : {
                                "dcnId" : "plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610",
                                "extId" : [ "9495beca-ff69-4005-bb29-111111112222" ],
                                "sourceLabel" : "plooi-dcn"
                              },
                              "versies" : [ {
                                "nummer" : 1,
                                "oorzaak" : "aanlevering",
                                "bestanden" : [ {
                                  "label" : "meta",
                                  "bestandsnaam" : "metadata.json",
                                  "hash" : "1db4d3ef91e3b7ab15089c0d08493fb1a8dc163c"
                                } ]
                              } ]
                            }
                            """.getBytes(StandardCharsets.UTF_8)));
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "blokkades" : [ "blocked" ],
                                        "bestanden" : [ {
                                            "label" : "document",
                                            "bestandsnaam" : "document.pdf"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111112222")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/document.pdf, manifest.json", TestHelper.reposUpdates()); // 1/meta.json isn't saved in this test
        }

        @Test
        void new2callsOverwrite() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610", null, "manifest.json",
                    new ByteArrayInputStream("""
                            {
                              "plooiIntern" : {
                                "dcnId" : "plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610",
                                "extId" : [ "9495beca-ff69-4005-bb29-111111112222" ],
                                "sourceLabel" : "plooi-dcn"
                              },
                              "versies" : [ {
                                "nummer" : 1,
                                "oorzaak" : "aanlevering",
                                "bestanden" : [ {
                                  "label" : "meta",
                                  "bestandsnaam" : "metadata.json",
                                  "hash" : "1db4d3ef91e3b7ab15089c0d08493fb1a8dc163c"
                                } ]
                              } ]
                            }
                            """.getBytes(StandardCharsets.UTF_8)));
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                            "label" : "meta"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111112222")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("UNKNOWN.meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/UNKNOWN.meta, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void new2callsInconsistent() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610", null, "manifest.json",
                    new ByteArrayInputStream("""
                            {
                              "plooiIntern" : {
                                "dcnId" : "plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610",
                                "extId" : [ "9495beca-ff69-4005-bb29-111111112222" ],
                                "sourceLabel" : "plooi-dcn"
                              },
                              "versies" : [ {
                                "nummer" : 1,
                                "oorzaak" : "aanlevering",
                                "bestanden" : [ {
                                  "label" : "meta",
                                  "bestandsnaam" : "metadata.json",
                                  "hash" : "1db4d3ef91e3b7ab15089c0d08493fb1a8dc163c"
                                } ]
                              } ]
                            }
                            """.getBytes(StandardCharsets.UTF_8)));
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                            "label" : "document"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111112222")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Provided version does not have label for part meta for plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610"));
            assertEquals("manifest.json", TestHelper.reposUpdates()); // 1/meta.json isn't saved in this test
        }

        @Test
        void new2callsAlreadyProcessed() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610", null, "manifest.json",
                    new ByteArrayInputStream("""
                            {
                              "plooiIntern" : {
                                "dcnId" : "plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610",
                                "extId" : [ "9495beca-ff69-4005-bb29-111111112222" ],
                                "sourceLabel" : "plooi-dcn"
                              },
                              "versies" : [ {
                                "nummer" : 1,
                                "oorzaak" : "aanlevering",
                                "bestanden" : [ {
                                  "label" : "meta",
                                  "bestandsnaam" : "metadata.json",
                                  "hash" : "1db4d3ef91e3b7ab15089c0d08493fb1a8dc163c"
                                } ]
                              } ]
                            }
                            """.getBytes(StandardCharsets.UTF_8)));
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610", null, "plooi.json",
                    new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                            "label" : "document"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111112222")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Already processed meta data for plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610"));
        }

        @Test
        void new2callsAlreadyDone() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610", null, "manifest.json",
                    new ByteArrayInputStream("""
                            {
                              "plooiIntern" : {
                                "dcnId" : "plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610",
                                "extId" : [ "9495beca-ff69-4005-bb29-111111112222" ],
                                "sourceLabel" : "plooi-dcn"
                              },
                              "versies" : [ {
                                "nummer" : 1,
                                "oorzaak" : "aanlevering",
                                "bestanden" : [ {
                                  "label" : "meta",
                                  "bestandsnaam" : "metadata.json",
                                  "hash" : "1db4d3ef91e3b7ab15089c0d08493fb1a8dc163c"
                                } ]
                              }, {
                                "nummer" : 2,
                                "oorzaak" : "wijziging",
                                "bestanden" : [ {
                                  "label" : "meta",
                                  "bestandsnaam" : "newmeta.json",
                                  "hash" : "1db4d3ef91e3b7ab15089c0d08493fb1a8dc1630"
                                } ]
                              } ]
                            }
                            """.getBytes(StandardCharsets.UTF_8)));
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "aanlevering",
                                        "bestanden" : [ {
                                            "label" : "document"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111112222")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Initial version is already done for plooi-dcn-431b0b04ef4a88d48e065f447dd673f9fe24b610"));
        }
    }

    @Nested
    class PostWijziging {

        @Test
        void updateNotFound() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Unknown document plooi-dcn/9495beca-ff69-4005-bb29-111111111111 "
                            + "for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void updateNoFiles() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1].nummer").value("2"))
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].label").value("document"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].bestandsnaam").value("UNKNOWN.document"))
                    .andExpect(jsonPath("$.versies[1].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[2]").doesNotExist());
            assertEquals("2/UNKNOWN.document, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void updateNoParts() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("At least one file is required for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
            assertEquals("manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void updateDelayedAndBlocks() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging",
                                        "blokkades" : [ "blocked" ],
                                        "zichtbaarheidsdatumtijd" : "2023-01-01T00:00:00.000Z"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1].nummer").value("2"))
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[1].blokkades").value("blocked"))
                    .andExpect(jsonPath("$.versies[1].zichtbaarheidsdatumtijd").value("2023-01-01T00:00:00Z"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].label").value("document"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].bestandsnaam").value("UNKNOWN.document"))
                    .andExpect(jsonPath("$.versies[1].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[2]").doesNotExist());
            assertEquals("2/UNKNOWN.document, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void updateEmptyVersionManifest() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {}
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1].nummer").value("2"))
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].label").value("document"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].bestandsnaam").value("UNKNOWN.document"))
                    .andExpect(jsonPath("$.versies[1].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[2]").doesNotExist());
            assertEquals("2/UNKNOWN.document, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void updateDuplicateLabel() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("document", new byte[] {})
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Duplicate label document for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
            assertEquals("2/UNKNOWN.document, manifest.json", TestHelper.reposUpdates()); // some garbage, manifest not changed
        }

        @Test
        void updateUnknownLabel() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("wrong", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Not existing label wrong for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
            assertEquals("manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void updateWithFiles() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging",
                                        "bestanden" : [ {
                                            "gepubliceerd" : true,
                                            "label" : "document",
                                            "bestandsnaam" : "echte_naam.pdf"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1].nummer").value("2"))
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].label").value("document"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].bestandsnaam").value("echte_naam.pdf"))
                    .andExpect(jsonPath("$.versies[1].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[2]").doesNotExist());
            assertEquals("2/echte_naam.pdf, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void updateOneUnchanged() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging",
                                        "bestanden" : [ {
                                            "label" : "meta",
                                            "bestandsnaam" : "unchanged_metadata.json"
                                        }, {
                                            "gepubliceerd" : true,
                                            "label" : "document",
                                            "bestandsnaam" : "new_document.pdf"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1].nummer").value("2"))
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].label").value("document"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0].bestandsnaam").value("new_document.pdf"))
                    .andExpect(jsonPath("$.versies[1].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[2]").doesNotExist());
            assertEquals("2/new_document.pdf, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void updateAllUnchanged() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"some\" : \"data\"}")
                            .build()))
                    .andExpect(status().isNotModified())
                    .andExpect(content().string("Content did not change for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
            assertEquals("manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void updateInconsistent() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging",
                                        "bestanden" : [ {
                                            "label" : "zip",
                                            "bestandsnaam" : "archief.zip"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"something\" : \"else\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Provided version does not have label for part meta for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void updateMissingParts() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging",
                                        "bestanden" : [ {
                                            "label" : "meta",
                                            "bestandsnaam" : "new_metadata.json"
                                        }, {
                                            "label" : "document",
                                            "bestandsnaam" : "new_document.pdf"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"something\" : \"else\"}")
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Missing parts for label(s) document for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void updateIncompleteVersion() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging",
                                        "bestanden" : [ {
                                            "label" : "meta",
                                            "bestandsnaam" : "new_metadata.json"
                                        } ]
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("meta", "{\"something\" : \"else\"}")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            content().string("Provided version does not have label for part document for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }
    }

    @Nested
    class PostIntrekking {

        @Test
        void deletionOk() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.json",
                    new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "plooi.txt",
                    new ByteArrayInputStream("text".getBytes(StandardCharsets.UTF_8)));
            assertEquals("manifest.json, plooi.json, plooi.txt", TestHelper.reposUpdates());

            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "intrekking",
                                        "redenVerwijderingVervanging" : "document deugt niet"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1].nummer").doesNotExist())
                    .andExpect(jsonPath("$.versies[1].oorzaak").value("intrekking"))
                    .andExpect(jsonPath("$.versies[1].bestanden[0]").doesNotExist())
                    .andExpect(jsonPath("$.versies[2]").doesNotExist());
            // Meta data is deleted, so it cannot be processed anymore
            assertEquals("manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void deletionWithParts() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "intrekking",
                                        "redenVerwijderingVervanging" : "document deugt niet"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("intrekking action cannot post files for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void deletionAlreadyDeleted() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getDeletedManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "intrekking"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Document is already deleted for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void deletionUpdate() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getDeletedManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "wijziging"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[3].nummer").doesNotExist())
                    .andExpect(jsonPath("$.versies[3].oorzaak").value("intrekking"))
                    .andExpect(jsonPath("$.versies[3].bestanden[0]").doesNotExist())
                    .andExpect(jsonPath("$.versies[4].nummer").value("4"))
                    .andExpect(jsonPath("$.versies[4].oorzaak").value("wijziging"))
                    .andExpect(jsonPath("$.versies[4].bestanden[0].label").value("document"))
                    .andExpect(jsonPath("$.versies[4].bestanden[0].bestandsnaam").value("UNKNOWN.document"))
                    .andExpect(jsonPath("$.versies[5]").doesNotExist());
        }
    }

    @Nested
    class PostHerpublicatie {

        @Test
        void republishOk() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getDeletedManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "herpublicatie",
                                        "redenVerwijderingVervanging" : "document is toch wel ok"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[3].nummer").doesNotExist())
                    .andExpect(jsonPath("$.versies[3].oorzaak").value("intrekking"))
                    .andExpect(jsonPath("$.versies[3].bestanden[0]").doesNotExist())
                    .andExpect(jsonPath("$.versies[4].nummer").value("3"))
                    .andExpect(jsonPath("$.versies[4].oorzaak").value("herpublicatie"))
                    .andExpect(jsonPath("$.versies[4].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[4].bestanden[0].bestandsnaam").value("gewijzigd.json"))
                    .andExpect(jsonPath("$.versies[4].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[4].bestanden[1].bestandsnaam").value("gewijzigd.pdf"))
                    .andExpect(jsonPath("$.versies[5]").doesNotExist());
        }

        @Test
        void republishVersion() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getManifest(4, TestHelper.DEFAULT_SRC, TestHelper.DEFAULT_EXTID));
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "nummer" : "2",
                                        "oorzaak" : "herpublicatie",
                                        "redenVerwijderingVervanging" : "vorige was beter"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .build()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("meta"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("document"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("document.pdf"))
                    .andExpect(jsonPath("$.versies[4].nummer").value("2"))
                    .andExpect(jsonPath("$.versies[4].oorzaak").value("herpublicatie"))
                    .andExpect(jsonPath("$.versies[4].bestanden[0].label").value("document"))
                    .andExpect(jsonPath("$.versies[4].bestanden[0].bestandsnaam").value("gewijzigd.pdf"))
                    .andExpect(jsonPath("$.versies[4].bestanden[1].label").value("meta"))
                    .andExpect(jsonPath("$.versies[4].bestanden[1].bestandsnaam").value("metadata.json"))
                    .andExpect(jsonPath("$.versies[5]").doesNotExist());
        }

        @Test
        void republishIllegal() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "herpublicatie",
                                        "redenVerwijderingVervanging" : "nog niets gepubliceerd"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .build()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void republishWithParts() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getDeletedManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "herpublicatie"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .addPart("document", new byte[] {})
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("herpublicatie action cannot post files for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

        @Test
        void republishNotDeleted() throws Exception {
            AanleverenControllerTest.this.repositoryStore.store("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a", null, "manifest.json",
                    TestHelper.getFirstManifest());
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "oorzaak" : "herpublicatie",
                                        "redenVerwijderingVervanging" : "deze zou een versienummer moeten hebben"
                                    }
                                    """,
                                    "plooi-dcn",
                                    "9495beca-ff69-4005-bb29-111111111111")
                            .build()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Document is not deleted for plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
        }

    }

    @Nested
    class PostSpecifics {

        @Test
        void apiMeta() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                      "oorzaak" : "aanlevering"
                                    }
                                    """,
                                    "plooi-api",
                                    "ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e")
                            .addPart("metadata_v1", """
                                    { "document" : ... }
                                    """.getBytes(StandardCharsets.UTF_8))
                            .build()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.plooiIntern.extId[0]").value("ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"))
                    .andExpect(jsonPath("$.plooiIntern.extId[1]").doesNotExist())
                    .andExpect(jsonPath("$.plooiIntern.dcnId").value("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"))
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("metadata_v1"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("UNKNOWN.metadata_v1"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/UNKNOWN.metadata_v1, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void ronlBundle() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "bestanden" : [ {
                                            "gepubliceerd" : false,
                                            "label" : "xml",
                                            "url" : "https://opendata.rijksoverheid.nl/sources/rijksoverheid/documents/0acb97b8-b3cf-4654-92c2-0fa4199c100c",
                                            "mime-type" : "application/xml",
                                            "bestandsnaam" : "0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml",
                                            "titel" : "Akte van aanstelling 001474"
                                        } ]
                                    }
                                    """,
                                    "ronl",
                                    "0acb97b8-b3cf-4654-92c2-0fa4199c100c")
                            .addPart("xml", "<document />")
                            .build()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.plooiIntern.extId[0]").value("0acb97b8-b3cf-4654-92c2-0fa4199c100c"))
                    .andExpect(jsonPath("$.plooiIntern.extId[1]").doesNotExist())
                    .andExpect(jsonPath("$.plooiIntern.dcnId").value("ronl-228036432630c580dcdddefd9c3a6de8e2587c1b"))
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("xml"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void ronlPart() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "bestanden" : [ {
                                            "gepubliceerd" : false,
                                            "label" : "xml",
                                            "url" : "https://opendata.rijksoverheid.nl/sources/rijksoverheid/documents/0acb97b8-b3cf-4654-92c2-0fa4199c100c",
                                            "mime-type" : "application/xml",
                                            "bestandsnaam" : "0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml",
                                            "titel" : "Akte van aanstelling 001474"
                                        }, {
                                            "gepubliceerd" : true,
                                            "label" : "pdf",
                                            "url" : "https://opendata.rijksoverheid.nl/../akte-van-aanstelling/Akte+van+aanstelling_001474.pdf",
                                            "mime-type" : "application/pdf",
                                            "bestandsnaam" : "Akte van aanstelling_001474.pdf",
                                            "titel" : "Akte van aanstelling"
                                        } ]
                                    }
                                    """,
                                    "ronl",
                                    "0acb97b8-b3cf-4654-92c2-0fa4199c100c",
                                    "Akte van aanstelling_001474.pdf")
                            .addPart("xml", "<document />")
                            .addPart("pdf", new byte[] {})
                            .build()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.plooiIntern.extId[0]").value("0acb97b8-b3cf-4654-92c2-0fa4199c100c"))
                    .andExpect(jsonPath("$.plooiIntern.extId[1]").value("Akte van aanstelling_001474.pdf"))
                    .andExpect(jsonPath("$.plooiIntern.extId[2]").doesNotExist())
                    .andExpect(jsonPath("$.plooiIntern.dcnId").value("ronl-684333ccbc1afcdb275529c54261dff4061180ea"))
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("xml"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].label").value("pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1].bestandsnaam").value("Akte van aanstelling_001474.pdf"))
                    .andExpect(jsonPath("$.versies[0].bestanden[2]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml, 1/Akte van aanstelling_001474.pdf, manifest.json", TestHelper.reposUpdates());
        }

        @Test
        void roo() throws Exception {
            AanleverenControllerTest.this.mockMvc
                    .perform(Java11MockRequestBuilders.buildMockRequest(AanleverenControllerTest.this.aanleverenClient
                            .createRequest("""
                                    {
                                        "bestanden" : [ {
                                            "gepubliceerd" : false,
                                            "label" : "xml",
                                            "url" : "https://zoekservice.overheid.nl/sru/Search?x-connection=oo&operation=searchRetrieve&version=2.0&etc",
                                            "mime-type" : "application/xml",
                                            "bestandsnaam" : "https___identifier.overheid.nl_tooi_id_ministerie_mnre1045.xml"
                                        } ]
                                    }
                                    """,
                                    "roo",
                                    "https://identifier.overheid.nl/tooi/id/ministerie/mnre1045")
                            .addPart("xml", "<document />")
                            .build()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.plooiIntern.extId[0]").value("https://identifier.overheid.nl/tooi/id/ministerie/mnre1045"))
                    .andExpect(jsonPath("$.plooiIntern.extId[1]").doesNotExist())
                    .andExpect(jsonPath("$.plooiIntern.dcnId").value("roo-0aa91110d67b0907de3322a27010c3ede0060b92"))
                    .andExpect(jsonPath("$.versies[0].nummer").value("1"))
                    .andExpect(jsonPath("$.versies[0].oorzaak").value("aanlevering"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].label").value("xml"))
                    .andExpect(jsonPath("$.versies[0].bestanden[0].bestandsnaam").value("https___identifier.overheid.nl_tooi_id_ministerie_mnre1045.xml"))
                    .andExpect(jsonPath("$.versies[0].bestanden[1]").doesNotExist())
                    .andExpect(jsonPath("$.versies[1]").doesNotExist());
            assertEquals("1/https___identifier.overheid.nl_tooi_id_ministerie_mnre1045.xml, manifest.json", TestHelper.reposUpdates());
        }
    }
}

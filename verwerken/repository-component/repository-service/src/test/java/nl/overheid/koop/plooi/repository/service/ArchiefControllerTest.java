package nl.overheid.koop.plooi.repository.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;
import nl.overheid.koop.plooi.dcn.process.service.VerwerkenClient;
import nl.overheid.koop.plooi.repository.storage.FilesystemStorage;
import nl.overheid.koop.plooi.service.error.HttpStatusExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = { TestConfiguration.class, ArchiefController.class, RepositoryExceptionHandler.class, HttpStatusExceptionHandler.class, })
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ArchiefControllerTest {

    @MockBean
    private VerwerkenClient verwerkenClient;

    @Autowired
    private MockMvc mockMvc;

    private FilesystemStorage repositoryStore = new FilesystemStorage(TestHelper.REPOS_DIR, false);

    @BeforeEach
    void init() throws IOException {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
        FileUtils.deleteDirectory(new File(TestHelper.REPOS_DIR));
    }

    @Test
    void exportToLocation() throws Exception {
        createRepos("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a");
        var archief = new File(TestHelper.REPOS_DIR + ".zip");
        FileUtils.deleteQuietly(archief);
        this.mockMvc
                .perform(get("/archief/export?location=file:" + archief.getAbsolutePath()))
                .andExpect(status().isOk());
        Thread.sleep(500);
        assertZip(new FileInputStream(archief), "plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a");
    }

    @Test
    void exportZip() throws Exception {
        createRepos("plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a");
        this.mockMvc
                .perform(get("/archief/export?dcnId=plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"))
                .andExpect(status().isOk())
                .andExpect(result -> assertZip(
                        new ByteArrayInputStream(result.getResponse().getContentAsByteArray()), "plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a"));
    }

    @Test
    void exportZip_api() throws Exception {
        createRepos("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e");
        this.mockMvc
                .perform(get("/archief/export?dcnId=plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"))
                .andExpect(status().isOk())
                .andExpect(result -> assertZip(
                        new ByteArrayInputStream(result.getResponse().getContentAsByteArray()), "plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"));
    }

    private void createRepos(String dcnId) {
        this.repositoryStore.store(dcnId, null, "manifest.json", TestHelper.getFirstManifest());
        this.repositoryStore.store(dcnId, 1, "doc.pdf", new ByteArrayInputStream("PDF".getBytes(StandardCharsets.UTF_8)));
        this.repositoryStore.store(dcnId, 2, "doc.pdf", new ByteArrayInputStream("PDF".getBytes(StandardCharsets.UTF_8)));
    }

    private void assertZip(InputStream stream, String dcnId) throws IOException {
        try (var zipFile = new ZipInputStream(stream)) {
            var entries = new TreeSet<>();
            ZipEntry entry;
            while ((entry = zipFile.getNextEntry()) != null) {
                entries.add(entry.getName());
            }
            assertEquals(Stream.of("/1/doc.pdf", "/2/doc.pdf", "/manifest.json").map(f -> dcnId + f).toList(), new ArrayList<>(entries));
        }
    }

    @Test
    void importFromLocation() throws Exception {
        var archief = new File(TestHelper.REPOS_DIR + ".zip");
        FileUtils.deleteQuietly(archief);
        createZip(new FileOutputStream(archief), "plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a");
        this.mockMvc
                .perform(get("/archief/import?location=file:" + archief.getAbsolutePath()))
                .andExpect(status().isOk());
        Thread.sleep(500);
        assertEquals("1/doc.pdf, 2/doc.pdf, manifest.json", TestHelper.reposUpdates());
    }

    @Test
    void importZip() throws Exception {
        var archief = createZip(new ByteArrayOutputStream(), "plooi-dcn-d84bc2127c43a0fdc443b09b2b143180641aa47a").toByteArray();
        this.mockMvc
                .perform(post("/archief/import")
                        .contentType("application/zip")
                        .content(archief))
                .andExpect(status().isOk());
        assertEquals("1/doc.pdf, 2/doc.pdf, manifest.json", TestHelper.reposUpdates());
    }

    @Test
    void importZip_api() throws Exception {
        var archief = createZip(new ByteArrayOutputStream(), "plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e").toByteArray();
        this.mockMvc
                .perform(post("/archief/import")
                        .contentType("application/zip")
                        .content(archief))
                .andExpect(status().isOk());
        assertEquals("1/doc.pdf, 2/doc.pdf, manifest.json", TestHelper.reposUpdates());
    }

    private <T extends OutputStream> T createZip(T toZip, String dcnId) throws IOException {
        try (var zipStream = new ZipOutputStream(toZip)) {
            zipStream.putNextEntry(new ZipEntry(dcnId + "/manifest.json"));
            zipStream.write(TestHelper.getFirstManifest().readAllBytes());
            zipStream.putNextEntry(new ZipEntry(dcnId + "/1/doc.pdf"));
            zipStream.write("PDF".getBytes());
            zipStream.putNextEntry(new ZipEntry(dcnId + "/2/doc.pdf"));
            zipStream.write("PDF".getBytes());
            zipStream.closeEntry();
            return toZip;
        }
    }

    @Captor
    private ArgumentCaptor<List<String>> idListCaptor;

    @Test
    void process() throws Exception {
        createRepos("plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e");
        createRepos("plooi-api-ce0e9a52-a2a4-4e30-a22c-000000000002");
        createRepos("plooi-api-ce0e9a52-a2a4-4e30-a22c-000000000003");
        this.mockMvc
                .perform(post("/archief/verwerken/VERWERKING/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isOk());
        Thread.sleep(500);
        verify(this.verwerkenClient, times(2)).process(eq(
                VerwerkingActies.VERWERKING),
                eq("00000000-0000-0000-0000-000000000000"),
                this.idListCaptor.capture());
        assertEquals(this.idListCaptor.getAllValues().size(), 2); // Two invocations of the process service;
        assertEquals(this.idListCaptor.getAllValues().get(0).size(), 2); // 1st invocation with 2 ids
        assertEquals(this.idListCaptor.getAllValues().get(1).size(), 1); // 2nd invocation with 1 id
        assertEquals(
                List.of("plooi-api-ce0e9a52-a2a4-4e30-a22c-000000000002",
                        "plooi-api-ce0e9a52-a2a4-4e30-a22c-000000000003",
                        "plooi-api-ce0e9a52-a2a4-4e30-a22c-1b7b4deb917e"),
                this.idListCaptor.getAllValues()
                        .stream()
                        .flatMap(List::stream)
                        .sorted()
                        .toList());
    }
}

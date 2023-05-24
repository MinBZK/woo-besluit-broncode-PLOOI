package nl.overheid.koop.plooi.dcn.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import nl.overheid.koop.plooi.dcn.admin.model.BulkActionRequest;
import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.TriggerType;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentService;
import nl.overheid.koop.plooi.dcn.solr.models.SolrSearchRequest;
import nl.overheid.koop.plooi.test.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock(lenient = true)
    private AdminService adminService;

    @Mock(lenient = true)
    private SolrDocumentService solrDocumentService;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new AdminController(this.adminService, this.solrDocumentService))
                .build();
    }

    @Test
    void redoMappingErrorDocuments() throws Exception {
        String sourceName = "oep";
        String targetElementName = "target";
        String sourceLabel = "label";
        this.mockMvc.perform(request(HttpMethod.POST, new URI("/admin/redo-mapping-error-docs"))
                .param("sourceName", sourceName)
                .param("targetElementName", targetElementName)
                .param("sourceLabel", sourceLabel)
                .param("severity", "WARNING"))
                .andExpect(status().is(200));
    }

    @Test
    void redoDocument() throws Exception {
        this.mockMvc.perform(request(HttpMethod.POST, new URI("/admin/redo/1234")))
                .andExpect(status().is(200));
    }

    @Test
    void redoAllSourceDocument() throws Exception {
        when(this.adminService.redoSourceDocuments("1234")).thenReturn(Boolean.TRUE);
        this.mockMvc.perform(request(HttpMethod.POST, new URI("/admin/redo/source/1234")))
                .andExpect(content().string("true"))
                .andExpect(status().is(200));
    }

    @Test
    void unpublishDocument() throws Exception {
        this.mockMvc.perform(request(HttpMethod.POST, new URI("/admin/unpublish/1234")))
                .andExpect(status().is(200));
    }

    @Test
    void publishDocument() throws Exception {
        this.mockMvc.perform(request(HttpMethod.POST, new URI("/admin/process/1234")))
                .andExpect(status().is(200));
    }

    @Test
    void getSolrDocument() throws Exception {
        this.mockMvc.perform(request(HttpMethod.GET, new URI("/admin/solrDocument/query")))
                .andExpect(status().is(200));
    }

    @Test
    void getSolrSources() throws Exception {
        this.mockMvc.perform(request(HttpMethod.GET, new URI("/admin/solrSources")))
                .andExpect(status().is(200));
    }

    @Test
    void applyBulkAction() throws Exception {
        SolrSearchRequest filter = new SolrSearchRequest(null, new ArrayList<>(), null, null);
        BulkActionRequest bulkActionRequest = new BulkActionRequest(TriggerType.INGRESS, filter, "reason", "action");
        ObjectMapper objectMapper = new ObjectMapper();
        this.mockMvc.perform(request(HttpMethod.POST, new URI("/admin/bulkAction"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bulkActionRequest)))
                .andExpect(status().is(200));
    }

    @Test
    void getSolrDocumentsResponse() throws Exception {
        SolrSearchRequest filter = new SolrSearchRequest(null, new ArrayList<>(), null, null);
        var page = PageRequest.of(1, 1);

        HashMap<String, String> solrDocument1 = new HashMap<>();
        solrDocument1.put("available_start", "2022-02-11T17:52:31.666833");
        solrDocument1.put("creator", "Ministerie Van Financiën");
        solrDocument1.put("dcn_id", "oep-b32cb674a416d7474d4630d3bc460c81bc1c49de");
        solrDocument1.put("type", "kamer stuk");
        solrDocument1.put("topthema", "begroting");
        solrDocument1.put("title", "begrotingsstaat van het Ministerie van Financiën");

        HashMap<String, String> solrDocument2 = new HashMap<>();
        solrDocument2.put("available_start", "2022-02-11T17:54:12.681765");
        solrDocument2.put("creator", "Ministerie Van Economische Zaken En Klimaat");
        solrDocument2.put("dcn_id", "oep-b32cb674a416d7474d4630d3bc460c81bc1c49de");
        solrDocument2.put("type", "Kamerstuk");
        solrDocument2.put("topthema", "Onbekend");
        solrDocument2.put("title", "Uitstelbrief antwoorden Kamervragen over de gebrekkige postbezorging in Den Haag");

        var ls = List.of(solrDocument1, solrDocument2);
        String[] ADMIN_DOCUMENT_FIELDS = { "available_start", "creator", "dcn_id", "type", "topthema", "title", "identifier", "timestamp" };

        when(this.solrDocumentService.getDocuments(filter, page, ADMIN_DOCUMENT_FIELDS)).thenReturn(ResponseEntity.of(Optional.of(ls)));

        ObjectMapper objectMapper = new ObjectMapper();
        this.mockMvc.perform(request(HttpMethod.POST, new URI("/admin/solrDocuments"))
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "1")
                .param("size", "1")
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().is(200))
                .andExpect(content().json(TestUtils.readFileAsString(getClass(), "solr-documents.json")));
    }

    @Test
    void getPortalUrl() throws Exception {
        String portalUrl = "https://open.overheid.nl";
        when(this.adminService.getPortalUrl()).thenReturn(portalUrl);
        this.mockMvc.perform(request(HttpMethod.GET, new URI("/admin/portalUrl")))
                .andExpect(status().is(200))
                .andExpect(content().string(portalUrl));
    }

    @Test
    void getPublishingStates() throws Exception {
        PublishingState publishingState1 = new PublishingState("1", PublishingState.State.INPROGRESS);
        PublishingState publishingState2 = new PublishingState("2", PublishingState.State.INPROGRESS);

        List<PublishingState> publishStates = List.of(publishingState1, publishingState2);
        Page<PublishingState> publishStatesLPage = new PageImpl<>(publishStates);
        when(this.adminService.getPublishingStateSummaryOlderThanDate(any(OffsetDateTime.class), any(PageRequest.class))).thenReturn(publishStatesLPage);
        this.mockMvc.perform(request(HttpMethod.GET, new URI("/admin/getPublishingStates"))
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().is(200))
                .andDo(e -> System.out.println(e.getResponse().getContentAsString()))
                .andExpect(content().json(TestUtils.readFileAsString(getClass(), "publishing-state.json")));

    }
}

package nl.overheid.koop.dcn.publishingstate;

import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.PublishingStateRepository;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentService;
import org.apache.solr.common.SolrDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.stream.Stream;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishingStateServiceTest {
    private PublishingStateService publishingStateService;
    @Mock
    private PublishingStateRepository publishingStateRepository;

    @Mock
    private SolrDocumentService solrDocumentService;
    protected static final String[] FIND_ID_FIELD = { "dcn_id" };

    @BeforeEach
    void initiatePublishingStateService() {
        this.publishingStateService = new PublishingStateService(this.publishingStateRepository, this.solrDocumentService);
    }

    @Test
    void publishStateNotUpdatedWhenNoDocumentMatchInSolr() {

        when(this.publishingStateRepository.getInProgressPublishingState()).thenReturn(Stream.of("1"));
        when(this.solrDocumentService.getIndexedDocument("1", FIND_ID_FIELD)).thenReturn(Stream.empty());

        this.publishingStateService.processPublishingStates();
        verify(this.publishingStateRepository, never()).updateAndSave(anyString(), any(PublishingState.State.class));
    }

    @Test
    void publishStateUpdateOnlyDocumentMatchInSolr() {
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("dcn_id", "1");

        when(this.publishingStateRepository.getInProgressPublishingState()).thenReturn(Stream.of("1", "2", "3"));
        when(this.solrDocumentService.getIndexedDocument("1", FIND_ID_FIELD)).thenReturn(Stream.of(solrDocument));

        this.publishingStateService.processPublishingStates();
        verify(this.publishingStateRepository).updateAndSave("1", PublishingState.State.OK);
        verify(this.publishingStateRepository, never()).updateAndSave("2", PublishingState.State.OK);
        verify(this.publishingStateRepository, never()).updateAndSave("3", PublishingState.State.OK);
    }

    @Test
    void orderInStreamDoesNotMatterToUpdatePublishState() {
        SolrDocument solrDocument = new SolrDocument();
        solrDocument.setField("dcn_id", "9");

        when(this.publishingStateRepository.getInProgressPublishingState()).thenReturn(Stream.of("1", "2", "3", "7", "9"));
        when(this.solrDocumentService.getIndexedDocument(anyString(), any(String[].class))).thenAnswer(
                invocation -> {
                    if (invocation.getArguments()[0].equals("9")) {
                        return Stream.of(solrDocument);
                    } else return Stream.empty();
                }
        );
        this.publishingStateService.processPublishingStates();
        verify(this.publishingStateRepository).updateAndSave("9", PublishingState.State.OK);
    }
}
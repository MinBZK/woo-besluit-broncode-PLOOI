package nl.overheid.koop.plooi.dcn.repository.store;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.dcn.model.PublishingState;
import nl.overheid.koop.plooi.dcn.model.PublishingStateRepository;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.repository.client.AanleverenClient;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlooiDeliveryStoringTest {

    @Mock
    private PublishingStateRepository publishingStateRepository;
    @Mock
    private AanleverenClient aanleverenClient;
    @Mock
    private AanleverenClient.Request aanleverRequest;
    @Mock
    private PublicatieClient publicatieClient;

    private PlooiDeliveryStoring plooiRepositoryStoring;

    @BeforeEach
    void init() {
        when( this.aanleverenClient.createRequest(any(Versie.class), anyString(), any())).thenReturn(this.aanleverRequest);
        this.plooiRepositoryStoring = new PlooiDeliveryStoring(this.aanleverenClient, this.publicatieClient, this.publishingStateRepository);
    }

    @Test
    void documentWithoutPublishStateShouldSavedAndCreateNewToDoPublishStateWithStateToDo() {
        var dcnId = "ronl-16bac8e0eb30525cc2655d53d4fb50b5acabfb30";

        Optional<PublishingState> publishingState = Optional.empty();
        var target = new DeliveryEnvelope("ronl", "5afce6ec-a4ed-4c62-a6d5-168a59247678");
        when(this.publishingStateRepository.findById(dcnId)).thenReturn(publishingState);
        when(this.aanleverRequest.post()).thenReturn(Optional.of(new Plooi()));
        this.plooiRepositoryStoring.process(target);
        assertFalse(target.status().isDiscarded());
        verify(this.publishingStateRepository, times(1)).updateAndSave(dcnId, PublishingState.State.TODO);
    }

    @Test
    void documentWitConfirmedPublishStateShouldSavedAndUpdatePublishStateToToDo() {
        var dcnId = "ronl-16bac8e0eb30525cc2655d53d4fb50b5acabfb30";

        PublishingState publishingState = new PublishingState(dcnId, PublishingState.State.OK);
        var target = new DeliveryEnvelope("ronl", "5afce6ec-a4ed-4c62-a6d5-168a59247678");
        when(this.publishingStateRepository.findById(dcnId)).thenReturn(Optional.of(publishingState));
        when(this.aanleverRequest.post()).thenReturn(Optional.of(new Plooi()));
        this.plooiRepositoryStoring.process(target);
        assertFalse(target.status().isDiscarded());
        verify(this.publishingStateRepository, times(1)).updateAndSave(dcnId, PublishingState.State.TODO);
    }

    @Test
    void existingConfirmedDocumentWillBeDiscardedAndPublishingStateRemainUnchanged() {
        var dcnId = "ronl-16bac8e0eb30525cc2655d53d4fb50b5acabfb30";

        PublishingState publishingState = new PublishingState(dcnId, PublishingState.State.OK);
        var target = new DeliveryEnvelope("ronl", "5afce6ec-a4ed-4c62-a6d5-168a59247678");
        when(this.publishingStateRepository.findById(dcnId)).thenReturn(Optional.of(publishingState));
        when(this.aanleverRequest.post()).thenReturn(Optional.empty());
        this.plooiRepositoryStoring.process(target);
        assertTrue(target.status().isDiscarded());
        verify(this.publishingStateRepository, times(0)).updateAndSave(dcnId, PublishingState.State.TODO);
    }

    @Test
    void newConfirmedDocumentWillNotBeDiscardedPublishStateSetBackToDo() {
        var dcnId = "ronl-16bac8e0eb30525cc2655d53d4fb50b5acabfb30";

        PublishingState publishingState = new PublishingState(dcnId, PublishingState.State.OK);
        var target = new DeliveryEnvelope("ronl", "5afce6ec-a4ed-4c62-a6d5-168a59247678");
        when(this.publishingStateRepository.findById(dcnId)).thenReturn(Optional.of(publishingState));
        when(this.aanleverRequest.post()).thenReturn(Optional.of(new Plooi()));
        this.plooiRepositoryStoring.process(target);
        assertFalse(target.status().isDiscarded());
        verify(this.publishingStateRepository, times(1)).updateAndSave(dcnId, PublishingState.State.TODO);

    }
}

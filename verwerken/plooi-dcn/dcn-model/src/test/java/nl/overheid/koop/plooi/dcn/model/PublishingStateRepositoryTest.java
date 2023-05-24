package nl.overheid.koop.plooi.dcn.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class PublishingStateRepositoryTest {

    @Autowired
    private PublishingStateRepository publishingStateRepository;

    @Test
    void updateExistingPublishStateToNewState() {
        var dcnId = "oep-42f5fcf361d7269c56c4a456fd2f1d3b8b25954f";
        var newPublishingState = new PublishingState(dcnId, PublishingState.State.TODO);
        this.publishingStateRepository.save(newPublishingState);
        var publishingState = this.publishingStateRepository.findById(dcnId);
        assertTrue(publishingState.isPresent());
        assertEquals(PublishingState.State.TODO, publishingState.get().getIndexed());

        this.publishingStateRepository.updateAndSave(dcnId, PublishingState.State.INPROGRESS);

        publishingState = this.publishingStateRepository.findById(dcnId);
        assertTrue(publishingState.isPresent());
        assertEquals(PublishingState.State.INPROGRESS, publishingState.get().getIndexed());
    }

    @Test
    void updateNotExistPublishStateWillCreateNewPublishingStateWithTheGivenState() {
        var dcnId = "oep-42f5fcf361d7269c56c4a456fd2f1d3b8b25954f";

        var publishingState = this.publishingStateRepository.findById(dcnId);
        assertTrue(publishingState.isEmpty());

        this.publishingStateRepository.updateAndSave(dcnId, PublishingState.State.INPROGRESS);
        var result = this.publishingStateRepository.findById(dcnId);
        assertTrue(result.isPresent());
        assertEquals(PublishingState.State.INPROGRESS, result.get().getIndexed());
    }

    @Test
    void countPublishingStateByIndexedType() {
        assertEquals(0, this.publishingStateRepository.countPublishingStateByIndexed(PublishingState.State.INPROGRESS));
        this.publishingStateRepository.save(new PublishingState("INPROGRESS_2", PublishingState.State.INPROGRESS));
        this.publishingStateRepository.save(new PublishingState("INPROGRESS_1", PublishingState.State.INPROGRESS));

        assertEquals(2, this.publishingStateRepository.countPublishingStateByIndexed(PublishingState.State.INPROGRESS));

        this.publishingStateRepository.save(new PublishingState("INPROGRESS_3", PublishingState.State.INPROGRESS));
        this.publishingStateRepository.save(new PublishingState("OK_1", PublishingState.State.OK));

        assertEquals(3, this.publishingStateRepository.countPublishingStateByIndexed(PublishingState.State.INPROGRESS));
        assertEquals(1, this.publishingStateRepository.countPublishingStateByIndexed(PublishingState.State.OK));
        assertEquals(0, this.publishingStateRepository.countPublishingStateByIndexed(PublishingState.State.TODO));
    }
}

package nl.overheid.koop.plooi.dcn.model;

import java.time.OffsetDateTime;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PublishingStateRepository extends JpaRepository<PublishingState, String> {

    default void updateAndSave(String dcnId, PublishingState.State state) {
        this.save(this.findById(dcnId).orElse(new PublishingState(dcnId, state)).updatePublishingState(state));
    }

    int countPublishingStateByIndexed(PublishingState.State state);

    @Query(" SELECT ps "
            + " FROM PublishingState ps "
            + " WHERE (ps.indexed = 'INPROGRESS' OR  ps.indexed = 'TODO') AND ps.timeUpdated <= :maxDate "
            + " ORDER BY ps.timeUpdated ASC")
    Page<PublishingState> getExecutionsWithNotIndexedPublishingStates(OffsetDateTime maxDate, Pageable pageable);

    @Query("SELECT ps.dcnId "
            + " FROM PublishingState ps "
            + " WHERE ps.indexed = 'INPROGRESS' ")
    Stream<String> getInProgressPublishingState();
}

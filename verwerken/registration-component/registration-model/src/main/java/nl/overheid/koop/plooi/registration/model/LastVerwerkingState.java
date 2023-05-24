package nl.overheid.koop.plooi.registration.model;
import java.time.OffsetDateTime;

public class LastVerwerkingState {
    private final OffsetDateTime timeCreated;
    private final String dcnId;

    public LastVerwerkingState(OffsetDateTime timeCreated, String dcnId) {
        this.timeCreated = timeCreated;
        this.dcnId = dcnId;
    }

    public OffsetDateTime getTimeCreated() {
        return this.timeCreated;
    }
    public String getdcnId() {
        return this.dcnId;
    }
}

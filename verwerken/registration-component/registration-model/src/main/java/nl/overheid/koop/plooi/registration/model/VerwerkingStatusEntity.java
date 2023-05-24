package nl.overheid.koop.plooi.registration.model;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Verwerkingstatus")
public class VerwerkingStatusEntity {

    @Id
    private String dcnId;
    private String sourceLabel;
    private String extId;
    private OffsetDateTime timeCreated;
    private OffsetDateTime timeUpdated;
    private String lastSeverity;
    private String lastStage;
    private String lastProcesId;

    protected VerwerkingStatusEntity() {
        super();
    }

    public VerwerkingStatusEntity(@Nonnull String dcnId, @Nonnull String lastProcesId) {
        super();
        this.dcnId = dcnId;
        this.lastProcesId = lastProcesId;
    }

    public String getDcnId() {
        return this.dcnId;
    }

    public String getSourceLabel() {
        return this.sourceLabel;
    }

    public String getExtId() {
        return this.extId;
    }

    public OffsetDateTime getTimeCreated() {
        return this.timeCreated;
    }

    public OffsetDateTime getTimeUpdated() {
        return this.timeUpdated;
    }

    public String getLastSeverity() {
        return this.lastSeverity;
    }

    public String getLastStage() {
        return this.lastStage;
    }

    public String getLastProcesId() {
        return this.lastProcesId;
    }

    @Override
    public String toString() {
        return String.format("%s : %s", this.dcnId, this.lastProcesId);
    }
}

package nl.overheid.koop.plooi.dcn.model;

import java.time.OffsetDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "Publishingstate")
public class PublishingState {

    @Id
    private String dcnId;

    public enum State {
        TODO,
        INPROGRESS,
        OK;

        public boolean confirmed() {
            return this.equals(State.OK);
        }
    }

    @Enumerated(EnumType.STRING)
    private State indexed;
    @CreationTimestamp
    private OffsetDateTime timeCreated;
    @UpdateTimestamp
    private OffsetDateTime timeUpdated;

    protected PublishingState() {
        super();
    }

    public PublishingState(@NotNull String dcnId, @NotNull State indexed) {
        super();
        this.dcnId = dcnId;
        this.indexed = indexed;
    }

    public State getIndexed() {
        return this.indexed;
    }

    public String getDcnId() {
        return this.dcnId;
    }

    public OffsetDateTime getTimeCreated() {
        return this.timeCreated;
    }

    public PublishingState updatePublishingState(State indexed) {
        this.indexed = indexed;
        return this;
    }

    public OffsetDateTime getTimeUpdated() {
        return this.timeUpdated;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("dcnId", this.dcnId)
                .append("indexed", this.indexed)
                .append("timeCreated", this.timeCreated)
                .append("timeUpdated", this.timeUpdated)
                .toString();
    }
}

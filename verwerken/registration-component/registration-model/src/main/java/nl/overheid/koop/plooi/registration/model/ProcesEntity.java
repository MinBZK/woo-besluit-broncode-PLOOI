package nl.overheid.koop.plooi.registration.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Represents the "Processen" table entry which is the event for received Represents the "Processen" table entry. There
 * is a one-to-many relationship between {@link ProcesEntity Proces} and
 * {@link nl.overheid.koop.plooi.registration.model.Verwerking}.
 *
 * @see nl.overheid.koop.plooi.registration.model.VerwerkingRepository
 * @see nl.overheid.koop.plooi.registration.model.Verwerking
 */
@Entity
@Table(name = "Processen")
public class ProcesEntity {

    @Id
    private String id;
    private String sourceLabel;
    @Column(nullable = false)
    private String triggerType;
    @Column(nullable = false)
    private String trigger;
    @CreationTimestamp
    private OffsetDateTime timeCreated;

    protected ProcesEntity() {
        super();
    }

    public ProcesEntity(@Nonnull String src, @Nonnull String trgTp, @Nonnull String trg) {
        super();
        this.id = UUID.randomUUID().toString();
        this.sourceLabel = StringUtils.abbreviate(src, "", 16);
        this.triggerType = StringUtils.abbreviate(trgTp, "", 32);
        this.trigger = StringUtils.abbreviate(trg, 512);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("triggerType", this.triggerType)
                .append("source", this.sourceLabel)
                .append("timeCreated", this.timeCreated)
                .append("id", this.id)
                .append("trigger", this.trigger)
                .toString();
    }

    public String getId() {
        return this.id;
    }

    public String getSourceLabel() {
        return this.sourceLabel;
    }

    public String getTriggerType() {
        return this.triggerType;
    }

    public String getTrigger() {
        return this.trigger;
    }

    public OffsetDateTime getTimeCreated() {
        return this.timeCreated;
    }
}

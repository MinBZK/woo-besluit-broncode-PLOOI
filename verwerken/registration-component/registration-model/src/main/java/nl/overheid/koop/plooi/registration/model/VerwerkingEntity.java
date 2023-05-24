package nl.overheid.koop.plooi.registration.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Represents the "Verwerkingen" table entry which is the event for received documents and stores. "ProcesId" field
 * is the foreign key for the {@link nl.overheid.koop.plooi.registration.model.Proces Proces} table and related entity
 * class.
 *
 * @see nl.overheid.koop.plooi.registration.model.VerwerkingRepository
 * @see ProcesEntity
 */
@Entity
@Table(name = "Verwerkingen")
public class VerwerkingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long dcnSeq;
    private String id;
    private String procesId;
    private String sourceLabel;
    private String extId;
    private String dcnId;
    private String stage;
    @Enumerated(EnumType.STRING)
    private Severity severity;
    @CreationTimestamp
    private OffsetDateTime timeCreated;

    protected VerwerkingEntity() {
        super();
    }

    public VerwerkingEntity(@Nonnull String procesId, String source, String dcnId, String extId, @Nonnull String stg, @Nonnull Severity svr) {
        super();
        this.id = UUID.randomUUID().toString();
        this.procesId = procesId;
        this.sourceLabel = source;
        this.dcnId = dcnId;
        this.extId = StringUtils.abbreviate(extId, 1024);
        this.stage = stg;
        this.severity = svr;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("procesId", this.procesId)
                .append("sourceLabel", this.sourceLabel)
                .append("dcnId", this.dcnId)
                .append("stage", this.stage)
                .append("severity", this.severity)
                .toString();
    }

    public String getId() {
        return this.id;
    }

    public String getProcesId() {
        return this.procesId;
    }

    public String getSourceLabel() {
        return this.sourceLabel;
    }

    public String getExtId() {
        return this.extId;
    }

    public String getDcnId() {
        return this.dcnId;
    }

    public String getStage() {
        return this.stage;
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public OffsetDateTime getTimeCreated() {
        return this.timeCreated;
    }
}

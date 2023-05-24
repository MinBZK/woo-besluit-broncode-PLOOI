package nl.overheid.koop.plooi.repository.relational;

import java.time.OffsetDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "RELATIES")
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;
    private String fromId;
    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type")
    private RelationType relationType;
    private String toId;
    private String title;
    private String origin;
    @CreationTimestamp
    private OffsetDateTime timestamp;

    protected Relation() {
    }

    public Relation(String from, RelationType type, String to, String t, String o) {
        this.fromId = from;
        this.relationType = type;
        this.toId = to;
        this.title = t;
        this.origin = o;
    }

    public String getFromId() {
        return this.fromId;
    }

    public RelationType getRelationType() {
        return this.relationType;
    }

    public String getToId() {
        return this.toId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getOrigin() {
        return this.origin;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this
                || (obj instanceof Relation other
                        && Objects.equals(this.fromId, other.fromId)
                        && Objects.equals(this.relationType, other.relationType)
                        && Objects.equals(this.toId, other.toId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fromId, this.relationType, this.toId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fromId", this.fromId)
                .append("relationType", this.relationType)
                .append("toId", this.toId)
                .append("title", this.title)
                .toString();
    }
}

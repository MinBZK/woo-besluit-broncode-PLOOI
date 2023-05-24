package nl.overheid.koop.plooi.registration.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Diagnoses")
public class DiagnoseEntity {

    private static final int MAX_TEXT_LENGTH = 256;

    public Long getId() {
        return this.id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;
    private String verwerkingId;
    private String code;
    @Enumerated(EnumType.STRING)
    private Severity severity;
    private String sourceId;
    private String sourceLabel;
    @Column(name = "targetElementName")
    private String target;
    @Column(columnDefinition = "CLOB")
    private String message;

    protected DiagnoseEntity() {
        super();
    }

    public DiagnoseEntity(String verw, String cd, Severity sev, String msg, String si, String sl, String t) {
        super();
        this.verwerkingId = verw;
        this.code = cd;
        this.severity = sev;
        this.message = msg;
        this.sourceId = si;
        this.sourceLabel = sl;
        this.target = t;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject instanceof DiagnoseEntity other) {
            return Objects.equals(this.code, other.code) &&
                    Objects.equals(this.severity, other.severity) &&
                    Objects.equals(this.sourceId, other.sourceId) &&
                    Objects.equals(this.sourceLabel, other.sourceLabel) &&
                    Objects.equals(this.target, other.target) &&
                    Objects.equals(this.message, other.message);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.code, this.severity, this.sourceId, this.sourceLabel, this.target, this.message);
    }

    public String getVerwerkingId() {
        return this.verwerkingId;
    }

    public String getCode() {
        return this.code;
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public String getMessage() {
        return this.message;
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public String getSourceLabel() {
        return this.sourceLabel;
    }

    public String getTarget() {
        return this.target;
    }

    @Override
    public String toString() {
        return String.format("Diagnose %s %s: %s", this.code, this.target, this.message);
    }
}

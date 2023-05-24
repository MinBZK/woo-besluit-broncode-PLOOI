package nl.overheid.koop.plooi.registration.model;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "Excepties")
public class ExceptieEntity {

    public static final int STANDARD_MAX_LENGTH = 256;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;
    @CreationTimestamp
    private OffsetDateTime timeCreated;
    private String fromRoute;
    private String verwerkingId;
    private String exceptionClass;
    @Column(columnDefinition = "CLOB")
    private String exceptionMessage;
    @Column(columnDefinition = "CLOB")
    private String exceptionStacktrace;
    private Integer statusCode;
    private String statusText;
    @Column(columnDefinition = "CLOB")
    private String messageBody;

    protected ExceptieEntity() {
        super();
    }

    public ExceptieEntity(String verwerkingId, String route) {
        super();
        this.fromRoute = StringUtils.abbreviate(route, STANDARD_MAX_LENGTH);
        this.verwerkingId = verwerkingId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("verwerkingId", this.verwerkingId)
                .append("fromRoute", this.fromRoute)
                .append("exception", this.exceptionMessage == null ? this.exceptionClass : this.exceptionMessage)
                .toString();
    }

    public Long getId() {
        return this.id;
    }

    public OffsetDateTime getTimeCreated() {
        return this.timeCreated;
    }

    public String getFromRoute() {
        return this.fromRoute;
    }

    public String getVerwerkingId() {
        return this.verwerkingId;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = StringUtils.abbreviate(exceptionClass, STANDARD_MAX_LENGTH);
    }

    public String getExceptionClass() {
        return this.exceptionClass;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

    public void setExceptionStacktrace(String exceptionStacktrace) {
        this.exceptionStacktrace = exceptionStacktrace;
    }

    public String getExceptionStacktrace() {
        return this.exceptionStacktrace;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageBody() {
        return this.messageBody;
    }
}

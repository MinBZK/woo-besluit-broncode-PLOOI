package nl.overheid.koop.plooi.registration.model;

public class ProcesVerwerkingSeverityStats {

    private final Long totalCount;
    private final Long documentCount;
    private final Long okCount;
    private final Long infoCount;
    private final Long warningCount;
    private final Long errorCount;
    private final Long exceptionCount;

    public ProcesVerwerkingSeverityStats(Long totals, Long docs, Long oks, Long infos, Long warnings, Long errors, Long exceptions) {
        this.totalCount = totals == null ? 0 : totals;
        this.documentCount = docs == null ? 0 : docs;
        this.okCount = oks == null ? 0 : oks;
        this.infoCount = infos == null ? 0 : infos;
        this.warningCount = warnings == null ? 0 : warnings;
        this.errorCount = errors == null ? 0 : errors;
        this.exceptionCount = exceptions == null ? 0 : exceptions;
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public Long getDocumentCount() {
        return this.documentCount;
    }

    public Long getOkCount() {
        return this.okCount;
    }

    public Long getInfoCount() {
        return this.infoCount;
    }

    public Long getWarningCount() {
        return this.warningCount;
    }

    public Long getErrorCount() {
        return this.errorCount;
    }

    public Long getExceptionCount() {
        return this.exceptionCount;
    }
}

package nl.overheid.koop.plooi.registration.model;

public class SeveritySummaryResponse {

    private final String targetElementName;
    private final String sourceLabel;
    private final Long count;

    public SeveritySummaryResponse(String targetElementName, String sourceLabel, Long count) {
        super();
        this.targetElementName = targetElementName;
        this.sourceLabel = sourceLabel;
        this.count = count;
    }

    public String getTargetElementName() {
        return this.targetElementName;
    }

    public String getSourceLabel() {
        return this.sourceLabel;
    }

    public Long getCount() {
        return this.count;
    }
}

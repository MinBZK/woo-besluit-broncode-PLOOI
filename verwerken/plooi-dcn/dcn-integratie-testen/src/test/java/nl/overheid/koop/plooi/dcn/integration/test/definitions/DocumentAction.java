package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import java.time.OffsetDateTime;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;

public class DocumentAction {

    private final String documentId;
    private final VerwerkingActies verwerkingActie;
    private OffsetDateTime currentDate;
    private OffsetDateTime updatedDate;

    public DocumentAction(String documentId, VerwerkingActies action) {
        this.documentId = documentId;
        this.verwerkingActie = action;
    }

    public String getDocumentId() {
        return this.documentId;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate != null ? OffsetDateTime.parse(currentDate) : null;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = OffsetDateTime.parse(updatedDate);
    }

    public OffsetDateTime getCurrentDate() {
        return this.currentDate;
    }

    public OffsetDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public boolean isUpdated() {
        return switch (this.verwerkingActie) {
            case VERWERKING -> this.updatedDate != null && this.currentDate.isBefore(this.updatedDate);
            case INTREKKING -> this.currentDate != null && this.updatedDate == null;
            case HERPUBLICATIE -> this.currentDate == null && this.updatedDate != null;
            default -> false;
        };
    }
}

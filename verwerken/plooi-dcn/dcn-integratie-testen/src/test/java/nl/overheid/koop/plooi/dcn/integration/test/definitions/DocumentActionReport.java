package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import java.util.ArrayList;
import java.util.List;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;

public class DocumentActionReport {

    private final List<DocumentAction> documents = new ArrayList<>();
    private final TestReport testReport = new TestReport();

    public boolean documentsAreUpdated() {
        return this.documents.stream().allMatch(DocumentAction::isUpdated) && this.testReport.isPassed();
    }

    public TestReport getTestReport() {
        return this.testReport;
    }

    public List<DocumentAction> getDocuments() {
        return this.documents;
    }

    public void add(String documentId, String currentDate, String action) {
        var found = this.documents.stream().filter(e -> e.getDocumentId().equals(documentId)).findFirst();
        var verwerkingActie = VerwerkingActies.valueOf(action);
        if ((verwerkingActie.equals(VerwerkingActies.VERWERKING) || verwerkingActie.equals(VerwerkingActies.INTREKKING)) && found.isEmpty()) {
            DocumentAction updatedDocument = new DocumentAction(documentId, verwerkingActie);
            updatedDocument.setCurrentDate(currentDate);
            this.documents.add(updatedDocument);
        } else if (verwerkingActie.equals(VerwerkingActies.HERPUBLICATIE) && found.isEmpty()) {
            DocumentAction unDeleteDocument = new DocumentAction(documentId, verwerkingActie);
            unDeleteDocument.setUpdatedDate(currentDate);
            this.documents.add(unDeleteDocument);
        } else if (verwerkingActie.equals(VerwerkingActies.VERWERKING) || verwerkingActie.equals(VerwerkingActies.HERPUBLICATIE)) {
            found.get().setUpdatedDate(currentDate);
        }
    }
}

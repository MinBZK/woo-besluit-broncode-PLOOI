package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetDateTime;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;
import org.junit.jupiter.api.Test;

public class DocumentActionReportTest {

    @Test
    void update_document_by_reprocess() {
        var documentActionReport = new DocumentActionReport();
        documentActionReport.add("doc1", OffsetDateTime.now().toString(), VerwerkingActies.VERWERKING.name());
        assertFalse(documentActionReport.getDocuments().isEmpty());
        documentActionReport.add("doc1", OffsetDateTime.now().plusMinutes(1).toString(), VerwerkingActies.VERWERKING.name());
        assertTrue(documentActionReport.getDocuments().get(0).isUpdated());
    }

    @Test
    void add_document_by_reprocess() {
        var documentActionReport = new DocumentActionReport();
        documentActionReport.add("doc1", OffsetDateTime.now().toString(), VerwerkingActies.VERWERKING.name());
        assertFalse(documentActionReport.getDocuments().isEmpty());
        assertFalse(documentActionReport.getDocuments().get(0).isUpdated());
    }

    @Test
    void update_document_for_delete_will_not_add_update_time() {
        var documentActionReport = new DocumentActionReport();
        documentActionReport.add("doc1", OffsetDateTime.now().toString(), VerwerkingActies.INTREKKING.name());
        DocumentAction dco1 = documentActionReport.getDocuments().get(0);
        assertNotNull(dco1.getCurrentDate());
        assertNull(dco1.getUpdatedDate());

        documentActionReport.add("doc1", OffsetDateTime.now().plusMinutes(1).toString(), VerwerkingActies.INTREKKING.name());

        assertNotNull(dco1.getCurrentDate());
        assertNull(dco1.getUpdatedDate());
    }

    @Test
    void update_document_for_undelete_will_add_update_time_only() {
        var documentActionReport = new DocumentActionReport();
        documentActionReport.add("doc1", OffsetDateTime.now().toString(), VerwerkingActies.HERPUBLICATIE.name());
        DocumentAction dco1 = documentActionReport.getDocuments().get(0);
        assertNull(dco1.getCurrentDate());
        assertNotNull(dco1.getUpdatedDate());
        documentActionReport.add("doc1", OffsetDateTime.now().plusMinutes(1).toString(), VerwerkingActies.HERPUBLICATIE.name());
        assertNull(dco1.getCurrentDate());
        assertNotNull(dco1.getUpdatedDate());
    }
}

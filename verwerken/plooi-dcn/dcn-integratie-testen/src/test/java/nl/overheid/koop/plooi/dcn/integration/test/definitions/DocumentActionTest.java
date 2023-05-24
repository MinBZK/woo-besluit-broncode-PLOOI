package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetDateTime;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;
import org.junit.jupiter.api.Test;

public class DocumentActionTest {

    @Test
    void reprocess_document_updated() {
        var doc1 = new DocumentAction("doc1", VerwerkingActies.VERWERKING);
        doc1.setCurrentDate(OffsetDateTime.now().toString());
        doc1.setUpdatedDate(OffsetDateTime.now().plusMinutes(10).toString());
        assertTrue(doc1.isUpdated());
    }

    @Test
    void reprocess_document_not_updated() {
        var doc1 = new DocumentAction("doc1", VerwerkingActies.HERPUBLICATIE);
        doc1.setCurrentDate(OffsetDateTime.now().toString());
        assertFalse(doc1.isUpdated());
    }

    @Test
    void update_deleted_document() {
        var doc1 = new DocumentAction("doc1", VerwerkingActies.INTREKKING);
        doc1.setCurrentDate(OffsetDateTime.now().toString());
        assertTrue(doc1.isUpdated());
    }

    @Test
    void update_redo_document() {
        var doc1 = new DocumentAction("doc1", VerwerkingActies.HERPUBLICATIE);
        doc1.setUpdatedDate(OffsetDateTime.now().toString());
        assertTrue(doc1.isUpdated());
    }
}

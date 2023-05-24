package nl.overheid.koop.plooi.dcn.component.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.registration.model.Diagnose;
import org.junit.jupiter.api.Test;

class DiagnosticFilterTest {

    @Test
    void emptyFilter() {
        var diagnosticFilter = DiagnosticFilter.configure();
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.DISCARD.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.DISCARD)).message("message")));
        assertFalse(diagnosticFilter.rejects( new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("value").target("target")));
    }

    @Test
    void justCode() {
        var diagnosticFilter = DiagnosticFilter.configure().add(DiagnosticCode.EMPTY_ID);
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.DISCARD.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.DISCARD)).message("message")));
        assertTrue(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("value").target("target")));
        assertTrue(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("value").target("other")));
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.REQUIRED_DEFAULT.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.REQUIRED_DEFAULT)).sourceLabel("value").target("target")));
    }

    @Test
    void codeAndTarget() {
        var diagnosticFilter = DiagnosticFilter.configure().add(DiagnosticCode.EMPTY_ID, "target");
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.DISCARD.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.DISCARD)).message("message")));
        assertTrue(diagnosticFilter.rejects( new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("value").target("target")));
        assertTrue(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("othervalue").target("target")));
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("value").target("other")));
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.REQUIRED_DEFAULT.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.REQUIRED_DEFAULT)).sourceLabel("value").target("target")));
    }

    @Test
    void codeTargetAndValue() {
        var diagnosticFilter = DiagnosticFilter.configure()
                .add(DiagnosticCode.EMPTY_ID, "target", "value");

        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.DISCARD.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.DISCARD)).message("message")));
        assertTrue(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("value").target("target")));
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("othervalue").target("target")));
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("value").target("other")));
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.REQUIRED_DEFAULT.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.REQUIRED_DEFAULT)).sourceLabel("value").target("target")));
    }

    @Test
    void alsoWorksForIdentifier() {

        var diagnosticFilter = DiagnosticFilter.configure()
                .add(DiagnosticCode.EMPTY_ID, "target", "id");

        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.DISCARD.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.DISCARD)).message("message")));
        assertTrue(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceId("id").target("target")));
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("othervalue").target("target")));
        assertFalse(diagnosticFilter.rejects(new Diagnose().code(DiagnosticCode.EMPTY_ID.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.EMPTY_ID)).sourceLabel("value").target("other")));
        assertFalse(diagnosticFilter.rejects( new Diagnose().code(DiagnosticCode.REQUIRED_DEFAULT.name()).severity(DiagnosticCode.getSeverity(DiagnosticCode.REQUIRED_DEFAULT)).sourceLabel("value").target("target")));
    }
}

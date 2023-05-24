package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TestReportTest {

    @Test
    void isPassed_empty() {
        var testReport = new TestReport();
        assertTrue(testReport.isPassed());
    }

    @Test
    void isPassed_allOk() {
        var testReport = new TestReport();
        testReport.add("test", "field", "ok", "ok");
        assertTrue(testReport.isPassed());
    }

    @Test
    void isPassed_allFailed() {
        var testReport = new TestReport();
        testReport.add("test", "field", "ok", "wrong");
        assertFalse(testReport.isPassed());
    }

    @Test
    void isPassed_oneFailed() {
        var testReport = new TestReport();
        testReport.add("test", "field1", "ok", "ok");
        testReport.add("test", "field2", "ok", "wrong");
        testReport.add("test", "field3", "ok", "ok");
        assertFalse(testReport.isPassed());
    }
}

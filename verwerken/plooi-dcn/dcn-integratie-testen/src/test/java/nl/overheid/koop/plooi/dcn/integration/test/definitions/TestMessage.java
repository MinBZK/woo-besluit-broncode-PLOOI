package nl.overheid.koop.plooi.dcn.integration.test.definitions;

public class TestMessage {

    private static final String MESSAGE_SUCCESS_TEMPLATE = "Test [%s] succeed on field: [%s]";
    private static final String MESSAGE_FAIL_TEMPLATE = "Test [%s] failed on field: [%s]. Actual is [%s] but Expected was [%s]";
    private static final String NO_MESSAGE = "";

    private final String testName;
    private final String field;
    private final String expectedValue;
    private final String actualValue;
    private final boolean printAll;
    private final boolean isPassed;

    public TestMessage(String testName, String field, String expectedValue, String actualValue, boolean printAll) {
        this.testName = testName;
        this.field = field;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
        this.printAll = printAll;
        this.isPassed = expectedValue.equals(actualValue);
    }

    public boolean isPassed() {
        return this.isPassed;
    }

    @Override
    public String toString() {
        if (!this.isPassed) {
            return String.format(MESSAGE_FAIL_TEMPLATE, this.testName, this.field, this.actualValue, this.expectedValue);
        } else if (this.printAll) {
            return String.format(MESSAGE_SUCCESS_TEMPLATE, this.testName, this.field, this.actualValue, this.expectedValue);
        } else {
            return NO_MESSAGE;
        }
    }
}

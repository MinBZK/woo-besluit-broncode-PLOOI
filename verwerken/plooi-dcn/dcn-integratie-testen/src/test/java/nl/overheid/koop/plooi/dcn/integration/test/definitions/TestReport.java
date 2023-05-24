package nl.overheid.koop.plooi.dcn.integration.test.definitions;

import java.util.ArrayList;
import java.util.List;

public class TestReport {

    private final List<TestMessage> testMessageList = new ArrayList<>();
    private final boolean printAll;

    public TestReport() {
        this(false);
    }

    public TestReport(boolean printAll) {
        this.printAll = printAll;
    }

    public void add(String testName, String field, String expectedValue, String actualValue) {
        this.testMessageList.add(new TestMessage(testName, field, expectedValue, actualValue, this.printAll));
    }

    public boolean isPassed() {
        return this.testMessageList.stream().allMatch(TestMessage::isPassed);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        if (this.printAll) {
            sb.append("Test Report");
            sb.append(System.lineSeparator());
            sb.append("-----------");
        } else {
            sb.append("Test Failure Report");
            sb.append(System.lineSeparator());
            sb.append("-------------------");
        }
        sb.append(System.lineSeparator());
        this.testMessageList.forEach(testModel -> {
            if (!testModel.isPassed() || (this.printAll)) {
                sb.append(testModel);
                sb.append(System.lineSeparator());
            }
        });
        return sb.toString();
    }
}

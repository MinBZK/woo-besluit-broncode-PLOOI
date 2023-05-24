package nl.overheid.koop.plooi.test.util;

import static org.junit.jupiter.api.Assertions.fail;
import io.cucumber.datatable.DataTable;
import java.util.function.Function;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class Retry {

    private static final int HEALTH_TRIES = 30;
    private static final int HEALTH_WAIT = 10_000;

    private static final Logger logger = LoggerFactory.getLogger(Retry.class);

    @Value("${integration.retry.max-count}")
    public Integer retryMaxCount;

    @Value("${integration.retry.sleep}")
    public Long retrySleep;

    public void handle(DataTable dataTable, Function<DataTable, Boolean> handler, String failMessage) {
        int count = 0;
        do {
            if (handler.apply(dataTable)) {
                return;
            } else {
                sleep(this.retrySleep);
                logger.trace("  Retrying {}th time...  ({})", count + 1, handler);
            }
        } while (count++ < this.retryMaxCount);
        fail("No result after " + this.retryMaxCount + " tries:" + failMessage);
    }

    @SuppressWarnings("java:S2925") // Nothing wrong with waiting a bit
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Interrupted");
        }
    }

    public static void waitForSuccess(String url) {
        int count = 0;
        do {
            try {
                if (ConnectionUtils.getHttpGetResponse(url).statusCode() == 200) {
                    return;
                }
            } catch (AssertionFailedError e) {
                // Can't connect yet
            }
            logger.trace("  Not ready yet after {} tries, waiting {} seconds", count + 1, HEALTH_WAIT / 1000);
            Retry.sleep(HEALTH_WAIT);
        } while (count++ < HEALTH_TRIES);
        fail(String.format("Url '%s' not alive after %s tries", url, HEALTH_TRIES));
    }
}

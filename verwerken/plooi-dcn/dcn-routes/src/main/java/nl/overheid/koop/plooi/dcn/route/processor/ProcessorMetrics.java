package nl.overheid.koop.plooi.dcn.route.processor;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.util.Optional;

class ProcessorMetrics {

    private static final String LABEL = "dcn_processor";
    private static final String PROCESSOR = "processor";
    private static final String PROCESSING = "processing";
    static final String SOURCE = "source";
    static final String DISCARDED = "discarded";

    private final Timer.Sample timer;

    private ProcessorMetrics() {
        this.timer = Timer.start();
    }

    static ProcessorMetrics start() {
        return new ProcessorMetrics();
    }

    void stop(Object processor, Object processing, Optional<String> source) {
        this.timer.stop(Metrics.timer(
                LABEL,
                Tags.of(PROCESSOR, processor.getClass().getSimpleName().toLowerCase(),
                        PROCESSING, processing.getClass().getSimpleName().replaceAll("\\$\\$.*", "").toLowerCase(),
                        SOURCE, source.orElse("unidentified").toLowerCase())));
    }
}

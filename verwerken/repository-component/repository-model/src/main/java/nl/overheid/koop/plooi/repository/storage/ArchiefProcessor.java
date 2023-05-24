package nl.overheid.koop.plooi.repository.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes each identifier by buffering them into fixed sized lists and passing those lists to a provided
 * {@link Consumer}.
 */
public class ArchiefProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Consumer<List<String>> archiefConsumer;
    private final int chunkSize;
    private final AtomicInteger processed = new AtomicInteger();
    private List<String> buffer;

    /**
     * @param consumer {@link Consumer} responsible for processing {@link List}s of identifiers
     * @param size     Size of the lists to pass to the consumer
     */
    public ArchiefProcessor(Consumer<List<String>> consumer, int size) {
        this.archiefConsumer = consumer;
        this.chunkSize = size;
        this.buffer = new ArrayList<>();
    }

    /** Adds an identifier to the buffer and passes the buffer if it reaches the maximum size. */
    synchronized void process(String id) {
        this.logger.trace("Adding {}", id);
        this.buffer.add(id);
        if (this.buffer.size() >= this.chunkSize) {
            consume(new ArrayList<>());
        }
    }

    /** Passes the remaining buffer. */
    synchronized int flush() {
        consume(null);
        return this.processed.get();
    }

    private void consume(List<String> newBuffer) {
        try {
            var chunk = this.buffer;
            this.buffer = newBuffer;
            var total = this.processed.addAndGet(chunk.size());
            this.logger.debug("Processing chunk of +{}={} identifiers with {}", chunk.size(), total, this.archiefConsumer);
            this.archiefConsumer.accept(chunk);
        } catch (RuntimeException e) {
            this.logger.error("Exception thrown by " + this.archiefConsumer, e);
            throw e;
        }
    }
}

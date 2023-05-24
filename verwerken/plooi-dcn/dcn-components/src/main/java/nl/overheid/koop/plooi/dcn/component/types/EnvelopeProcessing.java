package nl.overheid.koop.plooi.dcn.component.types;

/**
 * Processes a {@link Envelope}; modifying it, persisting it, etc.
 */
public interface EnvelopeProcessing<T extends Envelope> {

    /**
     * Processes a {@link Envelope}; modifying it, persisting it, etc.
     *
     * @param  target The Envelope to process
     * @return        The processed Envelope
     */
    T process(T target);
}

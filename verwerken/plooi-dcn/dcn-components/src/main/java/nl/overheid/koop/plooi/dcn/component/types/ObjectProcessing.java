package nl.overheid.koop.plooi.dcn.component.types;

/**
 * Processes a typed Object, producing whatever (Camel does not care).
 */
public interface ObjectProcessing<T extends Object> {

    Object process(T object);
}

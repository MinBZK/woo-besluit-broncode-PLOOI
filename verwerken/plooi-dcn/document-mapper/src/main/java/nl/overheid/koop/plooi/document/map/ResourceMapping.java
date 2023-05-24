package nl.overheid.koop.plooi.document.map;

import nl.overheid.koop.plooi.model.data.IdentifiedResource;

/**
 * Representation of mappings to a {@link IdentifiedResource} object. A ResourceMapping is configured in a MapperConfig
 * and applied in a {@link PlooiMappingInstance}
 * <p>
 * With ResourceMapping a root path can be configured. The term, uri and scheme path expressions will then be applied
 * within that root context. This way we can map multiple occurrences of a needed property.
 */
public record ResourceMapping(String rootPath, String termPath, String schemePath, String uriPath) {

    /**
     * Factory method for {@link PlooiDocumentMapperConfig} to create a {@link ResourceMapping} with only a term mapping (no
     * schema nor uri).
     * <p>
     * NB consumers of ResourceMapping need to deal with this special case where the root path is set and not the term path.
     */
    public static ResourceMapping term(String root) {
        return new ResourceMapping(root, null, null, null);
    }

    /**
     * Factory method for {@link PlooiDocumentMapperConfig} to create a {@link ResourceMapping} with default scheme and
     * identifier attributes.
     */
    public static ResourceMapping owms(String root) {
        return new ResourceMapping(root, null, "@scheme", "@resourceIdentifier");
    }

    /** Factory method for {@link PlooiDocumentMapperConfig} to create a {@link ResourceMapping}. */
    public static ResourceMapping full(String root, String term, String scheme, String uri) {
        return new ResourceMapping(root, term, scheme, uri);
    }
}

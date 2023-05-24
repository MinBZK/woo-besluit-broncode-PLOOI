package nl.overheid.koop.plooi.document.map;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.model.data.ExtraMetadata;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.registration.model.Severity;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Node;

/**
 * Factory for {@link PlooiMappingInstance} objects, so after configuring a PlooiMapping we get a fresh
 * {@link PlooiMappingInstance} for every input to map.
 */
@SuppressWarnings("java:S112")
interface PlooiMapping {

    PlooiMappingInstance getInstance(InputStream srcStrm) throws Exception;

    default Severity getParsingErrorSeverity() {
        return Severity.ERROR;
    }
}

/**
 * Implements mapping of elements in an input document to PLOOI types, like {@link IdentifiedResource}. Implementations
 * of this interface deal with specific input formats like XML, JSON. These implementations can then be injected in
 * mappers like {@link ConfigurableDocumentMapper} and {@link ConfigurableFileMapper}.
 */
@SuppressWarnings("java:S112")
interface PlooiMappingInstance {

    /**
     * Maps a path expression to a single string value.
     *
     * @param  path                  The path expression to evaluate
     * @param  setter                Optional supplier to set the string value on an object
     * @return                       The string value or null (see nullForNoPath)
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    String mapString(String path, Consumer<String> setter);

    /**
     * Maps a path expression to a list of string values.
     *
     * @param  path                  The path expression to evaluate
     * @param  stringList            Existing String list to add values to
     * @param  setter                Optional supplier to set the String list extension value on an object
     * @return                       List of Strings with the added values. Is null when addTo is null and nothing is added
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    List<String> mapStrings(String path, List<String> stringList, Consumer<List<String>> setter);

    /**
     * Convenience method which maps a list of path expressions to a list of string values.
     *
     * @param  paths                 The path expressions to evaluate
     * @param  stringList            Existing String list to add values to
     * @param  setter                Optional supplier to set the String list extension value on an object
     * @return                       List of Strings with the added values. Is null when addTo is null and nothing is added
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    default List<String> mapStrings(List<String> paths, List<String> stringList, Consumer<List<String>> setter) {
        if (!paths.isEmpty()) {
            var addTo = new ArrayList<String>();
            paths.forEach(path -> mapStrings(path, stringList, addTo::addAll));
            if (!addTo.isEmpty()) {
                return setWith(addTo, setter);
            }
        }
        return null;
    }

    /**
     * Maps a path expression to a list of string values paired with the name of the containing element.
     *
     * @param  path                  The path expression to evaluate
     * @param  addTo                 List of {@link Pair}s to add the mapped values to. If addTo is null and values are to
     *                               be added, a new List is created
     * @return                       List of Strings with the added values. Is null when addTo is null and nothing is added
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    List<Pair<String, String>> mapTaggedStrings(String path);

    /**
     * Maps a mapping with path expression and format definition to a timestamp ({@link ZonedDateTime}) object.
     *
     * @param  mapping               The mapping to evaluate for the {@link ZonedDateTime timestamp}
     * @param  setter                Optional supplier to set the {@link ZonedDateTime timestamp} value on an object
     * @return                       The {@link IdentifiedResource} value or null
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    default ZonedDateTime mapDate(DateMapping mapping, Consumer<ZonedDateTime> setter) {
        return mapping == null ? null : setWith(mapping.mapDate(mapString(mapping.path(), null)), setter);
    }

    /**
     * Maps a resource mapping with path expressions to a single {@link IdentifiedResource} object.
     *
     * @param  mapping               The mapping to evaluate for the {@link IdentifiedResource resource}
     * @param  setter                Optional supplier to set the {@link IdentifiedResource} value on an object
     * @return                       The {@link IdentifiedResource} value or null
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    IdentifiedResource mapResource(ResourceMapping mapping, Consumer<IdentifiedResource> setter);

    /**
     * Maps a resource mapping with path expressions to a list of {@link IdentifiedResource} objects.
     *
     * @param  mapping               The mapping to evaluate for the {@link IdentifiedResource resource}
     * @param  addTo                 List of {@link IdentifiedResource} objects to add the mapped values to. If addTo is
     *                               null and values are to be added, a new List is created
     * @return                       List of {@link IdentifiedResource} objects with the added values. Is null when addTo is
     *                               null and nothing is added
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    List<IdentifiedResource> mapResources(ResourceMapping mapping, List<IdentifiedResource> addTo);

    /**
     * Convenience method which maps a list of resource mappings to a list of {@link IdentifiedResource} objects.
     *
     * @param  mappings              List of mappings to evaluate for the {@link IdentifiedResource resource}
     * @param  addTo                 List of {@link IdentifiedResource} objects to add the mapped values to. If addTo is
     *                               null and values are to be added, a new List is created
     * @return                       List of {@link IdentifiedResource} objects with the added values. Is null when addTo is
     *                               null and nothing is added
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    default List<IdentifiedResource> mapResources(List<ResourceMapping> mappings, List<IdentifiedResource> addTo) {
        for (ResourceMapping mapping : mappings) {
            addTo = mapResources(mapping, addTo);
        }
        return addTo;
    }

    /**
     * Maps a path expression to a list of {@link Node} objects. (When parsing XML, these are just deep copies of the
     * matching nodes.)
     *
     * @param  path                  The path expression to evaluate for the {@link Node}
     * @param  addTo                 List of {@link Node} objects to add the mapped values to. If addTo is null and values
     *                               are to be added, a new List is created
     * @return                       List of {@link Node} objects with the added values.
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    default List<Node> mapDomNode(String path, List<Node> addTo) {
        throw new UnsupportedOperationException("Mapping to a DOM node is not supported");
    }

    /**
     * Creates a list of PlooiMappingInstances for the elements identified by a given path. Via this we can nest complex
     * types, like {@link PlooiFile#getChildren()}.
     *
     * @param  <T>                   Type of the {@link PlooiMappingInstance} implementation to create for each child
     * @param  path                  The path expression to evaluate for the children
     * @return                       The list of child {@link PlooiMappingInstances}
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    <T extends PlooiMappingInstance> List<T> childMapping(String path);

    /**
     * Creates a list of ExtraMetadata for the elements identified by a given path. {@link ResourceMapping} is (ab)used to
     * keep the mapping attributes:
     * <ul>
     * <li>{@link ResourceMapping.rootPath}: Holds the path referring to the root of the node with the data (as intended)
     * <li>{@link ResourceMapping.termPath}: Holds the path referring to the value within the root node (kinda as intended)
     * <li>{@link ResourceMapping.uriPath}: Holds the path referring to the key/suffix within the root node
     * <li>{@link ResourceMapping.schemePath}: Hold a literal key/prefix
     * </ul>
     *
     * @param  mapping               ResourceMapping holding the mapping attributes as described above
     * @param  addTo                 {@link ExtraMetadata} object to add the mapped values to. If addTo is null and values
     *                               are to be added, a new object is created
     * @return                       ExtraMetadata list object with the added values.
     * @throws PlooiMappingException If the expression cannot be evaluated
     */
    List<ExtraMetadata> mapExtraMetadatas(ExtraMetadataMapping mapping, List<ExtraMetadata> addTo);

    /** Utility method to set a value on an object, but only when needed. */
    static <T> T setWith(T toSet, Consumer<T> setter) {
        if (toSet != null && setter != null) {
            setter.accept(toSet);
        }
        return toSet;
    }
}

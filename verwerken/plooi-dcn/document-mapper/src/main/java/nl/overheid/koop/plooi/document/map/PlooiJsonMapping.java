package nl.overheid.koop.plooi.document.map;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import nl.overheid.koop.plooi.model.data.ExtraMetadata;
import nl.overheid.koop.plooi.model.data.ExtraMetadataVeld;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Maps JSON data to PLOOI types, using JsonPath expresiions.
 *
 * @see https://github.com/json-path/JsonPath
 * @see https://tools.ietf.org/id/draft-ietf-jsonpath-base-02.html
 */
public class PlooiJsonMapping implements PlooiMapping {
    @Override
    public PlooiJsonMappingInstance getInstance(InputStream srcStrm) {
        return new PlooiJsonMappingInstance(srcStrm);
    }
}

class PlooiJsonMappingInstance implements PlooiMappingInstance {

    private static final Configuration PATH_CONF = Configuration.builder().options(Option.ALWAYS_RETURN_LIST).build();
    private final DocumentContext documentContext;

    PlooiJsonMappingInstance(InputStream srcStrm) {
        this.documentContext = JsonPath.parse(srcStrm, PATH_CONF);
    }

    PlooiJsonMappingInstance(DocumentContext context) {
        this.documentContext = context;
    }

    @Override
    public List<ExtraMetadata> mapExtraMetadatas(ExtraMetadataMapping mapping, List<ExtraMetadata> addTo) {
        var extraMetadata = addTo == null ? new ArrayList<ExtraMetadata>() : addTo;
        if (mapping.keyPath() == null) {
            addStringValues(
                    this.documentContext,
                    mapping.rootPath(),
                    value -> buildExtraMetadata(
                            mapping,
                            null,
                            value,
                            extraMetadata),
                    extraMetadata,
                    null);
        } else {
            addMapValues(
                    this.documentContext,
                    mapping.rootPath(),
                    context -> buildExtraMetadata(
                            mapping,
                            doMapString(context, mapping.keyPath(), null),
                            doMapString(context, mapping.valuePath(), null),
                            extraMetadata),
                    extraMetadata);
        }
        return extraMetadata.isEmpty() ? null : extraMetadata;
    }

    @Override
    public String mapString(String path, Consumer<String> setter) {
        return doMapString(this.documentContext, path, setter);
    }

    @Override
    public List<String> mapStrings(String path, List<String> stringList, Consumer<List<String>> setter) {
        return PlooiMappingInstance.setWith(
                addStringValues(
                        this.documentContext,
                        path,
                        Function.identity(),
                        stringList,
                        ArrayList::new),
                setter);
    }

    @Override
    public List<Pair<String, String>> mapTaggedStrings(String path) {
        return addStringValues(
                this.documentContext,
                path,
                str -> str == null ? null : Pair.of("key", str),
                new ArrayList<>(),
                null);
    }

    @Override
    public IdentifiedResource mapResource(ResourceMapping mapping, Consumer<IdentifiedResource> setter) {
        if (mapping == null) {
            return null;
        } else {
            DocumentContext resourceContext;
            if (mapping.rootPath() == null) {
                resourceContext = this.documentContext;
            } else {
                var vals = readVals(mapping.rootPath(), this.documentContext);
                resourceContext = vals.isEmpty() ? null : JsonPath.parse(vals.get(0), PATH_CONF);
            }
            return PlooiMappingInstance.setWith(buildResource(mapping, resourceContext), setter);
        }
    }

    @Override
    public List<IdentifiedResource> mapResources(ResourceMapping mapping, List<IdentifiedResource> addTo) {
        if (mapping == null) {
            return addTo;
        } else if (mapping.rootPath() == null) {
            CollectionUtils.addIgnoreNull(addTo, buildResource(mapping, this.documentContext));
            return addTo;
        } else {
            return addMapValues(this.documentContext, mapping.rootPath(), context -> buildResource(mapping, context), addTo);
        }
    }

    @Override
    public List<PlooiMappingInstance> childMapping(String path) {
        return addMapValues(this.documentContext, path, PlooiJsonMappingInstance::new, new ArrayList<>());
    }

    private IdentifiedResource buildResource(ResourceMapping mapping, DocumentContext context) {
        if (mapping == null || context == null) {
            return null;
        } else {
            var resource = new IdentifiedResource()
                    .id(doMapString(context, mapping.uriPath(), null))
                    .type(doMapString(context, mapping.schemePath(), null))
                    .label(doMapString(context, mapping.termPath(), null));
            return resource.getId() == null && resource.getLabel() == null ? null : resource;
        }
    }

    private ExtraMetadata buildExtraMetadata(ExtraMetadataMapping mapping, String key, String value, List<ExtraMetadata> addTo) {
        if (value != null) {
            CollectionUtils.addIgnoreNull(
                    mapping.locateIn(addTo).getVelden(),
                    new ExtraMetadataVeld()
                            .key(mapping.key() == null ? key : mapping.key())
                            .values(List.of(value)));
        }
        // addNodeListValues does not have to add anything, that's already done here
        return null;
    }

    private String doMapString(DocumentContext context, String path, Consumer<String> setter) {
        if (path == null || context == null) {
            return null;
        } else {
            var vals = readVals(path, context); // This does not necessarily produce strings, so we toString it below
            return vals.isEmpty() ? null : PlooiMappingInstance.setWith(StringUtils.trimToNull(vals.get(0).toString()), setter);
        }
    }

    private <T> List<T> addStringValues(DocumentContext context, String path, Function<String, T> stringTransformer,
            List<T> addTo, Supplier<List<T>> addToSupplier) {
        if (path != null) {
            for (var val : readVals(path, context)) {
                if (val instanceof String strVal) {
                    addTo = addTo == null ? Objects.requireNonNull(addToSupplier, "An existing list or list supplier is required").get() : addTo;
                    CollectionUtils.addIgnoreNull(addTo, stringTransformer.apply(StringUtils.trimToNull(strVal)));
                } else {
                    throw new PlooiMappingException(path + " does not refer to a String value");
                }
            }
        }
        return addTo;
    }

    private <T> List<T> addMapValues(DocumentContext context, String path, Function<DocumentContext, T> contextTransformer, List<T> addTo) {
        if (path != null) {
            for (var val : readVals(path, context)) {
                if (val instanceof Map) {
                    CollectionUtils.addIgnoreNull(
                            Objects.requireNonNull(addTo, "An existing list is required"),
                            contextTransformer.apply(JsonPath.parse(val, PATH_CONF)));
                } else {
                    throw new PlooiMappingException(path + " does not refer to a Map value (when mapping a multi-valued resource, configure a rootPath)");
                }
            }
        }
        return addTo;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> readVals(String path, DocumentContext context) {
        try {
            return context.read(path, List.class);
        } catch (PathNotFoundException e) {
            return List.of();
        } catch (JsonPathException e) {
            throw new PlooiMappingException(e);
        }
    }
}

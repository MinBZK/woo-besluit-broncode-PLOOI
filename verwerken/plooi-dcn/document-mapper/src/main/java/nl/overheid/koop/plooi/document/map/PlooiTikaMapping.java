package nl.overheid.koop.plooi.document.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.overheid.koop.plooi.model.data.ExtraMetadata;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import org.apache.commons.collections4.CollectionUtils;
import nl.overheid.koop.plooi.registration.model.Severity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class PlooiTikaMapping implements PlooiMapping {

    public static final String DOCUMENT_TEXT = "DOCUMENT_TEXT";

    /**
     * Assuming Tika errors are not critical; Tika is used to extract text from a file and if that fails, we just have a
     * document without text.
     */
    private Severity parsingErrorSeverity = Severity.WARNING;

    @Override
    public PlooiTikaMappingInstance getInstance(InputStream srcStrm) throws TikaException, IOException, SAXException {
        return new PlooiTikaMappingInstance(srcStrm);
    }

    @Override
    public Severity getParsingErrorSeverity() {
        return this.parsingErrorSeverity;
    }

    public PlooiTikaMapping withParsingErrorSeverity(Severity severity) {
        this.parsingErrorSeverity = severity;
        return this;
    }
}

class PlooiTikaMappingInstance implements PlooiMappingInstance {

    private static final Parser TIKA_PARSER = new AutoDetectParser();

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Metadata metadata = new Metadata();
    private final BodyContentHandler handler = new BodyContentHandler(-1);

    PlooiTikaMappingInstance(InputStream tikaStrm) throws TikaException, IOException, SAXException {
        TIKA_PARSER.parse(tikaStrm, this.handler, this.metadata, new ParseContext());
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Metadata detected by Tika: {}",
                    Stream.of(this.metadata.names()).sorted().map(n -> n + "=" + this.metadata.get(n)).collect(Collectors.joining("\n")));
        }
    }

    @Override
    public String mapString(String key, Consumer<String> setter) {
        return PlooiTikaMapping.DOCUMENT_TEXT.equals(key) ? this.handler.toString() : doMapString(key, setter);
    }

    @Override
    public List<String> mapStrings(String key, List<String> stringList, Consumer<List<String>> setter) {
        if (PlooiTikaMapping.DOCUMENT_TEXT.equals(key)) {
            stringList = stringList == null ? new ArrayList<>() : stringList;
            CollectionUtils.addIgnoreNull(stringList, this.handler.toString());
        } else {
            stringList = addMetaValues(key, Function.identity(), stringList, ArrayList::new);
        }
        return PlooiMappingInstance.setWith(stringList, setter);
    }

    @Override
    public List<Pair<String, String>> mapTaggedStrings(String key) {
        throw new UnsupportedOperationException("Mapping of tagged Strings is not supported");
    }

    @Override
    public IdentifiedResource mapResource(ResourceMapping mapping, Consumer<IdentifiedResource> setter) {
        if (mapping == null) {
            return null;
        } else if (mapping.rootPath() == null) {
            return PlooiMappingInstance.setWith(
                    new IdentifiedResource()
                            .id(doMapString(mapping.uriPath(), null))
                            .type(doMapString(mapping.schemePath(), null))
                            .label(doMapString(mapping.termPath(), null)),
                    setter);
        } else if (mapping.termPath() == null && mapping.uriPath() == null) {
            return PlooiMappingInstance.setWith(
                    new IdentifiedResource()
                            .label(doMapString(mapping.rootPath(), null)),
                    setter);
        } else {
            throw new UnsupportedOperationException("Unsupported path " + mapping);
        }
    }

    @Override
    public List<IdentifiedResource> mapResources(ResourceMapping mapping, List<IdentifiedResource> addTo) {
        if (mapping != null) {
            if (mapping.rootPath() == null) {
                addMetaValues(mapping.termPath(), term -> new IdentifiedResource().label(term), addTo, null);
                addMetaValues(mapping.uriPath(), uri -> new IdentifiedResource().id(uri), addTo, null);
            } else if (mapping.termPath() == null && mapping.uriPath() == null) {
                addMetaValues(mapping.rootPath(), term -> new IdentifiedResource().label(term), addTo, null);
            } else {
                throw new UnsupportedOperationException("Unsupported path " + mapping);
            }
        }
        return addTo;
    }

    private <T> List<T> addMetaValues(String key, Function<String, T> metaValueFunction, List<T> addTo, Supplier<List<T>> addToSupplier) {
        if (key != null) {
            for (String metaValue : Stream.of(this.metadata.getValues(key))
                    .map(StringUtils::trimToNull)
                    .filter(Objects::nonNull)
                    .toList()) {
                addTo = addTo == null ? Objects.requireNonNull(addToSupplier, "An existing list or list supplier is required").get() : addTo;
                CollectionUtils.addIgnoreNull(addTo, metaValueFunction.apply(metaValue));
            }
        }
        return addTo;
    }

    private String doMapString(String key, Consumer<String> setter) {
        return key == null ? null : PlooiMappingInstance.setWith(StringUtils.trimToNull(this.metadata.get(key)), setter);
    }

    @Override
    public List<PlooiMappingInstance> childMapping(String path) {
        throw new UnsupportedOperationException("Mapping to children is not supported");
    }

    @Override
    public List<ExtraMetadata> mapExtraMetadatas(ExtraMetadataMapping mapping, List<ExtraMetadata> addTo) {
        throw new UnsupportedOperationException("Mapping of ExtraMetadata is not supported");
    }
}

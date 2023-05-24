package nl.overheid.koop.plooi.document.map;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.validation.Valid;
import nl.overheid.koop.plooi.model.data.ExtraMetadata;
import nl.overheid.koop.plooi.model.data.ExtraMetadataVeld;

public record ExtraMetadataMapping(String namespace, String prefix, String rootPath, String key, String keyPath, String valuePath) {

    public static final String DISPLAYFIELD_PREFIX = "plooi.displayfield";
    private static final String TMP_NS = "temp-namespace";

    public static ExtraMetadataMapping dynamic(String rootPath, String keyPath, String valuePath) {
        return new ExtraMetadataMapping(null, null, rootPath, null, keyPath, valuePath);
    }

    public static ExtraMetadataMapping dynamicDisplayfield(String rootPath, String keyPath, String valuePath) {
        return new ExtraMetadataMapping(null, DISPLAYFIELD_PREFIX, rootPath, null, keyPath, valuePath);
    }

    public static ExtraMetadataMapping basic(String key, String valuePath) {
        return new ExtraMetadataMapping(null, null, valuePath, key, null, ".");
    }

    public static ExtraMetadataMapping basicDisplayfield(String key, String valuePath) {
        return new ExtraMetadataMapping(null, DISPLAYFIELD_PREFIX, valuePath, key, null, ".");
    }

    public static ExtraMetadataMapping temp(String key, String valuePath) {
        return new ExtraMetadataMapping(TMP_NS, null, valuePath, key, null, ".");
    }

    public static List<String> getValue(List<ExtraMetadata> from, String key, String altValue) {
        return doGetValue(from, key, null).findFirst().orElse(List.of(altValue));
    }

    public static String getTempValue(List<ExtraMetadata> from, String key, String altValue) {
        return String.join(" ", getTempValues(from, key, List.of(altValue)));
    }

    public static List<String> getTempValues(List<ExtraMetadata> from, String key, List<String> altValues) {
        return doGetValue(from, key, TMP_NS).findFirst().orElse(altValues);
    }

    public static List<String> getTempValues(@Valid List<ExtraMetadata> from, String key) {
        return doGetValue(from, key, TMP_NS).flatMap(List::stream).toList();
    }

    private static Stream<List<String>> doGetValue(List<ExtraMetadata> from, String key, String ns) {
        return from == null
                ? Stream.empty()
                : from
                        .stream()
                        .filter(emi -> ns == null || ns.equals(emi.getNamespace()))
                        .flatMap(emi -> emi.getVelden().stream())
                        .filter(emvi -> emvi.getKey().equals(key))
                        .map(ExtraMetadataVeld::getValues);
    }

    public ExtraMetadata locateIn(List<ExtraMetadata> in) {
        var existingEm = in.stream()
                .filter(emi -> Objects.equals(emi.getNamespace(), this.namespace) && Objects.equals(emi.getPrefix(), this.prefix))
                .findAny();
        if (existingEm.isPresent()) {
            return existingEm.get();
        } else {
            var newEmi = new ExtraMetadata().namespace(this.namespace).prefix(this.prefix).velden(new LinkedHashSet<>());
            in.add(newEmi);
            return newEmi;
        }
    }

    public static void clearExtrametadata(List<ExtraMetadata> target) {
        var extrametadataIter = target.iterator();
        while (extrametadataIter.hasNext()) {
            var extrametadata = extrametadataIter.next();
            if (TMP_NS.equals(extrametadata.getNamespace()) // Temporary extrametadata can be used in source specific processing
                    || extrametadata.getVelden().isEmpty()) {
                extrametadataIter.remove();
            }
        }
    }
}

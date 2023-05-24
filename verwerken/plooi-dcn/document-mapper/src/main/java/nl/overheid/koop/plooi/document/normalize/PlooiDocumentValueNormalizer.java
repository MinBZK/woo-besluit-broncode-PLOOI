package nl.overheid.koop.plooi.document.normalize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.model.dictionary.PropertyFileHelper;
import nl.overheid.koop.plooi.model.dictionary.TooiDictionaries;
import nl.overheid.koop.plooi.model.dictionary.TooiDictionary;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Normalizes a PLOOI document by verifying its properties in TOOI dictionaries. This normalizer uses 2 property files
 * that configure it;
 * <ul>
 * <li><em>waardelijsten.properties</em> lists the dictionaries and the TOOI dictionary files for these.
 * <li><em>mapped_fields.properties</em> applies dictionaries on PLOOI properties (fields). A property can be mapped by
 * multiple dictionaries. For example, owmskern.creator can be a local or national governmental body.
 * </ul>
 * The configuration above can be extended with source integration specific:
 * <ul>
 * <li>Via <em>{@link Config#useExtraFieldProperties(String)}<em> one can add or override PLOOI properties mappings, for
 * example to restrict applicable dictionaries for owmskern.creator to only local governmental bodies.
 * <li>Via <em>{@link Config#useExtraDictionaryProperties(String)}<em> one can configure additional TOOI dictionary
 * files, for example to add source specific synonyms (alternative labels).
 * </ul>
 */
@SuppressWarnings({ "java:S5128", "java:S2583", "java:S2589" })
public final class PlooiDocumentValueNormalizer implements EnvelopeProcessing<PlooiEnvelope> {

    private static final String GLOBALFIELDPROPS_LOCATION = "/nl/overheid/koop/plooi/document/normalize/mapped_fields.properties";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, List<TooiDictionary>> enrichedDictionaries = new HashMap<>();

    /** Factory method, producing an instance of this normalizer class. */
    public static Config configure() {
        return new Config();
    }

    public static class Config {

        private String extraFieldPropsLocation;

        /**
         * @param location Location (inside classpath) of a (integration specific) field dictionary property file
         */
        public Config useExtraFieldProperties(String location) {
            this.extraFieldPropsLocation = location;
            return this;
        }

        private String extraDictPropsLocation;

        /**
         * @param location Same for a dictionary definition property file
         */
        public Config useExtraDictionaryProperties(String location) {
            this.extraDictPropsLocation = location;
            return this;
        }

        public PlooiDocumentValueNormalizer build() {
            return new PlooiDocumentValueNormalizer(this.extraFieldPropsLocation, this.extraDictPropsLocation);
        }
    }

    private PlooiDocumentValueNormalizer(String extraFieldPropsLocation, String extraDictPropsLocation) {
        var tooiDictionaries = new TooiDictionaries();
        var extraDictProps = PropertyFileHelper.readProperties(this.getClass(), extraDictPropsLocation, new Properties());
        var schemes = tooiDictionaries.getDictionaries().keySet();
        schemes.forEach(s -> tooiDictionaries.readIntoDictionary(
                tooiDictionaries.getDictionary(s).orElseThrow(() -> new NoSuchElementException("unknown scheme " + s)), extraDictProps.getProperty(s)));
        var fieldSchemes = new Properties();
        PropertyFileHelper.readProperties(this.getClass(), GLOBALFIELDPROPS_LOCATION, fieldSchemes);
        PropertyFileHelper.readProperties(this.getClass(), extraFieldPropsLocation, fieldSchemes);
        for (var fieldEntry : fieldSchemes.entrySet()) {
            var fieldDicts = this.enrichedDictionaries.computeIfAbsent(fieldEntry.getKey().toString(), k -> new ArrayList<>());
            for (var scheme : fieldEntry.getValue().toString().split(",")) {
                if (schemes.contains(scheme)) {
                    fieldDicts.add(tooiDictionaries.getDictionary(scheme).orElseThrow(() -> new NoSuchElementException("unknown scheme " + scheme)));
                } else {
                    throw new PlooiNormalizationException("Field properties refer to unknown scheme " + scheme);
                }
            }
        }
    }

    /**
     * Normalizes the plooi document, iterating over all the {@link IdentifiedResource} properties.
     *
     * @param  target The {@link PlooiEnvelope} to normalize
     * @return        The normalized {@link PlooiEnvelope}
     */
    @Override
    public PlooiEnvelope process(PlooiEnvelope target) {
        target.getClassificatiecollectie().getDocumentsoorten().forEach(res -> normalize(target, "Documentsoort", res));
        target.getClassificatiecollectie().getThemas().forEach(res -> normalize(target, "Thema", res));
        normalize(target, "Opsteller", target.getDocumentMeta().getOpsteller());
        normalize(target, "Verantwoordelijke", target.getDocumentMeta().getVerantwoordelijke());
        normalize(target, "Publisher", target.getDocumentMeta().getPublisher());
        return target;
    }

    private void normalize(PlooiEnvelope plooi, String field, IdentifiedResource resource) {
        if (isEmpty(resource)) {
            this.logger.trace(" - Skipping empty resource for field {}", field);
        } else if (!this.enrichedDictionaries.containsKey(field)) {
            this.logger.trace(" - Skipping unnormalized field {}", field);
        } else if (resource.getId() == null) {
            for (var dictionary : this.enrichedDictionaries.get(field)) {
                // Try till we find a matching result
                if (normalizeWithoutId(plooi, field, resource, dictionary)) {
                    return;
                }
            }
            diagnoseUnknown(plooi, DiagnosticCode.UNKNOWN_LABEL, field, resource);
        } else {
            for (var dictionary : this.enrichedDictionaries.get(field)) {
                if (normalizeWithId(plooi, field, resource, dictionary)) {
                    return;
                }
            }
            diagnoseUnknown(plooi, DiagnosticCode.UNKNOWN_ID, field, resource);
        }
    }

    private void diagnoseUnknown(PlooiEnvelope plooi, DiagnosticCode code, String field, IdentifiedResource resource) {
        plooi.status().addDiagnose(code, resource.getId(), resource.getLabel(), field);
        resource.id(null).label(null);
    }

    private boolean normalizeWithoutId(PlooiEnvelope plooi, String field, IdentifiedResource resource, TooiDictionary dictionary) {
        var origLabel = resource.getLabel();
        plooi.status().addDiagnose(DiagnosticCode.EMPTY_ID, "", origLabel, field);
        var resourceId = dictionary.findAltLabel(origLabel);
        if (resourceId.isEmpty()) {
            resourceId = dictionary.findPrefLabel(origLabel);
            if (resourceId.isEmpty()) {
                return false;
            }
        } else if (dictionary.findUri(resourceId.get()).isPresent()) {
            plooi.status().addDiagnose(DiagnosticCode.ALTLABEL, resourceId.get(), origLabel, field);
        } else {
            if (!"ignore".equalsIgnoreCase(resourceId.get())) {
                plooi.status().addDiagnose(DiagnosticCode.INCONSISTENT_ID, resourceId.get(), origLabel, field);
            }
            resource.id(null).label(null);
            return true;
        }
        resource.id(resourceId.get())
                .type(dictionary.getScheme())
                .label(dictionary.findUri(resourceId.get()).orElse(null)); // Overwrite alternative with preferred label
        if (!origLabel.equalsIgnoreCase(resource.getLabel())) {
            resource.bronwaarde(origLabel);
        }
        return true;
    }

    private boolean normalizeWithId(PlooiEnvelope plooi, String field, IdentifiedResource resource, TooiDictionary dictionary) {
        var origLabel = resource.getLabel();
        var prefLabel = dictionary.findUri(resource.getId());
        if (prefLabel.isEmpty()) {
            return false;
        } else if (StringUtils.isEmpty(origLabel)) {
            plooi.status().addDiagnose(DiagnosticCode.EMPTY_LABEL, resource.getId(), prefLabel.get(), field);
        } else if (!prefLabel.get().equalsIgnoreCase(origLabel)) {
            var altId = dictionary.findAltLabel(origLabel);
            if (altId.isEmpty()) {
                plooi.status().addDiagnose(DiagnosticCode.DIFF_LABEL, resource.getId(), origLabel, field);
            } else if (altId.get().equals(resource.getId())) {
                plooi.status().addDiagnose(DiagnosticCode.ALTLABEL, resource.getId(), origLabel, field);
            } else {
                plooi.status().addDiagnose(DiagnosticCode.INCONSISTENT_LABEL, altId.get(), origLabel, field);
            }
        }
        resource.id(resource.getId())
                .type(dictionary.getScheme())
                .label(prefLabel.get());
        if (!prefLabel.get().equalsIgnoreCase(origLabel)) {
            resource.bronwaarde(origLabel);
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " applying " + this.enrichedDictionaries;
    }

    public static boolean isEmpty(IdentifiedResource resource) {
        return resource == null || (resource.getId() == null && resource.getLabel() == null);
    }
}

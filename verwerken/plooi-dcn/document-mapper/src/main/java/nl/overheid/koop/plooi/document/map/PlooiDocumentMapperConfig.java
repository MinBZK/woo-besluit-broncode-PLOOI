package nl.overheid.koop.plooi.document.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Configuration for the PLOOI document mapper, containing paths and fixed values for the document attributes.
 */
public class PlooiDocumentMapperConfig {

    private final ConfigurableDocumentMapper parentMapper;

    public PlooiDocumentMapperConfig(ConfigurableDocumentMapper parent) {
        this.parentMapper = parent;
    }

    public ConfigurableDocumentMapper mapper() {
        return this.parentMapper;
    }

    String aanbieder;
    TextMapping officieleTitelMapping;
    TextMapping verkorteTitelMapping;
    TextMapping alternatieveTitelMapping;
    ResourceMapping documentsoortMapping;
    List<ResourceMapping> themaMappings = new ArrayList<>();
    String trefwoordPath;
    String identifierPath;
    String weblocatiePath;
    String weblocatieTemplate;
    String creatiedatumPath;
    DateMapping geldigheidsstartdatumMapping;
    ResourceMapping verantwoordelijkeMapping;
    ResourceMapping opstellerMapping;
    ResourceMapping publisherMapping;
    ResourceMapping languageMapping;
    ResourceMapping formatMapping;
    String onderwerpPath;
    TextMapping omschrijvingMapping;
    String aggregatiekenmerkPath;
    List<ExtraMetadataMapping> extraMetadataMappings = new ArrayList<>();
    List<Pair<TextMapping, Map<String, String>>> bodyTekstPathsEmbedded = new ArrayList<>();
    List<Pair<TextMapping, Map<String, String>>> bodyTekstPathsNested = new ArrayList<>();
    List<String> documentTextPaths = new ArrayList<>();

    public PlooiDocumentMapperConfig setAanbieder(String value) {
        this.aanbieder = value;
        return this;
    }

    public PlooiDocumentMapperConfig mapOfficieleTitel(TextMapping mapping) {
        this.officieleTitelMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapVerkorteTitel(TextMapping mapping) {
        this.verkorteTitelMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapAlternatieveTitel(TextMapping mapping) {
        this.alternatieveTitelMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapDocumentsoort(ResourceMapping mapping) {
        this.documentsoortMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig addThema(ResourceMapping mapping) {
        this.themaMappings.add(mapping);
        return this;
    }

    public PlooiDocumentMapperConfig mapTrefwoord(String path) {
        this.trefwoordPath = path;
        return this;
    }

    public PlooiDocumentMapperConfig mapIdentifier(String path) {
        this.identifierPath = path;
        return this;
    }

    public PlooiDocumentMapperConfig mapWeblocatie(String path) {
        this.weblocatiePath = path;
        return this;
    }

    public PlooiDocumentMapperConfig setWeblocatieTemplate(String template) {
        this.weblocatieTemplate = template;
        return this;
    }

    public PlooiDocumentMapperConfig mapCreatiedatum(String path) {
        this.creatiedatumPath = path;
        return this;
    }

    public PlooiDocumentMapperConfig mapGeldigheidsstartdatum(DateMapping mapping) {
        this.geldigheidsstartdatumMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapVerantwoordelijke(ResourceMapping mapping) {
        this.verantwoordelijkeMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapOpsteller(ResourceMapping mapping) {
        this.opstellerMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapPublisher(ResourceMapping mapping) {
        this.publisherMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapLanguage(ResourceMapping mapping) {
        this.languageMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapFormat(ResourceMapping mapping) {
        this.formatMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapOnderwerp(String path) {
        this.onderwerpPath = path;
        return this;
    }

    public PlooiDocumentMapperConfig mapOmschrijving(TextMapping mapping) {
        this.omschrijvingMapping = mapping;
        return this;
    }

    public PlooiDocumentMapperConfig mapAggregatiekenmerk(String path) {
        this.aggregatiekenmerkPath = path;
        return this;
    }

    public PlooiDocumentMapperConfig addExtraMetadata(ExtraMetadataMapping mapping) {
        this.extraMetadataMappings.add(mapping);
        return this;
    }

    /**
     * Configure paths to elements containing text which should be searchable and be displayed as document text (supporting
     * documents without PDF, etc.)
     *
     * @param  mapping The mapping configuration, supporting embedded HTML in the text (with JSoup to be used to extract it)
     * @return         This PlooiDocumentMapperConfig object
     */
    public PlooiDocumentMapperConfig addBodyTekst(TextMapping mapping) {
        return addBodyTekst(mapping, Map.of());
    }

    /**
     * Adds an option to {@link #addBodyTekst(String, boolean) addBodyTekst above} to configure tag mappings; the element
     * name of a text match is translated to a configured (HTML) tag, which is wrapped around the text. This supports
     * maintaining structure as provided in the input.
     * <p>
     * For example; Rijksoverheid.nl provides XML with "paragraphtitle" and "paragraph" element, which are displayed on the
     * portal using "h2" and "div" tags.
     */
    public PlooiDocumentMapperConfig addBodyTekst(TextMapping mapping, Map<String, String> tagMapping) {
        (mapping.parseEmbeddedHTML() ? this.bodyTekstPathsEmbedded : this.bodyTekstPathsNested).add(Pair.of(mapping, tagMapping));
        return this;
    }

    /**
     * Configure paths to elements containing text which should be searchable (without displaying as document text in the
     * portal). Mainly to be used by Tika mappers.
     */
    public PlooiDocumentMapperConfig addDocumentText(String path) {
        this.documentTextPaths.add(path);
        return this;
    }
}

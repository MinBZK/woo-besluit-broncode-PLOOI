package nl.overheid.koop.plooi.document.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.util.AggregatedVersion;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlooiDocumentMappers implements EnvelopeProcessing<PlooiEnvelope> {

    private static final String WILDCARD = "*";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, PlooiDocumentMapping> mappers = new LinkedHashMap<>();
    private final Map<List<String>, List<PlooiDocumentMapping>> altMappers = new LinkedHashMap<>();
    private PublicatieClient publicatieClient;

    public PlooiDocumentMappers withPublicatieClient(PublicatieClient publClient) {
        this.publicatieClient = publClient;
        return this;
    }

    /**
     * Configures a {@link PlooiDocumentMapping} that will map a particular manifestation.
     *
     * @param  forManifestatie The manifestation label to handle by this PlooiDocumentMapping
     * @param  mapper          The {@link PlooiDocumentMapping}
     * @return                 This PlooiDocumentMappers instance
     */
    public PlooiDocumentMappers addMapper(String forManifestatie, PlooiDocumentMapping mapper) {
        this.mappers.put(forManifestatie, mapper);
        return this;
    }

    /**
     * Configures a {@link PlooiDocumentMapping} that will map manifestations not configure with
     * {@link #addMapper(String, PlooiDocumentMapping)} above.
     *
     * @param  mapper The {@link PlooiDocumentMapping}
     * @return        This PlooiDocumentMappers instance
     */
    public PlooiDocumentMappers addCatchallMapper(PlooiDocumentMapping mapper) {
        this.mappers.put(WILDCARD, mapper);
        return this;
    }

    /**
     * Configures a number of alternative {@link PlooiDocumentMapping}s that will map a list of particular manifestations.
     * When one of the manifestations in the list is mapped, the others will be skipped.
     * <p>
     * Files are mapped in the order in which they are added to the PLOOI document. This can be tuned by the
     * {@link ConfigurableFileMapper}.
     *
     * @param  forManifestaties The manifestation labels to handle by the PlooiDocumentMappers
     * @param  mappers          The list of {@link PlooiDocumentMapping}. If the list contains just a single mapper, that
     *                          one will be used for all manifestation labels
     * @return                  This PlooiDocumentMappers instance
     */
    public PlooiDocumentMappers addAltMappers(List<String> forManifestaties, List<PlooiDocumentMapping> mappers) {
        this.altMappers.put(forManifestaties, mappers);
        return this;
    }

    /** Convenience method which just calls {@link #addAltMappers(List, List)} with a single PlooiDocumentMapping. */
    public PlooiDocumentMappers addAltMappers(List<String> forManifestaties, PlooiDocumentMapping multiMapper) {
        return addAltMappers(forManifestaties, List.of(multiMapper));
    }

    @Override
    public PlooiEnvelope process(PlooiEnvelope target) {
        this.logger.debug("Mapping {}", target);
        var latest = AggregatedVersion.aggregateLatestVersion(target.getPlooi().getVersies());
        if (latest.isEmpty()) {
            target.status().addDiagnose(DiagnosticCode.CANT_PARSE, "TODO");
        } else {
            var handled = new ArrayList<String>();
            for (var file : latest.get().toVersie().getBestanden()) {
                if (this.mappers.containsKey(file.getLabel())) {
                    map(target, this.mappers.get(file.getLabel()), file);
                    handled.add(file.getLabel());
                } else if (this.mappers.containsKey(WILDCARD)) {
                    map(target, this.mappers.get(WILDCARD), file);
                    handled.add(file.getLabel());
                } else if (!handled.contains(file.getLabel())) {
                    this.altMappers.entrySet()
                            .stream()
                            .filter(e -> e.getKey().contains(file.getLabel()))
                            .findFirst()
                            .ifPresentOrElse(
                                    e -> {
                                        map(target, e.getValue().get(e.getValue().size() == 1 ? 0 : e.getKey().indexOf(file.getLabel())), file);
                                        handled.addAll(e.getKey());
                                    },
                                    () -> this.logger.trace(" - Not mapping {} for label {}", file.getBestandsnaam(), file.getLabel()));
                } else {
                    this.logger.trace(" - Skipping {}, alternative with label {}", file.getBestandsnaam(), file.getLabel());
                }
            }
        }
        return target;
    }

    private void map(PlooiEnvelope target, PlooiDocumentMapping mapper, Bestand file) {
        try (InputStream contentStrm = Objects.requireNonNull(this.publicatieClient, "documentenClient is required")
                .getVersionedFile(target.getPlooiIntern().getDcnId(), PublicatieClient.LATEST, file.getLabel())) {
            this.logger.trace(" - Mapping {}", file.getBestandsnaam());
            mapper.populate(contentStrm, target);
        } catch (IOException e) {
            target.status().addDiagnose(DiagnosticCode.CANT_PARSE, e);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " applying " + this.mappers;
    }
}

package nl.overheid.koop.plooi.dcn.component.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nl.overheid.koop.plooi.dcn.model.Stage;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Body;
import nl.overheid.koop.plooi.model.data.Classificatiecollectie;
import nl.overheid.koop.plooi.model.data.Document;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Titelcollectie;
import nl.overheid.koop.plooi.model.data.util.AggregatedVersion;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import nl.overheid.koop.plooi.repository.data.PlooiManifest;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.apache.commons.lang3.StringUtils;

public class PlooiEnvelope extends PlooiManifest implements Envelope {

    private static final List<String> SUPPORTED_FILE_TYPES = List.of("pdf");

    private final Map<Stage, List<Relatie>> stagedRelations = new HashMap<>();
    private final StringBuilder documentText = new StringBuilder();
    private String verwerkingId;
    private String procesId;
    private Stage stage;
    private final Envelope.Diagnosis diagnosis = new Envelope.Diagnosis();

    public static boolean notSupported(String type) {
        return StringUtils.isBlank(type) || !PlooiEnvelope.SUPPORTED_FILE_TYPES.contains(type.toLowerCase());
    }

    /** For simpler test cases. */
    public PlooiEnvelope(String src, String... extId) {
        this(new Plooi());
        if (extId.length == 0) {
            throw new IllegalArgumentException("At least 1 externalIds is required for " + src);
        } else {
            getPlooiIntern()
                    .dcnId(DcnIdentifierUtil.generateDcnId(src, extId))
                    .sourceLabel(src)
                    .extId(List.of(extId));
            getDocumentMeta()
                    .pid("https://test.open.overheid.nl/documenten/" + getPlooiIntern().getDcnId());
        }
    }

    public PlooiEnvelope(Plooi p) {
        super(p);
    }

    public final Document getDocumentMeta() {
        return getOrSet(getPlooi()::getDocument,
                getPlooi()::setDocument,
                Document::new);
    }

    public final Titelcollectie getTitelcollectie() {
        return getOrSet(getDocumentMeta()::getTitelcollectie,
                getPlooi().getDocument()::setTitelcollectie,
                Titelcollectie::new);
    }

    public final Classificatiecollectie getClassificatiecollectie() {
        return getOrSet(getDocumentMeta()::getClassificatiecollectie,
                getPlooi().getDocument()::setClassificatiecollectie,
                Classificatiecollectie::new);
    }

    public final Body getBody() {
        return getOrSet(getPlooi()::getBody,
                getPlooi()::setBody,
                Body::new);
    }

    /**
     * Replace metadata in this PLOOI document with metadata from another document, keeping internal ({@link PlooiIntern}
     * and {@link Body}) data.
     */
    public void replacePlooi(Plooi other) {
        Objects.requireNonNull(other.getDocument(), "other.document is required").setPid(this.getDocumentMeta().getPid());
        getPlooi().document(other.getDocument());
        getPlooi().documentrelaties(other.getDocumentrelaties());
    }

    @Override
    public String getVerwerkingId() {
        return this.verwerkingId;
    }

    @Override
    public PlooiEnvelope verwerkingId(String verwId) {
        this.verwerkingId = verwId;
        return this;
    }

    public String getProcesId() {
        return this.procesId;
    }

    public PlooiEnvelope procesId(String procId) {
        this.procesId = procId;
        return this;
    }

    public Stage getStage() {
        return this.stage;
    }

    public PlooiEnvelope stage(Stage stg) {
        this.stage = stg;
        return this;
    }

    public String getDocumentText() {
        return this.documentText.toString().trim();
    }

    public PlooiEnvelope addToDocumentText(String text) {
        this.documentText.append(" ").append(text);
        return this;
    }

    public PlooiEnvelope addRelation(Relatie r) {
        return addRelation(this.stage, r);
    }

    public PlooiEnvelope addRelation(Stage stg, Relatie r) {
        this.stagedRelations.computeIfAbsent(stg, k -> new ArrayList<Relatie>()).add(r);
        return this;
    }

    private static final List<Relatie> NO_RELATIONS = new ArrayList<>();

    public List<Relatie> getRelationsForStage() {
        return this.stagedRelations.getOrDefault(this.stage, NO_RELATIONS);
    }

    public void fixBundlePartTitle(PublicatieClient publicatieClient) {
        if (!publicatieClient.getRelations(getPlooi().getPlooiIntern().getDcnId(), RelationType.BUNDEL).isEmpty()) {
            var titles = getTitelcollectie();
            titles.addAlternatieveTitelsItem(Objects.requireNonNull(titles.getOfficieleTitel(), "OfficieleTitel is required"));
            var latest = AggregatedVersion.aggregateLatestVersion(getPlooi().getVersies());
            if (latest.isPresent()) {
                latest.get()
                        .toVersie()
                        .getBestanden()
                        .stream()
                        .filter(Bestand::isGepubliceerd)
                        .findFirst()
                        .ifPresent(
                                pdf -> titles.setOfficieleTitel(
                                        titles.getOfficieleTitel() + " - " + (StringUtils.defaultIfBlank(pdf.getTitel(), pdf.getBestandsnaam()))));
            }
        }
    }

    @Override
    public Envelope.Diagnosis status() {
        return this.diagnosis;
    }
}

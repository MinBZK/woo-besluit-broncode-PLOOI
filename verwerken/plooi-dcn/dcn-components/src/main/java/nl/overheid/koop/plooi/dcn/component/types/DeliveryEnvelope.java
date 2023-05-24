package nl.overheid.koop.plooi.dcn.component.types;

import java.util.ArrayList;
import java.util.List;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;

public class DeliveryEnvelope implements Envelope {

    private final PlooiIntern plooiIntern = new PlooiIntern();
    private final Versie versie = new Versie();
    private final List<PlooiFile> plooiFiles = new ArrayList<>();
    private final List<Relatie> relations = new ArrayList<>();
    private final Envelope.Diagnosis diagnosis = new Envelope.Diagnosis();
    private String verwerkingId;

    public DeliveryEnvelope(String src, String... extIds) {
        if (extIds.length == 0) {
            throw new IllegalArgumentException("At least 1 externalIds is required for " + src);
        }
        this.plooiIntern.sourceLabel(src).extId(List.of(extIds)).dcnId(DcnIdentifierUtil.generateDcnId(src, extIds));
    }

    @Override
    public PlooiIntern getPlooiIntern() {
        return this.plooiIntern;
    }

    public Versie getVersie() {
        return this.versie;
    }

    public DeliveryEnvelope addPlooiFile(PlooiFile plooiFile) {
        this.plooiFiles.add(plooiFile);
        return this;
    }

    public List<PlooiFile> getPlooiFiles() {
        return this.plooiFiles;
    }

    public DeliveryEnvelope addRelation(Relatie r) {
        this.relations.add(r);
        return this;
    }

    public List<Relatie> getRelationsForStage() {
        return this.relations;
    }

    @Override
    public String getVerwerkingId() {
        return this.verwerkingId;
    }

    @Override
    public DeliveryEnvelope verwerkingId(String verwId) {
        this.verwerkingId = verwId;
        return this;
    }

    @Override
    public Envelope.Diagnosis status() {
        return this.diagnosis;
    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append(" ").append(getPlooiIntern().getDcnId()).toString();
    }
}

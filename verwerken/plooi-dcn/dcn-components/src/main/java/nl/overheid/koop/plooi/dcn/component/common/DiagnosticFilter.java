package nl.overheid.koop.plooi.dcn.component.common;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import nl.overheid.koop.plooi.dcn.component.types.Envelope;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.registration.model.Diagnose;
import org.apache.commons.lang3.tuple.Pair;

public final class DiagnosticFilter implements EnvelopeProcessing<Envelope> {

    private Map<DiagnosticCode, List<Pair<String, String>>> reject = new EnumMap<>(DiagnosticCode.class);

    private DiagnosticFilter() {
    }

    public static DiagnosticFilter configure() {
        return new DiagnosticFilter();
    }

    public DiagnosticFilter add(DiagnosticCode code) {
        this.reject.put(code, new ArrayList<>());
        return this;
    }

    public DiagnosticFilter add(DiagnosticCode code, String target) {
        return add(code, target, null);
    }

    public DiagnosticFilter add(DiagnosticCode code, String target, String value) {
        this.reject.computeIfAbsent(code, k -> new ArrayList<>()).add(Pair.of(target, value));
        return this;
    }

    @Override
    public Envelope process(Envelope plooi) {
        plooi.status().attachDiagnoseFilter(this);
        return plooi;
    }

    public boolean rejects(Diagnose diagnostic) {
        var rejectTargets = this.reject.get(DiagnosticCode.valueOf(diagnostic.getCode()));
        if (rejectTargets == null) {
            return false;
        } else if (rejectTargets.isEmpty()) {
            return true;
        } else {
            return rejectTargets.stream()
                    .anyMatch(t -> t.getKey().equals(diagnostic.getTarget())
                            && (t.getValue() == null || t.getValue().equals(diagnostic.getSourceLabel()) || t.getValue().equals(diagnostic.getSourceId())));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " rejecting " + this.reject;
    }
}

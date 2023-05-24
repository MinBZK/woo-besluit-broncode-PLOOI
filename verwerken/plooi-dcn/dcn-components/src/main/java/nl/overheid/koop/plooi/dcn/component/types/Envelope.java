package nl.overheid.koop.plooi.dcn.component.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nl.overheid.koop.plooi.dcn.component.common.DiagnosticFilter;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.registration.model.Diagnose;
import nl.overheid.koop.plooi.registration.model.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Envelope {

    Diagnosis status();

    PlooiIntern getPlooiIntern();

    String getVerwerkingId();

    Envelope verwerkingId(String verwId);

    class Diagnosis {

        private final Logger logger = LoggerFactory.getLogger(getClass());
        private boolean discarded;
        private DiagnosticFilter diagnosticFilter;
        private final List<Diagnose> diagnoses = new ArrayList<>();

        public Diagnosis discard() {
            this.discarded = true;
            return this;
        }

        public boolean isDiscarded() {
            return this.discarded || getDiagnoses().stream().anyMatch(d -> d.getSeverity().compareTo(Severity.ERROR) >= 0);
        }

        public Diagnosis attachDiagnoseFilter(DiagnosticFilter diagFltr) {
            this.diagnosticFilter = diagFltr;
            return this;
        }

        public List<Diagnose> getDiagnoses() {
            return this.diagnoses;
        }

        public String getDiagnosticSummary() {
            return this.diagnoses.stream()
                    .map(d -> new StringBuilder().append(d.getTarget()).append(":").append(d.getCode()))
                    .collect(Collectors.joining(" "));
        }

        public Diagnosis addDiagnose(Diagnose diagnose) {
            if (diagnose != null
                    && (this.diagnosticFilter == null || !this.diagnosticFilter.rejects(diagnose))
                    && !this.diagnoses.contains(diagnose)) {
                this.diagnoses.add(diagnose);
                this.logger.debug(" - Added {} to {}", diagnose, this);
            }
            return this;
        }

        public Diagnosis addDiagnose(DiagnosticCode cd, Exception e) {
            return addDiagnose(cd, DiagnosticCode.getSeverity(cd), DiagnosticCode.buildErrorMessage(e));
        }

        public Diagnosis addDiagnose(DiagnosticCode cd, String msg) {
            return addDiagnose(cd, DiagnosticCode.getSeverity(cd), msg);
        }

        public Diagnosis addDiagnose(DiagnosticCode cd, Severity severity, Exception e) {
            return addDiagnose(cd, severity, DiagnosticCode.buildErrorMessage(e));
        }

        public Diagnosis addDiagnose(DiagnosticCode cd, Severity severity, String msg) {
            return addDiagnose(new Diagnose()
                    .code(cd.name())
                    .severity(severity)
                    .message(Objects.requireNonNull(msg, "Diagnostic message is required")));
        }

        public Diagnosis addDiagnose(DiagnosticCode cd, String srcId, String srcLbl, String trgt) {
            Diagnose diagnose = new Diagnose()
                    .code(cd.name())
                    .severity(DiagnosticCode.getSeverity(cd))
                    .sourceId(DiagnosticCode.setText(srcId))
                    .sourceLabel(DiagnosticCode.setText(srcLbl))
                    .target(DiagnosticCode.setTarget(trgt));
            diagnose.message(DiagnosticCode.createMessage(diagnose));
            return addDiagnose(diagnose);
        }

        @Override
        public String toString() {
            var str = new StringBuilder(super.toString()).append(" with ");
            if (this.diagnoses.isEmpty()) {
                str.append("no");
            } else {
                str.append(this.diagnoses.size());
            }
            return str.append(" diagnostics").toString();
        }
    }
}

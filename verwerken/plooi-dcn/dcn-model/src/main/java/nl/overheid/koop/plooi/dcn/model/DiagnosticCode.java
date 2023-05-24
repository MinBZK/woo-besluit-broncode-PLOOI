package nl.overheid.koop.plooi.dcn.model;

import java.util.Objects;
import nl.overheid.koop.plooi.registration.model.Diagnose;
import nl.overheid.koop.plooi.registration.model.Severity;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public enum DiagnosticCode {

    // Generic codes
    INFO(Severity.INFO, null),
    WARNING(Severity.WARNING, null),
    ERROR(Severity.ERROR, null),
    INTEGRATION(Severity.ERROR, null),
    CANT_PARSE(Severity.ERROR, null),
    // Validator diagnostics
    REQUIRED(Severity.ERROR, "Missing required field %s"),
    REQUIRED_DEFAULT(Severity.INFO, "Missing required field %s, set it to default value '%s'"),
    REQUIRED_ID(Severity.ERROR, "Missing required uri of field %s with label '%s'"),
    REQUIRED_LABEL(Severity.ERROR, "Missing required label of field %s%s with uri %s"),
    REQUIRED_SCHEME(Severity.WARNING, "Missing required scheme of field %s with label '%s' and uri %s"),
    DATE_FORMAT(Severity.INFO, "Illegal date format in field %s: '%s'"),
    DERIVED(Severity.INFO, "Derived value of field %s as '%s'"),
    // Mapper diagnostics
    UNKNOWN_ID(Severity.WARNING, "The uri of field %s with label '%s' and uri %s is not known"),
    UNKNOWN_LABEL(Severity.WARNING, "The label of field %s with label '%s' is not known"),
    EMPTY_ID(Severity.INFO, "The uri of field %s with label '%s' was empty"),
    EMPTY_LABEL(Severity.INFO, "The label of field %s identified as %s with uri %s was empty"),
    DIFF_LABEL(Severity.INFO, "The label of field %s with label '%s' and uri %s is not a known alternative for the uri"),
    ALTLABEL(Severity.INFO, "The label of field %s with label '%s' and uri %s is a known alternative"),
    INCONSISTENT_ID(Severity.WARNING,
            "The preferred label for field %s with alternative label '%s' and uri %s cannot be found (thesaurus inconsistency)"),
    INCONSISTENT_LABEL(Severity.WARNING,
            "The field %s with alternative label '%s' links to a preferred label with a different uri %s (thesaurus inconsistency)"),
    EMPTY_TEXT(Severity.INFO, "Missing indexable text"),
    BUNDLE_ID_MAPPING(Severity.INFO, null),
    DISCARD(Severity.INFO, null),
    ;

    private final Severity severity;
    private final String template;
    private static final int MAX_TEXT_LENGTH = 256;

    DiagnosticCode(Severity sev, String templ) {
        this.severity = sev;
        this.template = templ;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", this.severity, super.toString());
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public String getTemplate() {
        return this.template;
    }

    public static Severity getSeverity(DiagnosticCode code) {
        for (DiagnosticCode codeTemplate : DiagnosticCode.values()) {
            if (codeTemplate.name().equals(code.name())) {
                return codeTemplate.getSeverity();
            }
        }
        return null;
    }

    public static String createMessage(Diagnose diagnose) {
        for (DiagnosticCode codeTemplate : DiagnosticCode.values()) {
            if (codeTemplate.name().equals(diagnose.getCode())) {
                return String.format(codeTemplate.getTemplate(), diagnose.getTarget(), diagnose.getSourceLabel(), diagnose.getSourceId());
            }
        }
        return null;
    }

    public static String buildErrorMessage(Exception e) {
        var message = ExceptionUtils.getMessage(e);
        var rootMessage = ExceptionUtils.getRootCauseMessage(e);
        return message.equals(rootMessage) ? message : (message + ": " + rootMessage);
    }

    public static String setTarget(String trgt) {
        return StringUtils.abbreviate(Objects.requireNonNull(trgt, "Diagnostic target is required"), MAX_TEXT_LENGTH);
    }

    public static String setText(String text) {
        return StringUtils.abbreviate(StringUtils.trimToEmpty(text), MAX_TEXT_LENGTH);
    }
}

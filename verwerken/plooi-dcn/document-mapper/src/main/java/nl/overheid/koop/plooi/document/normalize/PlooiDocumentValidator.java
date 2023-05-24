package nl.overheid.koop.plooi.document.normalize;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import nl.overheid.koop.plooi.dcn.component.types.EnvelopeProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.document.map.ExtraMetadataMapping;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Classificatiecollectie;
import nl.overheid.koop.plooi.model.data.Document;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.model.data.Titelcollectie;
import nl.overheid.koop.plooi.model.data.Versie;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "java:S2583", "java:S2589" })
public class PlooiDocumentValidator implements EnvelopeProcessing<PlooiEnvelope> {

    static final String UNKNOWN = "Onbekend";
    static final String UNKNOWN_ORG_ID = "https://identifier.overheid.nl/tooi/id/organisatie_onbekend";
    static final String UNKNOWN_DOCTYPE_ID = "https://identifier.overheid.nl/tooi/id/documentsoort_onbekend";
    static final String UNKNOWN_THEME_ID = "https://identifier.overheid.nl/tooi/id/thema_onbekend";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public PlooiEnvelope process(PlooiEnvelope target) {
        if (target.status().isDiscarded()) {
            this.logger.debug("Skipping validation of discarded {}", target);
        } else {
            validateTitelsReq(target, target.getTitelcollectie());
            validateClassificatiesIds(target,
                    validateClassificatiesReq(target,
                            clearClassificaties(target.getClassificatiecollectie())));
            validateDocumentIds(target,
                    validateDocumentReq(target,
                            clearDocument(target.getDocumentMeta())));
            validatePlooiInternReq(target, target.getPlooiIntern());
            validateSupportedTypes(target);
        }
        return target;
    }

    private Titelcollectie validateTitelsReq(PlooiEnvelope plooi, Titelcollectie titels) {
        validateRequiredObject(plooi, "OfficieleTitel", () -> StringUtils.isBlank(titels.getOfficieleTitel()), null, null, null);
        return titels;
    }

    private Classificatiecollectie clearClassificaties(Classificatiecollectie classificaties) {
        clearEmptyResources(classificaties.getDocumentsoorten());
        clearEmptyResources(classificaties.getThemas());
        return classificaties;
    }

    private Classificatiecollectie validateClassificatiesReq(PlooiEnvelope plooi, Classificatiecollectie classificaties) {
        validateRequiredResource(plooi, "Documentsoort", () -> classificaties.getDocumentsoorten().isEmpty(),
                IdentifiedResource::new, UNKNOWN_DOCTYPE_ID, classificaties::addDocumentsoortenItem);
        validateRequiredResource(plooi, "Thema", () -> classificaties.getThemas().isEmpty(),
                IdentifiedResource::new, UNKNOWN_THEME_ID, classificaties::addThemasItem);
        return classificaties;
    }

    private Classificatiecollectie validateClassificatiesIds(PlooiEnvelope plooi, Classificatiecollectie classificaties) {
        classificaties.getDocumentsoorten().forEach(res -> validateRequiredIdLbl(plooi, res, "Documentsoort"));
        classificaties.getThemas().forEach(res -> validateRequiredIdLbl(plooi, res, "Thema"));
        return classificaties;
    }

    private Document clearDocument(Document document) {
        if (document.getExtraMetadata() != null) {
            ExtraMetadataMapping.clearExtrametadata(document.getExtraMetadata());
        }
        return document;
    }

    private Document validateDocumentReq(PlooiEnvelope plooi, Document document) {
        validateRequiredResource(plooi, "Opsteller",
                () -> PlooiDocumentValueNormalizer.isEmpty(document.getOpsteller()) && StringUtils.isBlank(document.getNaamOpsteller()),
                IdentifiedResource::new, UNKNOWN_ORG_ID, document::setOpsteller);
        validateRequiredResource(plooi, "Verantwoordelijke", () -> PlooiDocumentValueNormalizer.isEmpty(document.getVerantwoordelijke()),
                IdentifiedResource::new, UNKNOWN_ORG_ID, document::setVerantwoordelijke);
        validateRequiredResource(plooi, "Publisher", () -> PlooiDocumentValueNormalizer.isEmpty(document.getPublisher()),
                IdentifiedResource::new, UNKNOWN_ORG_ID, document::setPublisher);
        if (document.getLanguage() == null
                || StringUtils.isBlank(document.getLanguage().getLabel())
                || document.getLanguage().getLabel().toLowerCase().startsWith("nl")) {
            document.setLanguage(new IdentifiedResource().id("http://publications.europa.eu/resource/authority/language/NLD").label("Nederlands"));
        }
        return document;
    }

    private Document validateDocumentIds(PlooiEnvelope plooi, Document document) {
        validateRequiredIdLbl(plooi, document.getOpsteller(), "Opsteller");
        validateRequiredIdLbl(plooi, document.getVerantwoordelijke(), "Verantwoordelijke");
        validateRequiredIdLbl(plooi, document.getPublisher(), "Publisher");
        validateRequiredIdLbl(plooi, document.getFormat(), "Format");
        validateRequiredIdLbl(plooi, document.getLanguage(), "Language");
        return document;
    }

    private PlooiIntern validatePlooiInternReq(PlooiEnvelope plooi, PlooiIntern intern) {
        validateRequiredObject(plooi, "Aanbieder", () -> StringUtils.isBlank(intern.getAanbieder()), null, null, null);
        validateRequiredObject(plooi, "DcnId", () -> StringUtils.isBlank(intern.getDcnId()), null, null, null);
        validateRequiredObject(plooi, "ExtId", () -> intern.getExtId() == null || intern.getExtId().isEmpty(), null, null, null);
        validateRequiredObject(plooi, "SourceLabel", () -> StringUtils.isBlank(intern.getSourceLabel()), null, null, null);
        return intern;
    }

    private void validateSupportedTypes(PlooiEnvelope target) {
        target.getVersies()
                .stream()
                .map(Versie::getBestanden)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Bestand::isGepubliceerd)
                .filter(f -> PlooiEnvelope.notSupported(f.getLabel()))
                .filter(f -> PlooiEnvelope.notSupported(FilenameUtils.getExtension(f.getBestandsnaam())))
                .forEach(f -> f.setGepubliceerd(false));
    }

    private <T extends IdentifiedResource> void clearEmptyResources(List<T> resources) {
        var resourceIter = resources.iterator();
        while (resourceIter.hasNext()) {
            if (PlooiDocumentValueNormalizer.isEmpty(resourceIter.next())) {
                resourceIter.remove();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends IdentifiedResource> void validateRequiredResource(PlooiEnvelope plooi, String target, BooleanSupplier condition,
            Supplier<T> unknownSupplier, String unknownId, Consumer<T> subject) {
        validateRequiredObject(plooi, target, condition,
                () -> (T) unknownSupplier.get().id(unknownId).label(UNKNOWN), subject, T::getLabel);
    }

    private <T extends Object> void validateRequiredObject(PlooiEnvelope plooi, String target, BooleanSupplier condition,
            Supplier<T> supplier, Consumer<T> subject, Function<T, String> optDiagValueFunc) {
        if (condition.getAsBoolean()) {
            if (supplier == null) {
                plooi.status().addDiagnose(DiagnosticCode.REQUIRED, null, null, target);
            } else {
                T unknown = supplier.get();
                subject.accept(unknown);
                String diagVal = optDiagValueFunc == null ? unknown.toString() : optDiagValueFunc.apply(unknown);
                plooi.status().addDiagnose(DiagnosticCode.REQUIRED_DEFAULT, null, diagVal, target);
            }
        }
    }

    private void validateRequiredIdLbl(PlooiEnvelope plooi, IdentifiedResource resource, String target) {
        if (resource != null && resource.getId() == null) {
            plooi.status().addDiagnose(DiagnosticCode.REQUIRED_ID, null, resource.getLabel(), target);
        } else if (resource != null && resource.getLabel() == null) {
            plooi.status().addDiagnose(DiagnosticCode.REQUIRED_LABEL, resource.getId(), null, target);
        }
    }
}

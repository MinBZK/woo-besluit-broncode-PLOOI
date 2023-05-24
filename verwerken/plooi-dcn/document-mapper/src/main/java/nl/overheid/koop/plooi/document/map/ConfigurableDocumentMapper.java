package nl.overheid.koop.plooi.document.map;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.Classificatiecollectie;
import nl.overheid.koop.plooi.model.data.Document;
import nl.overheid.koop.plooi.model.data.Geldigheid;
import nl.overheid.koop.plooi.model.data.Titelcollectie;
import nl.overheid.koop.plooi.model.data.util.XMLHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/** Maps input to a {@link PlooiEnvelope}. */
public final class ConfigurableDocumentMapper implements PlooiDocumentMapping {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PlooiDocumentMapperConfig cfg = new PlooiDocumentMapperConfig(this);
    private final PlooiMapping plooiMappingFact;

    /**
     * Factory method, producing an instance of this mapper class with accompanying {@link PlooiDocumentMapperConfig}
     *
     * @param  mappingFact The {@link PlooiMappingInstance} factory to use by this mapper
     * @return             The {@link PlooiDocumentMapperConfig} for this mapper
     */
    public static PlooiDocumentMapperConfig configureWith(PlooiMapping mappingFact) {
        return new ConfigurableDocumentMapper(mappingFact).cfg;
    }

    /** Use {@link #configureWith()} to instantiate this mapper. */
    private ConfigurableDocumentMapper(PlooiMapping mappingFact) {
        this.plooiMappingFact = mappingFact;
    }

    @Override
    public PlooiEnvelope populate(InputStream srcStrm, PlooiEnvelope target) {
        try {
            this.logger.debug("Populating {}", target);
            PlooiMappingInstance pm = this.plooiMappingFact.getInstance(srcStrm);
            PlooiMappingInstance.setWith(this.cfg.aanbieder, target.getPlooiIntern()::setAanbieder);
            populateTitelcollectie(pm, target.getTitelcollectie());
            populateClassificatiecollectie(pm, target.getClassificatiecollectie());
            populateDocumentMeta(pm, target.getDocumentMeta());
            populateBodyTekst(pm, target);
        } catch (Exception e) {
            target.status().addDiagnose(DiagnosticCode.CANT_PARSE, this.plooiMappingFact.getParsingErrorSeverity(), e);
        }
        return target;
    }

    private void populateTitelcollectie(PlooiMappingInstance pm, Titelcollectie titelcollectie) {
        pm.mapString(
                TextMapping.getPath(this.cfg.officieleTitelMapping),
                t -> titelcollectie.setOfficieleTitel(TextMapping.getText(this.cfg.officieleTitelMapping, t)));
        pm.mapStrings(
                TextMapping.getPath(this.cfg.verkorteTitelMapping),
                titelcollectie.getVerkorteTitels(),
                t -> titelcollectie.setVerkorteTitels(TextMapping.getTexts(this.cfg.verkorteTitelMapping, t)));
        pm.mapStrings(
                TextMapping.getPath(this.cfg.alternatieveTitelMapping),
                titelcollectie.getAlternatieveTitels(),
                t -> titelcollectie.setAlternatieveTitels(TextMapping.getTexts(this.cfg.alternatieveTitelMapping, t)));
    }

    private void populateClassificatiecollectie(PlooiMappingInstance pm, Classificatiecollectie classificaties) {
        pm.mapResources(this.cfg.documentsoortMapping, classificaties.getDocumentsoorten());
        pm.mapResources(this.cfg.themaMappings, classificaties.getThemas());
        pm.mapStrings(this.cfg.trefwoordPath, classificaties.getTrefwoorden(), classificaties::setTrefwoorden);
    }

    private void populateDocumentMeta(PlooiMappingInstance pm, Document documentMeta) {
        pm.mapStrings(
                this.cfg.identifierPath,
                documentMeta.getIdentifiers() == null ? null : List.copyOf(documentMeta.getIdentifiers()),
                l -> documentMeta.identifiers(l == null ? null : Set.copyOf(l)));
        pm.mapString(this.cfg.weblocatiePath,
                url -> documentMeta.setWeblocatie(this.cfg.weblocatieTemplate == null ? url : String.format(this.cfg.weblocatieTemplate, url)));
        pm.mapString(this.cfg.creatiedatumPath, documentMeta::setCreatiedatum);
        pm.mapDate(this.cfg.geldigheidsstartdatumMapping, d -> documentMeta.geldigheid(new Geldigheid().begindatum(LocalDate.from(d))));
        pm.mapResource(this.cfg.verantwoordelijkeMapping, documentMeta::setVerantwoordelijke);
        pm.mapResource(this.cfg.opstellerMapping, documentMeta::setOpsteller);
        pm.mapResource(this.cfg.publisherMapping, documentMeta::setPublisher);
        pm.mapResource(this.cfg.languageMapping, documentMeta::setLanguage);
        pm.mapResource(this.cfg.formatMapping, documentMeta::setFormat);
        pm.mapStrings(
                this.cfg.onderwerpPath,
                documentMeta.getOnderwerpen(),
                documentMeta::setOnderwerpen);
        pm.mapStrings(
                TextMapping.getPath(this.cfg.omschrijvingMapping),
                documentMeta.getOmschrijvingen(),
                t -> documentMeta.setOmschrijvingen(TextMapping.getTexts(this.cfg.omschrijvingMapping, t)));
        pm.mapString(this.cfg.aggregatiekenmerkPath, documentMeta::setAggregatiekenmerk);
        // hasParts en isPartOf are set via DocumentCollecting
        for (var extrametadataPath : this.cfg.extraMetadataMappings) {
            documentMeta.setExtraMetadata(pm.mapExtraMetadatas(extrametadataPath, documentMeta.getExtraMetadata()));
        }
    }

    private void populateBodyTekst(PlooiMappingInstance pm, PlooiEnvelope target) {
        if (!this.cfg.bodyTekstPathsEmbedded.isEmpty()) {
            var teksts = new ArrayList<Pair<TextMapping, String>>();
            this.cfg.bodyTekstPathsEmbedded
                    .forEach(pathMap -> pm.mapTaggedStrings(TextMapping.getPath(pathMap.getKey()))
                            .forEach(
                                    taggedVal -> teksts.add(Pair.of(pathMap.getKey(),
                                            pathMap.getValue().isEmpty()
                                                    ? taggedVal.getValue()
                                                    : String.format("<%1$s>%2$s</%1$s>",
                                                            pathMap.getValue().getOrDefault(taggedVal.getKey(), "div"),
                                                            taggedVal.getValue())))));
            if (!teksts.isEmpty()) {
                teksts.forEach(t -> {
                    target.getBody().addTekstItem(t.getValue());
                    target.addToDocumentText(TextMapping.getText(t.getKey(), t.getValue()));
                });
            }
        }
        if (!this.cfg.bodyTekstPathsNested.isEmpty()) {
            var tekstNodes = new ArrayList<Node>();
            this.cfg.bodyTekstPathsNested
                    .forEach(pathMap -> pm.mapDomNode(TextMapping.getPath(pathMap.getKey()), tekstNodes));
            tekstNodes.forEach(node -> {
                target.getBody().addTekstItem(XMLHelper.toString(node, false));
                target.addToDocumentText(node.getTextContent());
            });
        }
        pm.mapStrings(
                this.cfg.documentTextPaths,
                null,
                texts -> texts.forEach(target::addToDocumentText));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " applying " + this.plooiMappingFact;
    }
}

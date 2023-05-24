package nl.overheid.koop.plooi.dcn.solr;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import nl.overheid.koop.plooi.dcn.component.types.ObjectProcessing;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Classificatiecollectie;
import nl.overheid.koop.plooi.model.data.Document;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Titelcollectie;
import nl.overheid.koop.plooi.model.data.legacy.ToLegacy;
import nl.overheid.koop.plooi.model.data.util.AggregatedVersion;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.repository.client.ClientException;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrDocumentBuilder implements ObjectProcessing<String> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ToLegacy toLegacy = new ToLegacy();
    private final PublicatieClient publicatieClient;

    public SolrDocumentBuilder(PublicatieClient publClient) {
        this.publicatieClient = publClient;
    }

    @Override
    public SolrInputDocument process(String dcnId) {
        this.logger.debug("Building Solr document for {}", dcnId);
        var plooi = this.publicatieClient.getPlooi(dcnId);
        plooi.documentrelaties(this.publicatieClient.getRelations(dcnId));
        var target = new PlooiEnvelope(plooi);
        try {
            target.addToDocumentText(this.publicatieClient.getText(dcnId));
        } catch (ClientException e) {
            if (e.getStatusCode() != 404) {
                throw e;
            } // else, ignore and go on without text, if it's not in the repository, it's empty
        }
        return buildSolrDocument(target);
    }

    public SolrInputDocument buildSolrDocument(PlooiEnvelope target) {
        var latestVersion = AggregatedVersion.aggregateLatestVersion(target.getPlooi().getVersies());
        var versieNr = latestVersion.isPresent() ? latestVersion.get().getVersie().getNummer() : Integer.valueOf(1);
        var versieSuffix = "_" + versieNr;
        var solrDoc = new SolrInputDocument();
        var metadata = new StringBuilder();
        String identifier = populateDocumentMeta(solrDoc, metadata, target.getDocumentMeta(), versieSuffix);
        var metaxml = this.toLegacy.convertPart(target.getPlooi(), "plooi:meta");
        var documentenxml = this.toLegacy.convertPart(target.getPlooi(), "plooi:documenten");
        populateTitelcollectie(solrDoc, target.getTitelcollectie());
        populateClassificatiecollectie(solrDoc, metadata, target.getClassificatiecollectie());
        populateBundleProperties(solrDoc, target, versieSuffix);
        populatePlooiIntern(solrDoc, metadata, target.getPlooiIntern(), versieNr);
        populateVersion(solrDoc, target, latestVersion);
        populateOther(solrDoc, target, identifier, PlooiBindings.plooiBinding().marshalToString(target.getPlooi()), metaxml, documentenxml);
        addToSolrDocument(solrDoc, "metadata", metadata.toString());
        return solrDoc;
    }

    private String populateDocumentMeta(SolrInputDocument solrDoc, StringBuilder metadata, Document documentMeta, String versieSuffix) {
        addToSolrDocument(solrDoc, "blocktype", "document");
        String identifier = ToLegacy.stripPid(Objects.requireNonNull(documentMeta.getPid(), "pid is required"));
        addToSolrDocument(solrDoc, "id", identifier.concat(versieSuffix));
        addToSolrDocument(solrDoc, "identifier", identifier);
        addToSolrDocument(solrDoc, "workid", identifier);
        addToSolrDocument(solrDoc, "source", documentMeta.getWeblocatie());
        if (documentMeta.getGeldigheid() != null) {
            addToSolrDocument(solrDoc, "available_start", date2Solr(documentMeta.getGeldigheid().getBegindatum()));
            addToSolrDocument(solrDoc, "available_end", date2Solr(documentMeta.getGeldigheid().getEinddatum()));
        }
        addToSolrDocument(solrDoc, "issued", date2Solr(ToLegacy.convertCreatiedatum(documentMeta.getCreatiedatum())));
        addResource(solrDoc, metadata, "creator", documentMeta.getOpsteller());
        addToSolrDocument(solrDoc, "creator", documentMeta.getNaamOpsteller());
        addResource(solrDoc, metadata, "publisher", documentMeta.getPublisher());
        addResource(solrDoc, metadata, "verantwoordelijke", documentMeta.getVerantwoordelijke());
        addToSolrDocument(solrDoc, "language", documentMeta.getLanguage() == null ? null : documentMeta.getLanguage().getLabel());
        if (documentMeta.getOmschrijvingen() != null) {
            documentMeta.getOmschrijvingen().forEach(desc -> addToSolrDocument(solrDoc, "description", desc));
        }
        if (documentMeta.getOnderwerpen() != null) {
            documentMeta.getOnderwerpen().forEach(desc -> addToSolrDocument(solrDoc, "subject", desc));
        }
        if (documentMeta.getPublicatiebestemming() != null) {
            documentMeta.getPublicatiebestemming().forEach(desc -> addToSolrDocument(solrDoc, "publicatiebestemming", desc));
        }
        if (documentMeta.getExtraMetadata() != null) {
            documentMeta
                    .getExtraMetadata()
                    .stream()
                    .map(emi -> Pair.of(emi.getPrefix(), emi.getVelden()))
                    .filter(emiPair -> Objects.nonNull(emiPair.getRight()))
                    .flatMap(emiPair -> emiPair.getRight().stream().map(emviPair -> Pair.of(emiPair.getLeft(), emviPair)))
                    .flatMap(emviPair -> emviPair.getRight().getValues().stream().map(val -> Triple.of(emviPair.getLeft(), emviPair.getRight().getKey(), val)))
                    .forEach(triple -> {
                        addToSolrDocument(solrDoc, buildExtraMetadataField(triple.getLeft(), triple.getMiddle()), triple.getRight());
                        metadata.append(" ").append(triple.getRight());
                    });
        }
        if (documentMeta.getIdentifiers() != null) {
            documentMeta.getIdentifiers().forEach(id -> {
                addToSolrDocument(solrDoc, buildExtraMetadataField("plooi.displayfield", "identificatienummer"), id);
                metadata.append(" ").append(id);
            });
        }
        if (documentMeta.getAggregatiekenmerk() != null) {
            addToSolrDocument(solrDoc, buildExtraMetadataField("plooi.displayfield", "aggregatiekenmerk"), documentMeta.getAggregatiekenmerk());
            metadata.append(" ").append(documentMeta.getAggregatiekenmerk());
        }
        return identifier;
    }

    private String buildExtraMetadataField(String prefix, String field) {
        return new StringBuilder("extrametadata_")
                .append(prefix == null ? "" : prefix.concat("."))
                .append(field)
                .toString();
    }

    private void populateClassificatiecollectie(SolrInputDocument solrDoc, StringBuilder metadata, Classificatiecollectie classificatiecollectie) {
        if (classificatiecollectie.getDocumentsoorten() != null && !classificatiecollectie.getDocumentsoorten().isEmpty()) {
            var last = classificatiecollectie.getDocumentsoorten().get(classificatiecollectie.getDocumentsoorten().size() - 1);
            addResource(solrDoc, metadata, "type", last);
            addResource(solrDoc, metadata, "informatiecategorie", last);
        }
        if (classificatiecollectie.getOnderwerpenRonl() != null) {
            classificatiecollectie.getOnderwerpenRonl().forEach(oronl -> addToSolrDocument(solrDoc, "onderwerpenRonl_id", oronl.getId()));
        }
        if (classificatiecollectie.getThemas() != null) {
            classificatiecollectie.getThemas().forEach(thema -> addResource(solrDoc, metadata, "topthema", thema));
        }
    }

    private void populateTitelcollectie(SolrInputDocument solrDoc, Titelcollectie titelCollectie) {
        addToSolrDocument(solrDoc, "title",
                Objects.requireNonNull(titelCollectie.getOfficieleTitel(), "officieleTitel is required"));
        addToSolrDocument(solrDoc, "title_sort", titelCollectie.getOfficieleTitel().toLowerCase());
        // alternatieveTitels is not indexed
    }

    private void populatePlooiIntern(SolrInputDocument solrDoc, StringBuilder metadata, PlooiIntern plooiIntern, Integer versieNr) {
        addToSolrDocument(solrDoc, "dcn_id", Objects.requireNonNull(plooiIntern.getDcnId(), "dcn_id is required"));
        addToSolrDocument(solrDoc, "versie", versieNr);
        addToSolrDocument(solrDoc, "aanbieder", plooiIntern.getAanbieder());
        addToSolrDocument(solrDoc, "source_label", plooiIntern.getSourceLabel());
        if (plooiIntern.getExtId() != null && !plooiIntern.getExtId().isEmpty()) {
            addToSolrDocument(solrDoc, "external_id", plooiIntern.getExtId().get(0));
        }
        metadata.append(" ").append(plooiIntern.getAanbieder());
    }

    private static final String IS_BUNDLE = "isbundel";
    private static final String PART_OF = "ispartof";
    private static final String PARTS = "haspart";
    private static final String GROUP = "groupid";

    private void populateBundleProperties(SolrInputDocument solrDoc, PlooiEnvelope target, String versieSuffix) {
        var parts = getRelationsForRole(target, RelationType.ONDERDEEL, versieSuffix).toList();
        var bundle = getRelationsForRole(target, RelationType.BUNDEL, "").findAny();
        if (bundle.isPresent()) {
            addToSolrDocument(solrDoc, IS_BUNDLE, Boolean.FALSE);
            addToSolrDocument(solrDoc, PART_OF, bundle.get().concat(versieSuffix));
            addToSolrDocument(solrDoc, GROUP, bundle.get());
        } else if (!parts.isEmpty()) {
            addToSolrDocument(solrDoc, IS_BUNDLE, Boolean.TRUE);
            addToSolrDocument(solrDoc, PARTS, parts);
            addToSolrDocument(solrDoc, GROUP, target.getPlooiIntern().getDcnId());
        } else {
            addToSolrDocument(solrDoc, IS_BUNDLE, Boolean.FALSE);
            addToSolrDocument(solrDoc, GROUP, target.getPlooiIntern().getDcnId());
        }
    }

    private Stream<String> getRelationsForRole(PlooiEnvelope target, RelationType role, String suffix) {
        return target.getPlooi().getDocumentrelaties() == null
                ? Stream.empty()
                : target.getPlooi()
                        .getDocumentrelaties()
                        .stream()
                        .filter(r -> role.getUri().equals(r.getRole()))
                        .map(Relatie::getRelation)
                        .map(r -> ToLegacy.stripPid(r.concat(suffix)));
    }

    private void populateVersion(SolrInputDocument solrDoc, PlooiEnvelope target, Optional<AggregatedVersion> aggregatedVersion) {
        if (aggregatedVersion.isEmpty()) {
            throw new IllegalArgumentException("No files for " + target.getPlooiIntern().getDcnId());
        }
        var latestVersion = aggregatedVersion.get().toVersie();
        addToSolrDocument(solrDoc, "modified", latestVersion.getMutatiedatumtijd().format(DateTimeFormatter.ISO_INSTANT));
        addToSolrDocument(solrDoc, "openbaarmakingsdatum", date2Solr(latestVersion.getOpenbaarmakingsdatum()));
        var published = latestVersion.getBestanden().stream().filter(Bestand::isGepubliceerd).toList();
        addToSolrDocument(solrDoc, "hashes", published.stream().map(Bestand::getHash).filter(Objects::nonNull).toList());
        addToSolrDocument(solrDoc, "mimetype", published.stream().map(Bestand::getMimeType).findAny().orElse(null));
        addToSolrDocument(solrDoc, "bestandsgrootte", published.stream().map(Bestand::getGrootte).filter(Objects::nonNull).mapToLong(Long::longValue).sum());
        published.forEach(f -> f.id(target.getDocumentMeta().getPid() + "/" + f.getLabel()));
        if (latestVersion.getZichtbaarheidsdatumtijd() != null) {
            addToSolrDocument(solrDoc, "zichtbaarheidsdatumtijd", latestVersion.getZichtbaarheidsdatumtijd().format(DateTimeFormatter.ISO_INSTANT));
        }
        target.getPlooi().versies(List.of(latestVersion.bestanden(published)));
    }

    private void populateOther(SolrInputDocument solrDoc, PlooiEnvelope target, String identifier, String plooijson, String metaxml, String documentenxml) {
        addToSolrDocument(solrDoc, "execution_id", target.getProcesId());
        // Looking at the portal code, these repos fields are not really used...
        addToSolrDocument(solrDoc, "repos_name", identifier + ".xml"); // Can we change this to plooi.xml?
        addToSolrDocument(solrDoc, "repos_url", "/" + identifier + "/1/xml/" + identifier + ".xml");
        addToSolrDocument(solrDoc, "repos_modified", solrDoc.getFieldValue("modified"));
        addToSolrDocument(solrDoc, "repos_contenttype", "application/xml"); // yes, always
        addToSolrDocument(solrDoc, "repos_length", 0);
        // repos_published is not used? remove from schema?
        addToSolrDocument(solrDoc, "metaxml", metaxml);
        addToSolrDocument(solrDoc, "documentenxml", documentenxml);
        addToSolrDocument(solrDoc, "plooijson", plooijson);
        addToSolrDocument(solrDoc, "publicatiestatus", "gepubliceerd");
        // addToSolrDocument(solrDoc, "npages", 0); Needed?
        var documentText = target.getDocumentText();
        if (StringUtils.isBlank(documentText)) {
            target.status().addDiagnose(DiagnosticCode.EMPTY_TEXT, "No document text to index");
        } else {
            addToSolrDocument(solrDoc, "text", target.getDocumentText());
        }
    }

    private void addResource(SolrInputDocument solrDoc, StringBuilder metadata, String name, IdentifiedResource resource) {
        if (resource != null) {
            addToSolrDocument(solrDoc, name, resource.getLabel());
            addToSolrDocument(solrDoc, name.concat("_id"), resource.getId());
            if (!StringUtils.isBlank(resource.getLabel()) && !"Onbekend".equals(resource.getLabel())) {
                metadata.append(" ").append(resource.getLabel());
            }
            if (!StringUtils.isBlank(resource.getBronwaarde())) {
                metadata.append(" ").append(resource.getBronwaarde());
            }
        }
    }

    /** Null-proof {@link SolrDocument#addField(String, Object) } */
    private void addToSolrDocument(SolrInputDocument solrDoc, String name, Object value) {
        if (value != null) {
            solrDoc.addField(name.toLowerCase(), value);
        }
    }

    private String date2Solr(LocalDate date) {
        return date == null ? null : date2Solr(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private String date2Solr(String dateStr) {
        return StringUtils.isBlank(dateStr) ? null : (dateStr + "T00:00:00.000Z");
    }

    private static final String LABEL_PATTERN = "\"([.\\w]+)\":";
    private static final Pattern FIX_VALUE = Pattern.compile(LABEL_PATTERN + "\"\\1=");
    private static final Pattern FIX_LIST = Pattern.compile(LABEL_PATTERN + "\"\\[([^\\]\"]+)?\\]\"");

    public static String jsonStr(SolrInputDocument solrDoc) {
        var fixed = new StringBuilder();
        var listMatcher = FIX_LIST.matcher(FIX_VALUE.matcher(solrDoc.jsonStr()).replaceAll("\"$1\":\""));
        while (listMatcher.find()) {
            listMatcher.appendReplacement(fixed, StringUtils.isBlank(listMatcher.group(2))
                    ? "\"$1\":[]"
                    : ("\"$1\":[\"" + String.join("\",\"", List.of(listMatcher.group(2).split(",\\s*"))) + "\"]"));
        }
        return listMatcher.appendTail(fixed).toString();
    }
}

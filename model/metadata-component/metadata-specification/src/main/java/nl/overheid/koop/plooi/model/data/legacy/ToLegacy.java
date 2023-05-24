package nl.overheid.koop.plooi.model.data.legacy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.util.AggregatedVersion;
import nl.overheid.koop.plooi.model.data.util.PlooiBindingException;
import nl.overheid.koop.plooi.model.data.util.XMLHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@SuppressWarnings("java:S2589")
public class ToLegacy {

    public static final String XMLNS_NS = "http://www.w3.org/2000/xmlns/";
    public static final String PLOOI_NS = "http://standaarden.overheid.nl/plooi/terms/";
    public static final String DC_NS = "http://purl.org/dc/terms/";
    public static final String OWMS_NS = "http://standaarden.overheid.nl/owms/terms/";
    public static final String XHTML_NS = "http://www.w3.org/1999/xhtml";

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    public String convertPlooi(Plooi plooi) throws PlooiBindingException {
        return XMLHelper.toString(convert(plooi), true);
    }

    public String convertPart(Plooi plooi, String element) throws PlooiBindingException {
        var part = convert(plooi).getElementsByTagName(element);
        if (part.getLength() == 0) {
            return null;
        } else {
            return XMLHelper.toString(addNamespaces((Element) part.item(0)), false);
        }
    }

    private Document convert(Plooi plooi) throws PlooiBindingException {
        try {
            var documentBuilder = this.documentBuilderFactory.newDocumentBuilder();
            var doc = documentBuilder.newDocument();
            doc.setXmlStandalone(true);
            var root = doc.appendChild(addNamespaces(doc.createElementNS(PLOOI_NS, "plooi:plooi")));
            var meta = addNode(doc, root, "plooi:meta", PLOOI_NS);
            var body = addNode(doc, root, "plooi:body", PLOOI_NS);
            convertTekst(doc, body, plooi, documentBuilder);
            var versieNr = convertDocumenten(doc, body, plooi);
            var versieSuffix = "_" + versieNr;
            convertKern(doc, meta, plooi, versieSuffix);
            convertMantel(doc, meta, plooi, versieSuffix);
            convertBronmeta(doc, addNode(doc, meta, "plooi:othermeta", PLOOI_NS), plooi);
            convertPlooiipm(doc, meta, plooi, versieNr);
            return doc;
        } catch (ParserConfigurationException e) {
            throw new PlooiBindingException(e);
        }
    }

    private Element addNamespaces(Element elm) {
        elm.setAttributeNS(XMLNS_NS, "xmlns:plooi", PLOOI_NS);
        elm.setAttributeNS(XMLNS_NS, "xmlns:dcterms", DC_NS);
        elm.setAttributeNS(XMLNS_NS, "xmlns:overheid", OWMS_NS);
        return elm;
    }

    private Element convertKern(Document doc, Node meta, Plooi plooi, String versieSuffix) {
        var owmskern = addNode(doc, meta, "plooi:owmskern", PLOOI_NS);
        addTextNode(doc, owmskern, "dcterms:identifier", DC_NS, stripPid(plooi.getDocument().getPid()) + versieSuffix, false);
        if (plooi.getDocument().getTitelcollectie() != null) {
            addTextNode(doc, owmskern, "dcterms:title", DC_NS, plooi.getDocument().getTitelcollectie().getOfficieleTitel(), false);
        }
        if (plooi.getDocument().getLanguage() != null) {
            addTextNode(doc, owmskern, "dcterms:language", DC_NS, plooi.getDocument().getLanguage().getLabel(), false);
        }
        if (plooi.getDocument().getClassificatiecollectie() != null
                && plooi.getDocument().getClassificatiecollectie().getDocumentsoorten() != null
                && !plooi.getDocument().getClassificatiecollectie().getDocumentsoorten().isEmpty()) {
            addResourceNode(doc, owmskern, "dcterms:type", DC_NS, plooi.getDocument().getClassificatiecollectie().getDocumentsoorten().get(0));
        }
        addResourceNode(doc, owmskern, "dcterms:creator", DC_NS, plooi.getDocument().getOpsteller());
        addResourceNode(doc, owmskern, "overheid:authority", OWMS_NS, plooi.getDocument().getVerantwoordelijke());
        return owmskern;
    }

    private Element convertMantel(Document doc, Node meta, Plooi plooi, String versieSuffix) {
        var mantel = addNode(doc, meta, "plooi:owmsmantel", PLOOI_NS);
        addResourceNode(doc, mantel, "dcterms:publisher", DC_NS, plooi.getDocument().getPublisher());
        if (StringUtils.isNotEmpty(plooi.getDocument().getWeblocatie())) {
            addTextNode(doc, mantel, "dcterms:source", DC_NS, plooi.getDocument().getWeblocatie(), true)
                    .setAttribute("resourceIdentifier", plooi.getDocument().getWeblocatie());
        }
        if (plooi.getDocument().getGeldigheid() != null) {
            var available = addNode(doc, mantel, "dcterms:available", DC_NS);
            var geldigheid = plooi.getDocument().getGeldigheid();
            addTextNode(doc, available, "start", null, geldigheid.getBegindatum() == null ? null : geldigheid.getBegindatum().toString(), false);
            addTextNode(doc, available, "end", null, geldigheid.getEinddatum() == null ? null : geldigheid.getEinddatum().toString(), false);
        }
        addTextNode(doc, mantel, "dcterms:issued", DC_NS, convertCreatiedatum(plooi.getDocument().getCreatiedatum()), false);
        addTextNodes(doc, mantel, "dcterms:subject", DC_NS, plooi.getDocument().getOnderwerpen());
        addTextNodes(doc, mantel, "dcterms:description", DC_NS, plooi.getDocument().getOmschrijvingen());
        if (plooi.getDocument().getTitelcollectie() != null) {
            addTextNodes(doc, mantel, "dcterms:alternative", DC_NS, plooi.getDocument().getTitelcollectie().getAlternatieveTitels());
        }
        if (plooi.getDocumentrelaties() != null) {
            plooi.getDocumentrelaties()
                    .stream()
                    .filter(r -> "https://identifier.overheid.nl/plooi/def/thes/documentrelatie/gerelateerd".equals(r.getRole()))
                    .forEach(r -> {
                        var mr = addTextNode(doc, mantel, "dcterms:relation", DC_NS, stripPid(r.getRelation()), true);
                        mr.setAttribute("resourceIdentifier", stripPid(r.getRelation()));
                    });
            addResourceNode(doc, mantel, "dcterms:isPartOf", DC_NS, plooi.getDocumentrelaties()
                    .stream()
                    .filter(r -> "https://identifier.overheid.nl/plooi/def/thes/documentrelatie/bundel".equals(r.getRole()))
                    .map(r -> new IdentifiedResource().id(stripPid(r.getRelation())).label(stripPid(r.getRelation()) + versieSuffix))
                    .findAny()
                    .orElse(null));
            addResourceNodes(doc, mantel, "dcterms:hasPart", DC_NS, plooi.getDocumentrelaties()
                    .stream()
                    .filter(r -> "https://identifier.overheid.nl/plooi/def/thes/documentrelatie/onderdeel".equals(r.getRole()))
                    .map(r -> new IdentifiedResource().id(stripPid(r.getRelation())).label(stripPid(r.getRelation()) + versieSuffix))
                    .toList());
        }
        return mantel;
    }

    private Element convertBronmeta(Document doc, Node meta, Plooi plooi) {
        var bronmeta = addNode(doc, meta, "plooi:bronmeta", PLOOI_NS);
        if (plooi.getPlooiIntern().getExtId() != null && !plooi.getPlooiIntern().getExtId().isEmpty()) {
            addTextNode(doc, bronmeta, "dcterms:identifier", DC_NS, plooi.getPlooiIntern().getExtId().get(0), false);
        }
        addTextNode(doc, bronmeta, "plooi:source-label", PLOOI_NS, plooi.getPlooiIntern().getSourceLabel(), false);
        addBronmetaNode(doc, bronmeta, "plooi:creator", plooi.getDocument().getOpsteller());
        if (plooi.getDocument().getClassificatiecollectie() != null
                && plooi.getDocument().getClassificatiecollectie().getDocumentsoorten() != null
                && !plooi.getDocument().getClassificatiecollectie().getDocumentsoorten().isEmpty()) {
            addBronmetaNode(doc, bronmeta, "plooi:type", plooi.getDocument().getClassificatiecollectie().getDocumentsoorten().get(0));
        }
        addBronmetaNode(doc, bronmeta, "plooi:publisher", plooi.getDocument().getPublisher());
        addBronmetaNode(doc, bronmeta, "plooi:authority", plooi.getDocument().getVerantwoordelijke());
        if (plooi.getDocument().getClassificatiecollectie() != null
                && plooi.getDocument().getClassificatiecollectie().getThemas() != null
                && !plooi.getDocument().getClassificatiecollectie().getThemas().isEmpty()) {
            plooi.getDocument()
                    .getClassificatiecollectie()
                    .getThemas()
                    .forEach(t -> addBronmetaNode(doc, bronmeta, "plooi:subject", t));
        }
        return bronmeta;
    }

    private void addBronmetaNode(Document doc, Node bronmeta, String name, IdentifiedResource resource) {
        if (resource != null && StringUtils.isNotEmpty(resource.getBronwaarde())) {
            addTextNode(doc, bronmeta, name, PLOOI_NS, resource.getBronwaarde(), false);
        }
    }

    private void convertPlooiipm(Document doc, Node meta, Plooi plooi, Integer versieNr) {
        var plooiipm = addNode(doc, meta, "plooi:plooiipm", PLOOI_NS);
        addTextNode(doc, plooiipm, "plooi:versie", PLOOI_NS, versieNr.toString(), false);
        addTextNode(doc, plooiipm, "dcterms:identifier", DC_NS, plooi.getPlooiIntern().getDcnId(), false);
        if (plooi.getDocument().getClassificatiecollectie() != null
                && plooi.getDocument().getClassificatiecollectie().getDocumentsoorten() != null
                && !plooi.getDocument().getClassificatiecollectie().getDocumentsoorten().isEmpty()) {
            addResourceNode(doc, plooiipm, "plooi:informatiecategorie", PLOOI_NS, plooi.getDocument().getClassificatiecollectie().getDocumentsoorten().get(0));
        }
        addResourceNode(doc, plooiipm, "plooi:verantwoordelijke", PLOOI_NS, plooi.getDocument().getVerantwoordelijke());
        if (plooi.getDocument().getClassificatiecollectie() != null) {
            addResourceNodes(doc, plooiipm, "plooi:topthema", PLOOI_NS, plooi.getDocument().getClassificatiecollectie().getThemas());
        }
        addTextNode(doc, plooiipm, "plooi:aanbieder", PLOOI_NS, plooi.getPlooiIntern().getAanbieder(), false);
        if (plooi.getDocument().getExtraMetadata() != null) {
            plooi.getDocument()
                    .getExtraMetadata()
                    .stream()
                    .map(emi -> Pair.of(emi.getPrefix(), emi.getVelden()))
                    .filter(emiPair -> Objects.nonNull(emiPair.getRight()))
                    .flatMap(emiPair -> emiPair.getRight().stream().map(emviPair -> Pair.of(emiPair.getLeft(), emviPair)))
                    .flatMap(emviPair -> emviPair.getRight().getValues().stream().map(val -> Triple.of(emviPair.getLeft(), emviPair.getRight().getKey(), val)))
                    .forEach(triple -> addTextNode(doc, plooiipm, "plooi:extrametadata", PLOOI_NS, triple.getRight(), true)
                            .setAttribute("name", new StringBuilder()
                                    .append(StringUtils.isBlank(triple.getLeft()) ? "" : triple.getLeft().concat("."))
                                    .append(triple.getMiddle())
                                    .toString()));
        }
    }

    private void convertTekst(Document doc, Node body, Plooi plooi, DocumentBuilder documentBuilder) {
        try {
            if (plooi.getBody() != null && plooi.getBody().getTekst() != null) {
                var tekstStr = new StringBuilder()
                        .append("<plooi:tekst xmlns=\"http://www.w3.org/1999/xhtml\">")
                        .append(String.join(" ", plooi.getBody().getTekst()))
                        .append("</plooi:tekst>");
                body.appendChild(
                        doc.adoptNode(documentBuilder.parse(new ByteArrayInputStream(tekstStr.toString().getBytes(StandardCharsets.UTF_8))).getFirstChild()));
            }
        } catch (SAXException | IOException e) {
            throw new PlooiBindingException(e);
        }
    }

    private Integer convertDocumenten(Document doc, Node body, Plooi plooi) {
        Node documenten = null;
        var latestVersion = AggregatedVersion.aggregateLatestVersion(plooi.getVersies());
        var versionNr = Integer.valueOf(1);
        if (latestVersion.isPresent()) {
            var version = latestVersion.get().toVersie();
            documenten = addNode(doc, body, "plooi:documenten", PLOOI_NS);
            for (var bestand : version.getBestanden()) {
                versionNr = version.getNummer();
                var document = addNode(doc, documenten, "plooi:document", PLOOI_NS);
                document.setAttribute("published", bestand.isGepubliceerd().toString());
                addTextNode(doc, document, "plooi:titel", PLOOI_NS, bestand.getTitel(), false);
                addTextNode(doc, document, "plooi:manifestatie-label", PLOOI_NS, bestand.getLabel(), false);
                addTextNode(doc, document, "plooi:url", PLOOI_NS, bestand.getUrl(), false);
                addTextNode(doc, document, "plooi:bestandsnaam", PLOOI_NS, bestand.getBestandsnaam(), false);
                if (bestand.getMutatiedatumtijd() != null) {
                    addTextNode(doc, document, "plooi:timestamp", PLOOI_NS, bestand.getMutatiedatumtijd().toString(), false);
                }
                addTextNode(doc, document, "plooi:hash", PLOOI_NS, bestand.getHash(), false);
            }
        }
        if (plooi.getDocumentrelaties() != null) {
            for (var part : plooi.getDocumentrelaties()
                    .stream()
                    .filter(r -> "https://identifier.overheid.nl/plooi/def/thes/documentrelatie/onderdeel".equals(r.getRole()))
                    .toList()) {
                if (documenten == null) {
                    documenten = addNode(doc, body, "plooi:documenten", PLOOI_NS);
                }
                var document = addNode(doc, documenten, "plooi:document", PLOOI_NS);
                document.setAttribute("published", "true");
                addTextNode(doc, document, "plooi:titel", PLOOI_NS, part.getTitel(), false);
                addTextNode(doc, document, "plooi:manifestatie-label", PLOOI_NS, "pdf", false);
                addTextNode(doc, document, "plooi:ref", PLOOI_NS, stripPid(part.getRelation()) + "_" + versionNr, false);
            }
        }
        return versionNr;
    }

    private void addResourceNodes(Document doc, Node node, String name, String namespace, List<IdentifiedResource> resources) {
        if (resources != null) {
            resources.forEach(r -> addResourceNode(doc, node, name, namespace, r));
        }
    }

    private void addResourceNode(Document doc, Node node, String name, String namespace, IdentifiedResource resource) {
        if (resource != null) {
            var child = addTextNode(doc, node, name, namespace, resource.getLabel(), true);
            if (StringUtils.isNotEmpty(resource.getType())) {
                child.setAttribute("scheme", resource.getType());
            }
            if (StringUtils.isNotEmpty(resource.getId())) {
                child.setAttribute("resourceIdentifier", resource.getId());
            }
        }
    }

    private void addTextNodes(Document doc, Node node, String name, String namespace, Collection<String> texts) {
        if (texts != null) {
            texts.forEach(r -> addTextNode(doc, node, name, namespace, r, false));
        }
    }

    private Element addTextNode(Document doc, Node node, String name, String namespace, String text, boolean createWhenEmpty) {
        if (createWhenEmpty || !StringUtils.isEmpty(text)) {
            var child = addNode(doc, node, name, namespace);
            child.appendChild(doc.createTextNode(text));
            return child;
        } else {
            return null;
        }
    }

    private Element addNode(Document doc, Node node, String name, String namespace) {
        var child = doc.createElementNS(namespace, name);
        node.appendChild(child);
        return child;
    }

    private static final Pattern ID_SFFX_PTTRN = Pattern.compile(".*/");

    public static String stripPid(String pid) {
        return StringUtils.isBlank(pid) ? "" : ID_SFFX_PTTRN.matcher(pid).replaceAll("");
    }

    private static final Pattern DATETIME_PTTRN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*)");
    private static final Pattern DATE_PTTRN = Pattern.compile("(\\d{2,4}-\\d{1,2}-\\d{1,2}).*");
    private static final ZoneId NL_ZONE = ZoneId.of("Europe/Amsterdam");

    public static String convertCreatiedatum(String creatiedatum) {
        try {
            var datetimeMatcher = DATETIME_PTTRN.matcher(StringUtils.defaultIfBlank(creatiedatum, ""));
            if (datetimeMatcher.matches()) {
                return OffsetDateTime.parse(datetimeMatcher.group(1))
                        .atZoneSameInstant(NL_ZONE)
                        .toLocalDate()
                        .toString();
            } else {
                var dateMatcher = DATE_PTTRN.matcher(StringUtils.defaultIfBlank(creatiedatum, ""));
                return dateMatcher.matches() ? LocalDate.parse(dateMatcher.group(1)).toString() : null;
            }
        } catch (DateTimeParseException e) {
            // Too bad
            return null;
        }
    }
}

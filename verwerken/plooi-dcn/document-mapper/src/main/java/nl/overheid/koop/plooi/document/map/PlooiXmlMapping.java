package nl.overheid.koop.plooi.document.map;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;
import nl.overheid.koop.plooi.model.data.ExtraMetadata;
import nl.overheid.koop.plooi.model.data.ExtraMetadataVeld;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PlooiXmlMapping implements PlooiMapping {
    @Override
    public PlooiXmlMappingInstance getInstance(InputStream srcStrm) throws SAXException, IOException, ParserConfigurationException {
        return new PlooiXmlMappingInstance(srcStrm);
    }
}

class PlooiXmlMappingInstance implements PlooiMappingInstance {

    private static final XPathFactory XPATH_FACT = XPathFactory.newInstance();
    private static final DocumentBuilderFactory DOC_BLDR_FACT;
    static {
        DOC_BLDR_FACT = DocumentBuilderFactory.newInstance();
        DOC_BLDR_FACT.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        DOC_BLDR_FACT.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    }

    private final XPath xpath = XPATH_FACT.newXPath();
    private final Node documentNode;

    @SuppressWarnings("java:S2755")
    PlooiXmlMappingInstance(InputStream srcStrm) throws SAXException, IOException, ParserConfigurationException {
        this(DOC_BLDR_FACT.newDocumentBuilder().parse(srcStrm));
    }

    PlooiXmlMappingInstance(Node node) {
        this.documentNode = node;
    }

    @Override
    public List<ExtraMetadata> mapExtraMetadatas(ExtraMetadataMapping mapping, List<ExtraMetadata> addTo) {
        var extraMetadata = addTo == null ? new ArrayList<ExtraMetadata>() : addTo;
        addNodeListValues(
                this.documentNode,
                mapping.rootPath(),
                node -> buildExtraMetadata(mapping, node, extraMetadata),
                extraMetadata,
                null);
        return extraMetadata.isEmpty() ? null : extraMetadata;
    }

    @Override
    public String mapString(String path, Consumer<String> setter) {
        return doMapString(this.documentNode, path, setter);
    }

    @Override
    public List<String> mapStrings(String path, List<String> stringList, Consumer<List<String>> setter) {
        return PlooiMappingInstance.setWith(
                addNodeListValues(
                        this.documentNode,
                        path,
                        node -> StringUtils.trimToNull(node.getTextContent()),
                        stringList,
                        ArrayList::new),
                setter);
    }

    @Override
    public List<Pair<String, String>> mapTaggedStrings(String path) {
        return addNodeListValues(
                this.documentNode,
                path,
                node -> {
                    var txt = StringUtils.trimToNull(node.getTextContent());
                    return txt == null ? null : Pair.of(node.getNodeName(), txt);
                },
                new ArrayList<>(),
                null);
    }

    @Override
    public IdentifiedResource mapResource(ResourceMapping mapping, Consumer<IdentifiedResource> setter) {
        if (mapping == null) {
            return null;
        } else {
            return PlooiMappingInstance.setWith(
                    buildResource(mapping, mapping.rootPath() == null ? this.documentNode : getSingleNode(this.documentNode, mapping.rootPath())),
                    setter);
        }
    }

    @Override
    public List<IdentifiedResource> mapResources(ResourceMapping mapping, List<IdentifiedResource> addTo) {
        return mapping == null
                ? addTo
                : addNodeListValues(
                        this.documentNode,
                        mapping.rootPath() == null ? "." : mapping.rootPath(),
                        node -> buildResource(mapping, node),
                        addTo,
                        null);
    }

    @Override
    public List<Node> mapDomNode(String path, List<Node> addTo) {
        return addNodeListValues(
                this.documentNode,
                path,
                node -> node.cloneNode(true),
                addTo,
                null);
    }

    @Override
    public List<PlooiMappingInstance> childMapping(String path) {
        return addNodeListValues(
                this.documentNode,
                path,
                PlooiXmlMappingInstance::new,
                new ArrayList<>(),
                null);
    }

    private IdentifiedResource buildResource(ResourceMapping mapping, Node node) {
        if (mapping == null || node == null) {
            return null;
        } else {
            var resource = new IdentifiedResource()
                    .id(doMapString(node, mapping.uriPath(), null))
                    .type(doMapString(node, mapping.schemePath(), null))
                    .label(mapping.termPath() == null ? StringUtils.trimToNull(node.getTextContent()) : doMapString(node, mapping.termPath(), null));
            return resource.getId() == null && resource.getLabel() == null ? null : resource;
        }
    }

    private ExtraMetadata buildExtraMetadata(ExtraMetadataMapping mapping, Node node, List<ExtraMetadata> addTo) {
        if (mapping != null && node != null) {
            CollectionUtils.addIgnoreNull(
                    mapping.locateIn(addTo).getVelden(),
                    new ExtraMetadataVeld()
                            .key(mapping.key() == null ? doMapString(node, mapping.keyPath(), null) : mapping.key())
                            .values(List.of(doMapString(node, mapping.valuePath(), null))));
        }
        // addNodeListValues does not have to add anything, that's already done here
        return null;
    }

    private String doMapString(Node node, String path, Consumer<String> setter) {
        if (path == null || node == null) {
            return null;
        } else {
            var innerNode = getSingleNode(node, path);
            return innerNode == null ? null : PlooiMappingInstance.setWith(StringUtils.trimToNull(innerNode.getTextContent()), setter);
        }
    }

    private <T> List<T> addNodeListValues(Node fromNode, String path, Function<Node, T> nodeTransformer, List<T> addTo, Supplier<List<T>> addToSupplier) {
        if (path != null) {
            try {
                NodeList nodes = (NodeList) this.xpath.compile(path).evaluate(fromNode, XPathConstants.NODESET);
                for (int i = 0; i < nodes.getLength(); i++) {
                    addTo = addTo == null ? Objects.requireNonNull(addToSupplier, "An existing list or list supplier is required").get() : addTo;
                    CollectionUtils.addIgnoreNull(addTo, nodeTransformer.apply(nodes.item(i)));
                }
            } catch (XPathException e) {
                throw new PlooiMappingException(e);
            }
        }
        return addTo;
    }

    private Node getSingleNode(Node fromNode, String path) {
        try {
            return (Node) this.xpath.compile(path).evaluate(fromNode, XPathConstants.NODE);
        } catch (XPathException e) {
            throw new PlooiMappingException(e);
        }
    }
}

package nl.overheid.koop.plooi.model.data.util;

import java.io.IOException;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

public final class XMLHelper {

    private static final boolean FIX_LINESEP = !System.lineSeparator().equals("\n") && Boolean.getBoolean("nl.overheid.koop.plooi.document.PrettyPrint");
    private static final TransformerFactory TRANSFORMER_FACT = TransformerFactory.newInstance();
//  static {
//      TRANSFORMER_FACT.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    // Not supported by our factory: TRANSFORMER_FACT.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
//      TRANSFORMER_FACT.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
//  }

    private XMLHelper() {
    }

    public static String toString(Node xml, boolean withXmlDeclarion) {
        try (StringWriter writer = new StringWriter()) {
            Transformer transformer = TRANSFORMER_FACT.newTransformer();
            if (!withXmlDeclarion) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            } else {
                transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            }
            transformer.setOutputProperty(OutputKeys.INDENT, Boolean.getBoolean("nl.overheid.koop.plooi.model.data.PrettyPrint") ? "yes" : "no");
            transformer.transform(new DOMSource(xml), new StreamResult(writer));
            var xmlStr = writer.toString();
            return FIX_LINESEP ? xmlStr.replace("\r\n", "\n") : xmlStr;
        } catch (TransformerException | IOException e) {
            throw new PlooiBindingException(e);
        }
    }

    public static String trim(String toTrim) {
        return toTrim
                .replaceAll("(?m)\\s+$", "") // Trim end of line
                .replaceAll("(?m)^\\s*$", ""); // Remove empty lines
    }
}

package nl.overheid.koop.plooi.model.data.legacy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import nl.overheid.koop.plooi.model.data.util.PlooiBindingException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FromLegacy {

    private static final String TRANSFORMATION = "from-legacy.xsl";
    private static final Pattern EOL = Pattern.compile("\s+\n", Pattern.MULTILINE);
    private static final Pattern PID = Pattern.compile("(\"pid\" : \"https://open\\.overheid\\.nl/documenten/[^\"/]+)_1\"", Pattern.MULTILINE);

    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    public static void main(String[] args) throws Exception {
        System.out.println(new FromLegacy().convert(Files.newInputStream(new File(args[0]).toPath())));
    }

    public String convert(InputStream legacyStream) throws PlooiBindingException {
        try (var xsltStream = getClass().getResourceAsStream(TRANSFORMATION);
                var stringWriter = new StringWriter()) {
            Document doc = this.documentBuilderFactory.newDocumentBuilder().parse(legacyStream);
            doc.normalize();
            this.transformerFactory
                    .newTransformer(new StreamSource(xsltStream))
                    .transform(new DOMSource(doc), new StreamResult(stringWriter));
            return EOL.matcher(PID.matcher(stringWriter.toString()).replaceFirst("$1\"")).replaceAll("\n");
        } catch (ParserConfigurationException | SAXException | TransformerException | IOException e) {
            throw new PlooiBindingException(e);
        }
    }
}

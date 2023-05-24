package nl.overheid.koop.plooi.document.map;

import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;

public record TextMapping(String path, boolean parseEmbeddedHTML) {

    private static final W3CDom JSOUP2DOM = new W3CDom();

    public static TextMapping plain(String path) {
        return new TextMapping(path, false);
    }

    public static TextMapping embeddedHTML(String path) {
        return new TextMapping(path, true);
    }

    public static String getPath(TextMapping using) {
        return using == null ? null : using.path;
    }

    public static String getText(TextMapping using, String from) {
        if (from != null && using.parseEmbeddedHTML) {
            var parsedDoc = JSOUP2DOM.fromJsoup(Jsoup.parse(
                    ("<html><body>" + from + "</body><html>")
                            .replaceAll("<(p|br)[ >]", " $0")/* Prevent paragraph content being glued together */));
            parsedDoc.normalize();
            return parsedDoc.getDocumentElement().getTextContent().trim();
        } else {
            return from;
        }
    }

    public static <T extends List<String>> T getTexts(TextMapping using, T from) {
        if (using != null && from != null) {
            for (int i = 0; i < from.size(); i++) {
                from.set(i, getText(using, from.get(i)));
            }
        }
        return from;
    }
}

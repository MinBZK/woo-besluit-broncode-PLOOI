package nl.overheid.koop.plooi.model.data.util;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import nl.overheid.koop.plooi.model.data.Bestand;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public final class PlooiFileUtil {

    private PlooiFileUtil() {
    }

    public static Bestand populate(Bestand file) {
        file.setBestandsnaam(prepareFileName(deriveFilename(file.getBestandsnaam(), file.getUrl(), file.getId()), file.getLabel()));
        file.setLabel(prepareLabel(file.getBestandsnaam(), file.getLabel()));
        if (StringUtils.isBlank(file.getId())) {
            file.id(file.getBestandsnaam());
        }
        if (StringUtils.isBlank(file.getMimeType())) {
            try {
                file.mimeType(Files.probeContentType(Paths.get(file.getBestandsnaam())));
            } catch (IOException e) {
                // Too bad
            }
        } else {
            file.mimeType(file.getMimeType().split(";")[0]);
        }
        return file;
    }

    private static String deriveFilename(String fileName, String url, String id) {
        if (StringUtils.isBlank(fileName)) {
            String urlPath = StringUtils.isBlank(url) ? "" : URI.create(url).getPath();
            if (urlPath.endsWith("/")) {
                urlPath = urlPath.substring(0, urlPath.length() - 1);
            }
            if (StringUtils.isBlank(urlPath)) {
                fileName = StringUtils.defaultIfBlank(id, "UNKNOWN");
            } else {
                urlPath = URLDecoder.decode(urlPath, StandardCharsets.UTF_8);
                fileName = urlPath.indexOf('/') < 0 ? urlPath : urlPath.substring(urlPath.lastIndexOf('/') + 1);
            }
        }
        return fileName;
    }

    private static String prepareFileName(String fileName, String label) {
        var baseFileName = FilenameUtils.removeExtension(fileName).replaceAll("[^-_. 0-9a-zA-Z]", "_");
        if (baseFileName.length() > 160) {
            baseFileName = StringUtils.truncate(baseFileName, 128)
                    + "-...-"
                    + StringUtils.truncate(Hex.encodeHexString(DigestUtils.updateDigest(DigestUtils.getSha1Digest(), fileName).digest()), 7);
        }
        var extension = FilenameUtils.getExtension(fileName);
        if (StringUtils.isBlank(label) && StringUtils.isBlank(extension)) {
            return baseFileName;
        } else if (StringUtils.isBlank(label)) {
            return baseFileName + "." + extension;
        } else {
            return baseFileName + "." + (StringUtils.defaultIfBlank(extension, label));
        }
    }

    private static String prepareLabel(String fileName, String label) {
        if (StringUtils.isBlank(label)) {
            var extension = FilenameUtils.getExtension(fileName);
            return StringUtils.defaultIfBlank(extension, "unknown").toLowerCase();
        } else {
            return label;
        }
    }
}

package nl.overheid.koop.plooi.repository.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.AggregatedVersion;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import nl.overheid.koop.plooi.model.data.util.PlooiBindingException;
import nl.overheid.koop.plooi.model.data.util.PlooiFileUtil;
import nl.overheid.koop.plooi.repository.data.InterneLabels;
import nl.overheid.koop.plooi.repository.data.PlooiManifest;
import nl.overheid.koop.plooi.service.error.ResponseErrorHelper;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class AanleverenController implements AanleverenApi {

    private static final Pattern LEADING_SLASH_PTTRN = Pattern.compile("^/");

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ServletFileUpload fileUpload = new ServletFileUpload();
    private final HttpServletRequest httpRequest;
    private final StorageAccess storageAccess;

    public AanleverenController(@Nonnull HttpServletRequest req, StorageAccess acc) {
        this.httpRequest = req;
        this.storageAccess = acc;
    }

    @Override
    public ResponseEntity<String> getDocument(String sourceLabel, String extId) {
        var externalIds = LEADING_SLASH_PTTRN.matcher(extId).replaceFirst("").split("/");
        try {
            return ResponseEntity
                    .status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, PublicatieController.buildFileUrl(
                            DcnIdentifierUtil.generateDcnId(sourceLabel, LEADING_SLASH_PTTRN.matcher(extId).replaceFirst("").split("/")), null, null))
                    .build();
        } catch (NullPointerException e) {
            throw ResponseErrorHelper.inputError(null, "Empty extId in", List.of(externalIds).toString());
        }
    }

    @Override
    public ResponseEntity<Plooi> postVersion(String sourceLabel, String extId) {
        try {
            if (!ServletFileUpload.isMultipartContent(this.httpRequest)) {
                throw ResponseErrorHelper.inputError(this.httpRequest.getPathInfo(), "Expecting multipart request");
            }
            var externalIds = Stream.of(LEADING_SLASH_PTTRN.matcher(extId).replaceFirst("").split("/"))
                    .map(id -> URLDecoder.decode(id, UTF_8)) // Slashes in an extId have to be double-encoded
                    .toArray(String[]::new);
            var dcnId = DcnIdentifierUtil.generateDcnId(sourceLabel, externalIds);
            var currentPlooi = this.storageAccess.exists(dcnId, null, StorageAccess.MANIFEST_NAME) ? this.storageAccess.retrieveManifest(dcnId) : null;
            var partIter = this.fileUpload.getItemIterator(this.httpRequest);
            var newVersion = getVersionManifest(dcnId, partIter);
            var status = HttpStatus.OK;
            if (currentPlooi == null && (newVersion.getOorzaak() == Versie.OorzaakEnum.AANLEVERING || newVersion.getOorzaak() == null)) {
                currentPlooi = new PlooiManifest(dcnId, sourceLabel, externalIds);
                status = HttpStatus.CREATED;
                newVersion.oorzaak(Versie.OorzaakEnum.AANLEVERING);
            } else if (currentPlooi == null) {
                throw ResponseErrorHelper.unknownParameter(dcnId, "document", sourceLabel + extId);
            } else if (newVersion.getOorzaak() == null) {
                newVersion.oorzaak(Versie.OorzaakEnum.WIJZIGING);
            }
            switch (newVersion.getOorzaak()) {
                case INTREKKING, HERPUBLICATIE -> handleDeletion(dcnId, partIter, currentPlooi, newVersion);
                case AANLEVERING -> handleNew(dcnId, partIter, currentPlooi, newVersion);
                case WIJZIGING -> handleUpdate(dcnId, partIter, currentPlooi, newVersion);
                default -> throw ResponseErrorHelper.internalError(dcnId, "Unimplemented " + newVersion.getOorzaak());
            }
            this.storageAccess.storeManifest(currentPlooi, StorageAccess.MANIFEST_NAME);
            return ResponseEntity.status(status).body(currentPlooi.getPlooi());
        } catch (FileUploadException | IOException e) {
            throw ResponseErrorHelper.internalError(this.httpRequest.getPathInfo(), e);
        }
    }

    private Versie getVersionManifest(String dcnId, FileItemIterator partIter) {
        try {
            if (!partIter.hasNext()) {
                throw ResponseErrorHelper.inputError(dcnId, "The request contains no parts");
            } else {
                var versionPart = partIter.next();
                if (!InterneLabels.VERSIE.getValue().equals(versionPart.getFieldName())) {
                    throw ResponseErrorHelper.inputError(dcnId, "First part must contain version manifest");
                } else {
                    return PlooiBindingsHelper.unmarshalVersion(versionPart.openStream());
                }
            }
        } catch (PlooiBindingException e) {
            throw ResponseErrorHelper.inputError(dcnId, "Can't parse version manifest");
        } catch (FileUploadException | IOException e) {
            throw ResponseErrorHelper.internalError(dcnId, e);
        }
    }

    private void handleDeletion(String dcnId, FileItemIterator partIter, PlooiManifest currentPlooi, Versie newVersion)
            throws FileUploadException, IOException {
        this.logger.debug("Handling deletion for {} with {}", dcnId, newVersion);
        if (partIter.hasNext()) {
            throw ResponseErrorHelper.inputError(dcnId, newVersion.getOorzaak() + " action cannot post files");
        } else {
            this.storageAccess.deletion(currentPlooi, newVersion);
        }
    }

    private void handleNew(String dcnId, FileItemIterator partIter, PlooiManifest currentPlooi, Versie newVersion)
            throws IOException, FileUploadException {
        this.logger.debug("Handling new document for {} with {}", dcnId, newVersion);
        if (!partIter.hasNext()) {
            throw ResponseErrorHelper.inputError(dcnId, "At least one file is required");
        }
        var aggrPlooi = AggregatedVersion.aggregateLatestVersion(currentPlooi.getVersies());
        if (aggrPlooi.isEmpty()) {
            currentPlooi.getVersies().clear(); // Just to be sure...
            currentPlooi.getVersies().add(AggregatedVersion.copy(newVersion).oorzaak(Versie.OorzaakEnum.AANLEVERING));
        } else if (aggrPlooi.get().getVersie().getOorzaak() != Versie.OorzaakEnum.AANLEVERING) {
            throw ResponseErrorHelper.inputError(dcnId, "Initial version is already done");
        } else if (currentPlooi.getVersies().size() != 1) {
            throw ResponseErrorHelper.internalError(dcnId, "Illegal state: Multiple versions");
        } else if (this.storageAccess.exists(dcnId, null, StorageAccess.PLOOI_NAME)) {
            throw ResponseErrorHelper.inputError(dcnId, "Already processed meta data");
        }

        boolean versionProvidesFiles = newVersion.getBestanden() != null;
        if (versionProvidesFiles) {
            newVersion.getBestanden().forEach(f -> f.hash(null));
        }
        newVersion.nummer(Integer.valueOf(1));

        var firstVersion = currentPlooi.getVersies().get(0);
        while (partIter.hasNext()) {
            var part = partIter.next();
            this.logger.trace(" - Part {} with file {}", part.getFieldName(), part.getName());
            var file = versionProvidesFiles ? getFile(dcnId, newVersion, part) : createFile(part);
            boolean fileExists = false;
            if (firstVersion.getBestanden() != null) {
                for (int i = 0; i < firstVersion.getBestanden().size(); i++) {
                    if (firstVersion.getBestanden().get(i).getLabel().equals(file.getLabel())) {
                        this.storageAccess.deleteFile(dcnId, newVersion.getNummer(), firstVersion.getBestanden().get(i).getBestandsnaam());
                        firstVersion.getBestanden().set(i, file);
                        fileExists = true;
                        break;
                    }
                }
            }
            if (!fileExists) {
                firstVersion.addBestandenItem(file);
            }
            var stored = this.storageAccess.store(dcnId, newVersion.getNummer(), file.getBestandsnaam(), part.openStream());
            file.hash(stored.getHash()).grootte(stored.getGrootte());
        }
        firstVersion
                .nummer(Integer.valueOf(1))
                .blokkades(newVersion.getBlokkades())
                .zichtbaarheidsdatumtijd(newVersion.getZichtbaarheidsdatumtijd())
                .mutatiedatumtijd(OffsetDateTime.now())
                .openbaarmakingsdatum(LocalDate.now());
        checkResults(dcnId, newVersion, false);
    }

    private void handleUpdate(String dcnId, FileItemIterator partIter, PlooiManifest currentPlooi, Versie newVersion)
            throws IOException, FileUploadException {
        this.logger.debug("Handling document update for {} with {}", dcnId, newVersion);
        if (!partIter.hasNext()) {
            throw ResponseErrorHelper.inputError(dcnId, "At least one file is required");
        }
        var aggrPlooi = StorageAccess.getVersion(currentPlooi, StorageAccess.LATEST_VERSION);
        var currentLabels = new HashSet<String>(aggrPlooi.toVersie().getBestanden().stream().map(Bestand::getLabel).toList());
        newVersion
                .nummer(Integer.valueOf(aggrPlooi.getVersie().getNummer().intValue() + 1))
                .mutatiedatumtijd(OffsetDateTime.now())
                .wijzigingsdatum(LocalDate.now());
        boolean versionProvidesFiles = newVersion.getBestanden() != null;
        if (versionProvidesFiles) {
            newVersion.getBestanden().forEach(f -> f.hash(null));
        }
        currentPlooi.getVersies().add(newVersion);

        var allIdentical = true;
        var updatedLabels = new HashSet<String>();
        while (partIter.hasNext()) {
            var part = partIter.next();
            this.logger.trace(" - Part {} with file {}", part.getFieldName(), part.getName());
            var file = versionProvidesFiles ? getFile(dcnId, newVersion, part) : addToVersion(newVersion, createFile(part));
            if (!currentLabels.contains(file.getLabel())) {
                throw ResponseErrorHelper.inputError(dcnId, "Not existing label", file.getLabel());
            } else if (!updatedLabels.add(file.getLabel())) {
                throw ResponseErrorHelper.inputError(dcnId, "Duplicate label", file.getLabel());
            }
            var stored = this.storageAccess.store(dcnId, newVersion.getNummer(), file.getBestandsnaam(), part.openStream());
            file.hash(stored.getHash()).grootte(stored.getGrootte());
            var isIdentical = file.getHash().equals(aggrPlooi.getFile(part.getFieldName()).orElseThrow().getRight().getHash());
            allIdentical &= isIdentical;
            if (isIdentical) {
                newVersion.bestanden(new ArrayList<>(newVersion.getBestanden().stream().filter(f -> !f.getLabel().equals(part.getFieldName())).toList()));
                this.storageAccess.deleteFile(dcnId, newVersion.getNummer(), file.getBestandsnaam());
            }
        }
        checkResults(dcnId, newVersion, allIdentical);
    }

    private Bestand getFile(String dcnId, Versie newVersion, FileItemStream part) {
        Bestand file = newVersion.getBestanden()
                .stream()
                .filter(f -> f.getLabel().equals(part.getFieldName()))
                .findAny()
                .orElseThrow(() -> ResponseErrorHelper.inputError(dcnId, "Provided version does not have label for part", part.getFieldName()));
        file.bestandsnaam(StringUtils.defaultIfBlank(file.getBestandsnaam(), part.getName()));
        file.mimeType(StringUtils.defaultIfBlank(file.getMimeType(), part.getContentType()));
        if (file.getMutatiedatumtijd() == null) {
            file.mutatiedatumtijd(OffsetDateTime.now());
        }
        return PlooiFileUtil.populate(file);
    }

    private Bestand createFile(FileItemStream fromPart) {
        return PlooiFileUtil.populate(new Bestand()
                .label(fromPart.getFieldName())
                .bestandsnaam(fromPart.getName())
                .mimeType(fromPart.getContentType())
                .mutatiedatumtijd(OffsetDateTime.now()));
    }

    private Bestand addToVersion(Versie newVersion, Bestand file) {
        newVersion.addBestandenItem(file);
        return file;
    }

    private void checkResults(String dcnId, Versie newVersion, boolean allIdentical) {
        List<String> notProvided = newVersion.getBestanden() == null
                ? List.of()
                : newVersion.getBestanden().stream().filter(f -> StringUtils.isBlank(f.getHash())).map(Bestand::getLabel).toList();
        if (!notProvided.isEmpty()) {
            throw ResponseErrorHelper.inputError(dcnId, "Missing parts for label(s)", String.join(", ", notProvided));
        } else if (allIdentical) {
            throw ResponseErrorHelper.error(HttpStatus.NOT_MODIFIED, dcnId, "Content did not change");
        }
    }
}

package nl.overheid.koop.plooi.repository.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.legacy.ToLegacy;
import nl.overheid.koop.plooi.model.data.util.AggregatedVersion;
import nl.overheid.koop.plooi.repository.data.InterneLabels;
import nl.overheid.koop.plooi.repository.data.PlooiManifest;
import nl.overheid.koop.plooi.repository.idmapping.IdentifierMapping;
import nl.overheid.koop.plooi.repository.relational.RelationStore;
import nl.overheid.koop.plooi.repository.storage.Storage;
import nl.overheid.koop.plooi.service.error.ResponseErrorHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class DocumentenController implements DocumentenApi {

    private static final String ROOTPATH = "/documenten/";

    private final ToLegacy toLegacy = new ToLegacy();
    private final StorageAccess storageAccess;
    private final RelationStore relationStore;
    private final IdentifierMapping identifierMapping;

    public DocumentenController(StorageAccess acc, RelationStore relStore, IdentifierMapping idMapping) {
        this.storageAccess = acc;
        this.relationStore = relStore;
        this.identifierMapping = idMapping;
    }

    static String buildFileUrl(String id, String label) {
        return new StringBuilder(ROOTPATH).append(id).append("/").append(label).toString();
    }

    @Override
    public ResponseEntity<Object> getLabels(String id) {
        var labels = new LinkedHashMap<>();
        labels.putAll(getVerifiedManifestWithLatestVersion(id).getRight()
                .getLabels()
                .stream()
                .collect(Collectors.toMap(l -> l, l -> buildFileUrl(id, l))));
        labels.putAll(Stream.of(InterneLabels.values())
                .map(InterneLabels::toString)
                .collect(Collectors.toMap(l -> l, l -> buildFileUrl(id, l))));
        return ResponseEntity.ok(labels);
    }

    @SuppressWarnings("java:S2259")
    @Override
    public ResponseEntity<Resource> getFile(String id, String label) {
        // For RELATIES id is not required to be a DCN identifier
        var manifestAndVersion = InterneLabels.RELATIES.getValue().equals(label) ? null : getVerifiedManifestWithLatestVersion(id);
        if (label.startsWith("_")) {
            try {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, switch (InterneLabels.fromValue(label)) {
                            case OWMS -> "application/xml";
                            case TEXT -> Storage.MIME_TEXT;
                            default -> Storage.MIME_JSON;
                        })
                        .body(switch (InterneLabels.fromValue(label)) {
                            case MANIFEST -> new ByteArrayResource(
                                    PlooiBindingsHelper.marshalPlooi(id, manifestAndVersion.getLeft().getPlooi()));
                            case VERSIE -> new ByteArrayResource(
                                    PlooiBindingsHelper.marshalVersion(id, manifestAndVersion.getRight().toVersie()));
                            case PLOOI -> new ByteArrayResource(
                                    PlooiBindingsHelper.marshalPlooi(id, getPopulatedPlooi(id)));
                            case OWMS -> new ByteArrayResource(
                                    StorageAccess.bytes(this.toLegacy.convertPlooi(getPopulatedPlooi(id))));
                            case TEXT -> new InputStreamResource(
                                    this.storageAccess.retrieve(id, StorageAccess.TEXT_NAME));
                            case RELATIES -> new ByteArrayResource(
                                    PlooiBindingsHelper.marshalRelations(id, this.identifierMapping.mapPid(this.relationStore.related(id))));
                            default -> throw ResponseErrorHelper.unknownParameter(id, StorageAccess.PARAM_LABEL, label);
                        });
            } catch (IllegalArgumentException e) {
                throw ResponseErrorHelper.unknownParameter(id, StorageAccess.PARAM_LABEL, label);
            }
        } else {
            var fileWithVersion = StorageAccess.getFileWithVersion(manifestAndVersion.getLeft(), manifestAndVersion.getRight(), label);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE,
                            fileWithVersion.getRight().getMimeType())
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.inline().filename(fileWithVersion.getRight().getBestandsnaam()).build().toString())
                    .body(new InputStreamResource(this.storageAccess.retrieve(id, fileWithVersion.getLeft(), fileWithVersion.getRight().getBestandsnaam())));
        }
    }

    private Plooi getPopulatedPlooi(String id) {
        return this.identifierMapping.mapPid(this.relationStore.populate(this.storageAccess.retrievePlooi(id)));
    }

    private Pair<PlooiManifest, AggregatedVersion> getVerifiedManifestWithLatestVersion(String id) {
        try (var manifestStream = this.storageAccess.retrieve(id, StorageAccess.MANIFEST_NAME)) {
            var manifest = new PlooiManifest(PlooiBindingsHelper.unmarshalPlooi(id, manifestStream));
            var aggregatedVersion = StorageAccess.getVersion(manifest, StorageAccess.LATEST_VERSION);
            if (!StorageAccess.getBlocks(aggregatedVersion).isEmpty()) {
                throw ResponseErrorHelper.unknownParameter(null, "document", manifest.getPlooiIntern().getDcnId());
            } else {
                return Pair.of(manifest, aggregatedVersion);
            }
        } catch (IOException e) {
            throw ResponseErrorHelper.internalError(id, e);
        }
    }
}

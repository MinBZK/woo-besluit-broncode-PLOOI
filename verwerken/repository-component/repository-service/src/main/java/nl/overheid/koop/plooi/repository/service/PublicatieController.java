package nl.overheid.koop.plooi.repository.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.repository.data.InterneLabels;
import nl.overheid.koop.plooi.repository.data.PlooiManifest;
import nl.overheid.koop.plooi.repository.idmapping.IdentifierMapping;
import nl.overheid.koop.plooi.repository.relational.RelationStore;
import nl.overheid.koop.plooi.repository.storage.Storage;
import nl.overheid.koop.plooi.service.error.ResponseErrorHelper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class PublicatieController implements PublicatieApi {

    private static final String ROOTPATH = "/publicatie/";

    private final StorageAccess storageAccess;
    private final RelationStore relationStore;
    private final IdentifierMapping identifierMapping;

    public PublicatieController(StorageAccess acc, RelationStore relStore, IdentifierMapping idMapping) {
        this.storageAccess = acc;
        this.relationStore = relStore;
        this.identifierMapping = idMapping;
    }

    static String buildFileUrl(String id, String versieNummer, String label) {
        var url = new StringBuilder(ROOTPATH).append(id);
        if (versieNummer != null) {
            url.append("/").append(versieNummer);
        }
        if (label != null) {
            url.append("/").append(label);
        }
        return url.toString();
    }

    @Override
    public ResponseEntity<Object> getLabels(String id) {
        var manifest = this.storageAccess.retrieveManifest(id);
        var dcnId = manifest.getPlooiIntern().getDcnId();
        var labels = new LinkedHashMap<>();
        labels.putAll(StorageAccess.getVersion(manifest, StorageAccess.LATEST_VERSION)
                .getLabels()
                .stream()
                .collect(Collectors.toMap(l -> l, l -> buildFileUrl(dcnId, StorageAccess.LATEST_VERSION, l))));
        labels.putAll(Stream.of(InterneLabels.values())
                .map(InterneLabels::toString)
                .collect(Collectors.toMap(l -> l, l -> buildFileUrl(dcnId, null, l))));
        return ResponseEntity.ok(new TreeMap<>(Map.of("dcnId", dcnId, "labels", labels)));
    }

    @Override
    public ResponseEntity<Void> delete(String dcnId, String reden) {
        this.storageAccess.storeManifest(
                this.storageAccess.deletion(
                        this.storageAccess.retrieveManifest(dcnId),
                        new Versie().oorzaak(Versie.OorzaakEnum.INTREKKING).redenVerwijderingVervanging(reden)),
                StorageAccess.MANIFEST_NAME);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<Void> restore(String dcnId, Integer versieNummer, String reden) {
        this.storageAccess.storeManifest(
                this.storageAccess.deletion(
                        this.storageAccess.retrieveManifest(dcnId),
                        new Versie().oorzaak(Versie.OorzaakEnum.HERPUBLICATIE).redenVerwijderingVervanging(reden).nummer(versieNummer)),
                StorageAccess.MANIFEST_NAME);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<Resource> getMetaFile(String id, String label, List<String> types) {
        try {
            return InterneLabels.OWMS.getValue().equals(label)
                    ? ResponseEntity
                            .status(HttpStatus.SEE_OTHER)
                            .header(HttpHeaders.LOCATION, DocumentenController.buildFileUrl(id, label))
                            .build()
                    : ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, switch (InterneLabels.fromValue(label)) {
                                case TEXT -> Storage.MIME_TEXT;
                                default -> Storage.MIME_JSON;
                            })
                            .body(switch (InterneLabels.fromValue(label)) {
                                case MANIFEST -> new InputStreamResource(this.storageAccess.retrieve(id, StorageAccess.MANIFEST_NAME));
                                case PLOOI -> new InputStreamResource(this.storageAccess.retrieve(id, StorageAccess.PLOOI_NAME));
                                case VERSIE -> new ByteArrayResource(PlooiBindingsHelper.marshalVersion(id,
                                        StorageAccess.getVersion(this.storageAccess.retrieveManifest(id), StorageAccess.LATEST_VERSION).toVersie()));
                                case BLOKKADES -> new ByteArrayResource(PlooiBindingsHelper.marshalStrings(id,
                                        StorageAccess.getBlocks(
                                                StorageAccess.getVersion(this.storageAccess.retrieveManifest(id), StorageAccess.LATEST_VERSION))));
                                case RELATIES -> new ByteArrayResource(
                                        PlooiBindingsHelper.marshalRelations(id, this.identifierMapping.mapPid(this.relationStore.relations(id, types))));
                                case TEXT -> new InputStreamResource(this.storageAccess.retrieve(id, StorageAccess.TEXT_NAME));
                                default -> throw ResponseErrorHelper.unknownParameter(id, StorageAccess.PARAM_LABEL, label);
                            });
        } catch (IllegalArgumentException e) {
            throw ResponseErrorHelper.unknownParameter(id, StorageAccess.PARAM_LABEL, label);
        }
    }

    @Override
    public ResponseEntity<Void> postMetaFile(String id, String label, Resource body) {
        if (this.storageAccess.exists(id, null, StorageAccess.MANIFEST_NAME)) {
            try (var bodyStream = body.getInputStream()) {
                switch (InterneLabels.fromValue(label)) {
                    case PLOOI -> {
                        var plooi = PlooiBindingsHelper.unmarshalPlooi(id, bodyStream);
                        if (plooi.getDocument() == null) {
                            throw ResponseErrorHelper.inputError(id, "Document metadata is required");
                        } else if (plooi.getPlooiIntern() == null) {
                            throw ResponseErrorHelper.inputError(id, "PlooiIntern metadata is required");
                        } else if (!id.equals(plooi.getPlooiIntern().getDcnId())) {
                            throw ResponseErrorHelper.inputError(id, "PlooiIntern has other DCN identifier");
                        }
                        this.storageAccess.storeManifest(new PlooiManifest(plooi), StorageAccess.PLOOI_NAME);
                    }
                    case TEXT -> this.storageAccess.storeMetadata(id, null, StorageAccess.TEXT_NAME, bodyStream.readAllBytes());
                    case BLOKKADES -> this.storageAccess.storeBlocks(this.storageAccess.retrieveManifest(id),
                            PlooiBindingsHelper.unmarshalStrings(id, bodyStream));
                    default -> throw new IllegalArgumentException("Unexpected value: " + label);
                }
                return ResponseEntity.ok(null);
            } catch (IllegalArgumentException e) {
                throw ResponseErrorHelper.unknownParameter(id, StorageAccess.PARAM_LABEL, label);
            } catch (IOException e) {
                throw ResponseErrorHelper.internalError(id, "Cannot read body data");
            }
        } else {
            throw ResponseErrorHelper.unknownParameter("", "identifier", id);
        }
    }

    @Override
    public ResponseEntity<Resource> getVersionedFile(String id, String versieNummer, String label) {
        var manifest = this.storageAccess.retrieveManifest(id);
        var fileWithVersion = StorageAccess.getFileWithVersion(manifest, StorageAccess.getVersion(manifest, versieNummer), label);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, fileWithVersion.getRight().getMimeType())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(fileWithVersion.getRight().getBestandsnaam()).build().toString())
                .body(new InputStreamResource(this.storageAccess.retrieve(id, fileWithVersion.getLeft(), fileWithVersion.getRight().getBestandsnaam())));
    }

    @Override
    public ResponseEntity<Void> postRelations(String dcnId, String stage, List<Relatie> relaties) {
        this.relationStore.store(relaties, dcnId, stage);
        return ResponseEntity.ok(null);
    }
}

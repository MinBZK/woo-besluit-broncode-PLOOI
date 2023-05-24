package nl.overheid.koop.plooi.repository.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.AggregatedVersion;
import nl.overheid.koop.plooi.repository.data.PlooiManifest;
import nl.overheid.koop.plooi.repository.idmapping.IdentifierMapping;
import nl.overheid.koop.plooi.repository.storage.Storage;
import nl.overheid.koop.plooi.repository.storage.StorageException;
import nl.overheid.koop.plooi.service.error.HttpStatusException;
import nl.overheid.koop.plooi.service.error.ResponseErrorHelper;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class StorageAccess {

    static final String LATEST_VERSION = "-";

    static final String MANIFEST_NAME = Storage.manifestFilename(Storage.PLOOI_MANIFEST);
    static final String PLOOI_NAME = Storage.manifestFilename(Storage.PLOOI_FILE);
    static final String TEXT_NAME = Storage.textFilename(Storage.PLOOI_FILE);

    static final String PARAM_VERSION = "version number";
    static final String PARAM_LABEL = "label";
    static final String PARAM_FILE = "file";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Storage storage;
    private final IdentifierMapping identifierMapping;

    StorageAccess(Storage storage, IdentifierMapping idMapping) {
        this.storage = storage;
        this.identifierMapping = idMapping;
    }

    static byte[] bytes(String body) {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    private static HttpStatusException unknownParameter(PlooiManifest manifest, String parameter, String value) {
        return ResponseErrorHelper.unknownParameter(manifest.getPlooiIntern().getDcnId(), parameter, value);
    }

    static AggregatedVersion getVersion(PlooiManifest manifest, String versieNummer) {
        try {
            int findVersion = LATEST_VERSION.equals(versieNummer) ? -1 : Integer.parseInt(versieNummer);
            if (findVersion <= 0 && !LATEST_VERSION.equals(versieNummer)) {
                throw unknownParameter(manifest, PARAM_VERSION, versieNummer);
            } else {
                return AggregatedVersion.aggregateVersion(manifest.getVersies(), findVersion)
                        .orElseThrow(() -> unknownParameter(manifest, PARAM_VERSION, versieNummer));
            }
        } catch (NumberFormatException e) {
            throw ResponseErrorHelper.inputError(manifest.getPlooiIntern().getDcnId(), PARAM_VERSION, versieNummer);
        }
    }

    static List<String> getBlocks(AggregatedVersion aggregatedVersion) {
        var blocks = aggregatedVersion.getVersie().getBlokkades() == null ? new ArrayList<String>() : aggregatedVersion.getVersie().getBlokkades();
        if (aggregatedVersion.getVersie().getZichtbaarheidsdatumtijd() != null
                && aggregatedVersion.getVersie().getZichtbaarheidsdatumtijd().isAfter(OffsetDateTime.now())) {
            blocks.add("_zichtbaarheidsdatumtijd");
        }
        if (aggregatedVersion.getVersie().getOorzaak() == Versie.OorzaakEnum.INTREKKING) {
            blocks.add("_intrekking");
        }
        return blocks;
    }

    static Pair<Integer, Bestand> getFileWithVersion(PlooiManifest manifest, AggregatedVersion version, String label) {
        return version.getFile(label).orElseThrow(() -> unknownParameter(manifest, PARAM_LABEL, label));
    }

    InputStream retrieve(String id, String fileName) {
        return retrieve(id, null, fileName);
    }

    InputStream retrieve(String id, Integer version, String fileName) {
        try {
            var dcnId = this.identifierMapping.toDcn(id);
            this.logger.debug("Retrieving version {} of {} for {}", version, fileName, dcnId);
            if (this.storage.exists(dcnId, version, fileName)) {
                return this.storage.retrieve(dcnId, version, fileName);
            } else {
                throw ResponseErrorHelper.unknownParameter(dcnId, PARAM_FILE, fileName);
            }
        } catch (StorageException e) {
            throw ResponseErrorHelper.internalError(id, e);
        } catch (IllegalArgumentException e) {
            throw ResponseErrorHelper.inputError(id, e);
        }
    }

    boolean exists(String dcnId, Integer version, String fileName) {
        try {
            return this.storage.exists(dcnId, version, fileName);
        } catch (IllegalArgumentException e) {
            throw ResponseErrorHelper.inputError(dcnId, e);
        } catch (StorageException e) {
            throw ResponseErrorHelper.internalError(dcnId, e);
        }
    }

    PlooiManifest retrieveManifest(String id) {
        return new PlooiManifest(PlooiBindingsHelper.unmarshalPlooi(id, retrieve(id, MANIFEST_NAME)));
    }

    Plooi retrievePlooi(String id) {
        return PlooiBindingsHelper.unmarshalPlooi(id, retrieve(id, PLOOI_NAME));
    }

    Bestand store(String dcnId, Integer version, String fileName, InputStream contentStream) {
        this.logger.debug("Storing version {} of {} for {}", version, fileName, dcnId);
        var digest = DigestUtils.getSha1Digest();
        try (var digestStream = new DigestInputStream(contentStream, digest)) {
            var size = this.storage.store(dcnId, version, fileName, digestStream);
            return new Bestand().bestandsnaam(fileName).grootte(size).hash(Hex.encodeHexString(digest.digest()));
        } catch (IOException | StorageException e) {
            throw ResponseErrorHelper.internalError(dcnId, e);
        } catch (IllegalArgumentException e) {
            throw ResponseErrorHelper.inputError(dcnId, e);
        }
    }

    void storeMetadata(String dcnId, Integer version, String fileName, byte[] metadata) {
        try (var stream = new ByteArrayInputStream(metadata)) {
            store(dcnId, version, fileName, stream);
        } catch (IOException e) {
            throw new StorageException("Exception while creating ByteArrayInputStream", e);
        }
    }

    void storeManifest(PlooiManifest manifest, String fileName) {
        var dcnId = manifest.getPlooiIntern().getDcnId();
        manifest.getPlooi().setDocumentrelaties(null);
        storeMetadata(
                dcnId,
                null,
                fileName,
                PlooiBindingsHelper.marshalPlooi(manifest.getPlooiIntern().getDcnId(), this.identifierMapping.mapPid(manifest.getPlooi())));
    }

    void storeBlocks(PlooiManifest manifest, List<String> blocks) {
        var latestVersion = manifest.getVersies().get(manifest.getVersies().size() - 1);
        for (var newBlokkade : blocks) {
            if (newBlokkade.startsWith("-") && (latestVersion.getBlokkades() == null || !latestVersion.getBlokkades().remove(newBlokkade.substring(1)))) {
                throw ResponseErrorHelper.inputError(manifest.getPlooiIntern().getDcnId(), "not existing blokkade", newBlokkade.substring(1));
            } else if (newBlokkade.startsWith("-")) {
                this.logger.trace("Removed blokkade {} for {}", newBlokkade, manifest.getPlooiIntern().getDcnId());
            } else if (!newBlokkade.matches("\\p{Alpha}+")) {
                throw ResponseErrorHelper.inputError(manifest.getPlooiIntern().getDcnId(), "illegal blokkade", newBlokkade);
            } else if (latestVersion.getBlokkades() != null && latestVersion.getBlokkades().contains(newBlokkade)) {
                this.logger.trace("Ignoring duplicate blokkade {} for {}", newBlokkade, manifest.getPlooiIntern().getDcnId());
            } else {
                latestVersion.addBlokkadesItem(newBlokkade);
            }
        }
        storeManifest(manifest, StorageAccess.MANIFEST_NAME);
    }

    PlooiManifest deletion(PlooiManifest currentPlooi, Versie newVersion) {
        var aggrPlooi = StorageAccess.getVersion(currentPlooi,
                newVersion.getOorzaak() == Versie.OorzaakEnum.INTREKKING || newVersion.getNummer() == null
                        ? StorageAccess.LATEST_VERSION
                        : newVersion.getNummer().toString());
        var dcnId = currentPlooi.getPlooiIntern().getDcnId();
        switch (newVersion.getOorzaak()) {
            case INTREKKING -> {
                if (aggrPlooi.getVersie().getOorzaak() == Versie.OorzaakEnum.INTREKKING) {
                    throw ResponseErrorHelper.inputError(dcnId, "Document is already deleted");
                }
                newVersion.nummer(null).bestanden(null);
                deleteFile(dcnId, null, StorageAccess.TEXT_NAME);
                deleteFile(dcnId, null, StorageAccess.PLOOI_NAME);
            }
            case HERPUBLICATIE -> {
                if (newVersion.getNummer() == null && aggrPlooi.getVersie().getOorzaak() == Versie.OorzaakEnum.INTREKKING) {
                    newVersion.nummer(aggrPlooi.getVersie().getNummer());
                } else if (newVersion.getNummer() == null) {
                    throw ResponseErrorHelper.inputError(dcnId, "Document is not deleted");
                }
                newVersion.bestanden(aggrPlooi.toVersie().getBestanden());
            }
            default -> throw ResponseErrorHelper.internalError(dcnId, "Unexpected oorzaak", newVersion.getOorzaak().toString());
        }
        currentPlooi.getVersies().add(newVersion.mutatiedatumtijd(OffsetDateTime.now()).wijzigingsdatum(LocalDate.now()));
        return currentPlooi;
    }

    void deleteFile(String dcnId, Integer version, String fileName) {
        if (this.storage.exists(dcnId, version, fileName)) {
            try {
                this.logger.debug("Deleting version {} of {} for {}", version, fileName, dcnId);
                this.storage.delete(dcnId, version, fileName);
            } catch (StorageException e) {
                throw ResponseErrorHelper.internalError(dcnId, e);
            } catch (IllegalArgumentException e) {
                throw ResponseErrorHelper.inputError(dcnId, e);
            }
        }
    }
}

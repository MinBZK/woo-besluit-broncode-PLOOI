package nl.overheid.koop.plooi.repository.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Stores and retrieves (content from) files to/from file system storage. */
@Component
public class FilesystemStorage implements Storage {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Path storageRoot;
    private final boolean compress;

    public final Path getStorageRoot() {
        return this.storageRoot;
    }

    public FilesystemStorage(@Value("${repository.root:./repository}") String root, @Value("${repository.compress:false}") boolean compr) {
        super();
        this.storageRoot = Path.of(root);
        this.compress = compr;
    }

    @Override
    public Long store(String dcnId, Integer version, String fileName, InputStream contentStream) {
        var stored = doStore(createDirectories(createDirectories(documentReposVersionDir(dcnId, version)))
                .resolve(Objects.requireNonNullElse(fileName, "A filename is required")), contentStream);
        return stored.getRight();
    }

    private Path createDirectories(Path dir) {
        try {
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }
            return dir;
        } catch (IOException e) {
            var message = "Could not create directory " + dir;
            this.logger.error(message, e);
            throw new StorageException(message, e);
        }
    }

    @Override
    public InputStream retrieve(String dcnId, Integer version, String fileName) {
        try {
            return doRetrieve(documentReposVersionFile(dcnId, version, fileName));
        } catch (IOException e) {
            var message = String.format("Could not retrieve %s for %s: %s", fileName, dcnId, e.getMessage());
            this.logger.error(message, e);
            throw new StorageException(message, e);
        }
    }

    private Path documentReposVersionFile(String dcnId, Integer version, String fileName) {
        return documentReposVersionDir(dcnId, version).resolve(Objects.requireNonNullElse(fileName, "A filename is required"));
    }

    private Path documentReposVersionDir(String dcnId, Integer version) {
        return version == null
                ? documentReposDir(dcnId)
                : documentReposDir(dcnId).resolve(version.toString());
    }

    private Path documentReposDir(String dcnId) {
        if (StringUtils.isBlank(dcnId)) {
            throw new IllegalArgumentException("An document identifier is required)");
        } else {
            var source = DcnIdentifierUtil.extractSource(dcnId);
            var hash = DcnIdentifierUtil.extractHash(dcnId);
            if (hash == null || hash.length() < 32) {
                throw new IllegalArgumentException("Illegal DCN identifier " + dcnId);
            } else {
                return getStorageRoot()
                        .resolve(hash.substring(0, 2))
                        .resolve(hash.substring(0, 4))
                        .resolve(source + "-" + hash);
            }
        }
    }

    private InputStream doRetrieve(Path path) throws IOException {
        this.logger.trace("Reading from {}", path);
        return new BufferedInputStream(this.compress ? new GZIPInputStream(Files.newInputStream(path)) : Files.newInputStream(path));
    }

    private Pair<Path, Long> doStore(Path path, InputStream inStream) {
        try (var fileStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                var outStream = new BufferedOutputStream(this.compress ? new GZIPOutputStream(fileStream) : fileStream)) {
            var size = inStream.transferTo(outStream);
            this.logger.trace("Wrote {} bytes to {}", size, path);
            return Pair.of(path, Long.valueOf(size));
        } catch (IOException e) {
            var message = String.format("Could not store %s: %s", path.getFileName(), e.getMessage());
            this.logger.error(message, e);
            throw new StorageException(message, e);
        }
    }

    @Override
    public boolean exists(String dcnId, Integer version, String fileName) {
        return Files.exists(documentReposVersionFile(dcnId, version, fileName));
    }

    @Override
    public void delete(String dcnId, Integer version, String fileName) {
        Path filePath = documentReposVersionFile(dcnId, version, fileName);
        try {
            Files.delete(filePath);
            this.logger.trace("Deleted {}", filePath);
        } catch (IOException e) {
            this.logger.warn("Could not delete " + filePath, e);
        }
    }

    @Override
    public long importAll(ZipInputStream zipIn) {
        this.logger.trace("Importing");
        try {
            long count = 0;
            for (var entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
                count++;
                var idSep = entry.getName().indexOf('/');
                var fileSep = entry.getName().lastIndexOf('/');
                var dcnId = entry.getName().substring(0, idSep);
                var innerPath = idSep == fileSep ? "" : entry.getName().substring(idSep + 1, fileSep);
                var fileName = entry.getName().substring(fileSep + 1);
                var stored = doStore(createDirectories(documentReposDir(dcnId).resolve(innerPath)).resolve(fileName), zipIn);
                Files.setLastModifiedTime(stored.getLeft(), entry.getLastModifiedTime());
            }
            return count;
        } catch (IOException e) {
            throw new StorageException("Could not unzip", e);
        }
    }

    private static final int MAX_BYTE = 256;
    private final Random rnd = new Random();

    @Override
    @SuppressWarnings("java:S2095") // zipOut is closed by the caller
    public void exportAll(ZipOutputStream zipOut, int sample) {
        this.logger.debug("Exporting with sample size {}", sample);
        var total = new AtomicInteger();
        var docs = new AtomicInteger();
        IntStream.range(0, MAX_BYTE * MAX_BYTE).forEachOrdered(i -> {
            var dir = getStorageRoot().resolve(String.format("%02x/%04x", i / MAX_BYTE, i));
            if (Files.isDirectory(dir)) {
                try (var dirListing = Files.list(dir)) {
                    dirListing.forEach(dcnId -> doExport(zipOut, dcnId, sample == 1 || this.rnd.nextInt(sample) == 0, total, docs));
                } catch (IOException e) {
                    throw new StorageException("Error while listing " + dir, e);
                }
            }
        });
        this.logger.info(" - Exported {} files for {} documents", total, docs);
    }

    @Override
    public void exportSingle(ZipOutputStream zipOut, String dcnId) {
        this.logger.debug("Exporting id {}", dcnId);
        var total = new AtomicInteger();
        doExport(zipOut, documentReposDir(dcnId), true, total, new AtomicInteger());
        this.logger.trace(" - Exported {} files", total);
    }

    @SuppressWarnings("java:S2095") // zipOut is closed by the caller
    private void doExport(ZipOutputStream zipOut, Path dcnIdPath, boolean condition, AtomicInteger total, AtomicInteger docs) {
        if (condition) {
            this.logger.trace(" - Exporting {}", dcnIdPath);
            docs.incrementAndGet();
            try (var dirListing = Files.walk(dcnIdPath)) {
                dirListing.filter(Files::isRegularFile).forEach(p -> doExportEntry(zipOut, dcnIdPath.getFileName().toString(), p, total));
            } catch (IOException e) {
                throw new StorageException("Error while listing " + dcnIdPath, e);
            }
        } else {
            this.logger.trace(" - Skipping {}", dcnIdPath);
        }
    }

    private void doExportEntry(ZipOutputStream zipOut, String dcnId, Path filePath, AtomicInteger total) {
        try (var zipIn = doRetrieve(filePath)) {
            zipOut.putNextEntry(new ZipEntry(filePath.toString().substring(filePath.toString().indexOf(dcnId)))
                    .setLastModifiedTime(Files.getLastModifiedTime(filePath)));
            zipIn.transferTo(zipOut);
            zipOut.closeEntry();
            total.incrementAndGet();
        } catch (IOException | StorageException e) {
            // We might accept a single error, let's move on
            this.logger.warn("Error while reading " + filePath, e);
        }
    }

    @Override
    public int process(ArchiefProcessor processor) {
        try (var fileStream = Files.find(
                getStorageRoot(),
                4, // don't recurse into version directories
                (path, attr) -> attr.isRegularFile() && Storage.manifestFilename(Storage.PLOOI_MANIFEST).equals(path.getFileName().toString()))) {
            fileStream
                    .map(Path::getParent)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .forEach(processor::process);
        } catch (Exception e) {
            throw new StorageException("Error while scanning repository", e);
        }
        return processor.flush();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " at " + getStorageRoot().toAbsolutePath();
    }
}

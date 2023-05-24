package nl.overheid.koop.plooi.repository.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;
import nl.overheid.koop.plooi.dcn.process.service.VerwerkenClient;
import nl.overheid.koop.plooi.repository.storage.ArchiefProcessor;
import nl.overheid.koop.plooi.repository.storage.Storage;
import nl.overheid.koop.plooi.service.error.ResponseErrorHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ArchiefController implements ArchiefApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Storage storage;
    private final VerwerkenClient verwerkenClient;
    private final HttpServletResponse httpResponse;

    public ArchiefController(@Nonnull HttpServletResponse resp, Storage stg, VerwerkenClient verwClnt) {
        this.httpResponse = resp;
        this.storage = stg;
        this.verwerkenClient = verwClnt;
    }

    private static final String EXPORT_MIMETYPE = "application/zip";
    private static final String EXPORT_PREFIX = "dcn_export-";
    private static final String EXPORT_EXTENSION = ".zip";

    @Value("${repository.archive.process.chunkSize:200}")
    private int chunkSize;

    private ExecutorService exportExecutor = Executors.newFixedThreadPool(1);

    @Override
    public ResponseEntity<Resource> exportZip(String location, String dcnId, Integer sample) {
        if (location == null) {
            this.httpResponse.setContentType(EXPORT_MIMETYPE);
            try (var exportStream = new ZipOutputStream(this.httpResponse.getOutputStream())) {
                doExport(this.httpResponse, exportStream, dcnId, sample);
            } catch (IllegalArgumentException e) {
                this.httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            } catch (IOException | RuntimeException e) {
                this.logger.warn("Exception while exporting zip", e);
                this.httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } else {
            File fileLocation;
            try {
                fileLocation = new File(new URI(location));
            } catch (URISyntaxException e) {
                throw ResponseErrorHelper.inputError(location, e);
            }
            this.logger.info("Starting export to {}", fileLocation);
            this.exportExecutor.submit(() -> {
                try (var exportStream = new ZipOutputStream(new FileOutputStream(fileLocation))) {
                    doExport(null, exportStream, dcnId, sample);
                } catch (IOException | RuntimeException e) {
                    this.logger.error("Exception while exporting zip to " + location, e);
                }
            });
        }
        return null;
    }

    private void doExport(HttpServletResponse httpResponse, ZipOutputStream exportStream, String dcnId, int sample) {
        if (httpResponse != null) {
            httpResponse.addHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition
                    .attachment()
                    .filename(dcnId == null
                            ? (EXPORT_PREFIX + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy-HHmmss")) + EXPORT_EXTENSION)
                            : (EXPORT_PREFIX + dcnId + EXPORT_EXTENSION))
                    .build()
                    .toString());
        }
        if (dcnId == null) {
            this.storage.exportAll(exportStream, sample);
        } else {
            this.storage.exportSingle(exportStream, dcnId);
        }
    }

    private ExecutorService importExecutor = Executors.newFixedThreadPool(1);

    @Override
    public ResponseEntity<String> importFromLocation(String location) {
        this.logger.info("Starting import from {}", location);
        this.importExecutor.submit(() -> {
            try (var importStream = new URL(location).openConnection().getInputStream()) {
                doImport(importStream);
            } catch (IOException | RuntimeException e) {
                this.logger.warn("Exception while importing zip from " + location, e);
            }
        });
        return ResponseEntity.ok("Started import from " + location);
    }

    @Override
    public ResponseEntity<String> importZip(Resource request) {
        try (var importStream = request.getInputStream()) {
            return doImport(importStream);
        } catch (IOException | RuntimeException e) {
            this.logger.warn("Exception while importing posted zip", e);
            return ResponseEntity.internalServerError().body(ExceptionUtils.getStackTrace(e));
        }
    }

    private ResponseEntity<String> doImport(InputStream importStream) throws IOException {
        try (var zipStream = new ZipInputStream(importStream)) {
            long imported = this.storage.importAll(zipStream);
            var message = String.format("Imported %d files", imported);
            this.logger.info(message);
            return ResponseEntity.ok(message);
        }
    }

    private ExecutorService processExecutor = Executors.newFixedThreadPool(1);

    @Override
    public ResponseEntity<Void> process(String actie, String procesId) {
        this.logger.info("Executing {} action for process {}", actie, procesId);
        this.processExecutor.submit(() -> {
            try {
                var processed = this.storage
                        .process(new ArchiefProcessor(new VerwerkenClientWrapper(this.verwerkenClient, VerwerkingActies.valueOf(actie), procesId),
                                this.chunkSize));
                this.logger.info("Executed {} action on {} documents for process {}", actie, processed, procesId);
            } catch (RuntimeException e) {
                this.logger.warn("Exception while processing " + actie, e);
            }
        });
        return ResponseEntity.ok(null);
    }
}

package nl.overheid.koop.plooi.repository.storage;

import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/** Deals with storing and retrieving (content from) files to/from persistent storage. */
public interface Storage {

    String PLOOI_MANIFEST = "manifest";
    String PLOOI_FILE = "plooi";
    String MANIFEST_EXTENSION = ".json";
    String TEXT_EXTENSION = ".txt";
    String MIME_JSON = "application/json";
    String MIME_TEXT = "plain/text";

    /** Produces a manifest file name by concatenating a json extension. */
    static String manifestFilename(String manifest) {
        return manifest.concat(MANIFEST_EXTENSION);
    }

    /** Produces a text file name by concatenating a txt extension. */
    static String textFilename(String manifest) {
        return manifest.concat(TEXT_EXTENSION);
    }

    /**
     * Stores a file in the repository.
     *
     * @param  dcnId                    DCN id of document owning the file to store
     * @param  version                  Version number of the file to store
     * @param  fileName                 Name of the file to store
     * @param  contentStream            {@link InputStream} for the content of the file to store
     * @return                          The number of bytes stored
     * @throws StorageException         if an error occurs while storing the content
     * @throws IllegalArgumentException if the dcnId is incorrect
     */
    Long store(String dcnId, Integer version, String fileName, InputStream contentStream);

    /**
     * Retrieves content of a file.
     *
     * @param  dcnId                    DCN id of document owning the file to retrieve
     * @param  version                  Version number of the file to retrieve
     * @param  fileName                 Name of the file to retrieve
     * @return                          Content of the file as bytes
     * @throws StorageException         if an error occurs while retrieving the content
     * @throws IllegalArgumentException if the dcnId is incorrect
     */
    InputStream retrieve(String dcnId, Integer version, String fileName);

    /**
     * Check if a file exists in the repository.
     *
     * @param  dcnId                    DCN id of document owning the file to check
     * @param  version                  Optional version number of the file to check
     * @param  fileName                 Name of the Plooi file to check
     * @return                          True if the file exists, else false
     * @throws IllegalArgumentException if the dcnId is incorrect
     */
    boolean exists(String dcnId, Integer version, String fileName);

    /**
     * Deletes a version of a document from the repository.
     *
     * @param  dcnId                    DCN id of the file to delete from the repository
     * @param  version                  Optional version number of the file to delete from the repository
     * @param  fileName                 Name of the file to delete from the repository
     * @throws IllegalArgumentException if the dcnId is incorrect
     */
    void delete(String dcnId, Integer version, String fileName);

    /**
     * Import a zip file into the repository
     *
     * @param  zipIn            {@link ZipInputStream} to read the import from
     * @return                  Number of files imported
     * @throws StorageException if an error occurs while reading the zip file or storing the files
     */
    long importAll(ZipInputStream zipIn);

    /**
     * Export a zip file from the repository
     *
     * @param  zipOut           {@link ZipOutputStream} to write the export to
     * @param  sample           Rate for the documents to export; for sample = N, 1 out of N documents will be exported
     * @throws StorageException if an internal error occurs while navigating the repository. Errors while reading individual
     *                          files are tolerated and logged
     */
    void exportAll(ZipOutputStream zipOut, int sample);

    /**
     * Export a zip file with a single document from the repository
     *
     * @param  zipOut                   {@link ZipOutputStream} to write the export to
     * @param  dcnId                    Identifier of the document to export
     * @throws IllegalArgumentException if the dcnId is incorrect
     */
    void exportSingle(ZipOutputStream zipOut, String dcnId);

    /**
     * Scan the complete repository and processes the DCN identifier for each manifest found.
     *
     * @param  processor {@link ArchiefProcessor} which processes each identifier
     * @return           The number of identifiers processed
     */
    int process(ArchiefProcessor processor);
}

package nl.overheid.koop.plooi.aanleveren.metadata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

public class TestDataFactory {
    public static final String METADATA_ENDPOINT = "/overheid/openbaarmaken/api/v0/metadata/";
    public static final String DOCUMENT_ID = "d2d72caf-baed-4e8a-80aa-7cc5c9e5e7e7";
    public static final String METADATA = "json/metadata.json";
    public static final String METADATA_PID = "json/metadata-with-pid.json";
    public static final String METADATA_PID_WITHOUT_IDENTIFIERS = "json/metadata-with-pid-without-identifiers.json";
    public static final String METADATA_PID_OTHER_PUBLISHER = "json/metadata-with-pid-other-publisher.json";
    public static final String METADATA_OTHER_PID = "json/metadata-with-other-pid.json";
    public static final String METADATA_WITHOUT_IDENTIFIERS = "json/metadata-without-identifiers.json";
    public static final String METADATA_WITHOUT_PID = "json/metadata-without-pid.json";

    @SneakyThrows
    public static String getMetadataModel(final String path) {
        return new ObjectMapper(new YAMLFactory()).readTree(new ClassPathResource((path)).getFile()).toString();
    }

    @SneakyThrows
    public static String getBody(final String path) {
        return new ObjectMapper().readTree(new ClassPathResource(path).getFile()).toString();
    }
}

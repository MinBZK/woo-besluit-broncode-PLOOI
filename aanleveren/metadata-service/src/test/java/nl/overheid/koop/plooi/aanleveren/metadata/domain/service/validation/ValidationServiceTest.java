package nl.overheid.koop.plooi.aanleveren.metadata.domain.service.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.NotFoundException;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.PlooiJsonParseException;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.PlooiJsonValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationServiceTest {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ValidationService validationService = new ValidationService();

    @Test
    void isMetadataValid() {
        val content = readJsonFileContent(new File("src/test/resources/json/metadata.json"));
        val isValid = validationService.isMetadataValid(content.toString());

        assertTrue(isValid);
    }

    @Test
    void isMetadataValid_invalidJSON() {
        assertThrows(
                PlooiJsonParseException.class,
                () -> validationService.isMetadataValid("{\"key\": \"value\""));
    }

    @Test
    void isMetadataValid_invalidMetadata() {
        val metadata = readJsonFileContent(new File("src/test/resources/json/metadata-without-language.json"));

        assertThrows(
                PlooiJsonValidationException.class,
                () -> validationService.isMetadataValid(metadata.toString()));
    }

    @Test
    void isMetadataValid_invalidJavascript() {
        val metadata = readJsonFileContent(new File("src/test/resources/json/metadata-with-javascript.json"));

        assertThrows(PlooiJsonParseException.class, () -> validationService.isMetadataValid(metadata.toString()));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = "")
    void checkUUIDAndPID(final String currentMetadata) {
        assertThrows(NotFoundException.class, () -> validationService.checkMetadata(currentMetadata, "{\"test\":\"test\"}"));
    }

    @SneakyThrows
    private JsonNode readJsonFileContent(final File file) {
        return jsonMapper.readTree(file);
    }
}

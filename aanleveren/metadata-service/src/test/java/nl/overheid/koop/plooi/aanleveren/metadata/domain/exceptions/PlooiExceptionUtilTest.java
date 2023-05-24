package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

import com.networknt.schema.ValidationMessage;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.util.PlooiExceptionUtil;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.error.YAMLException;

import java.text.MessageFormat;
import java.util.ArrayList;

import static nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.ExceptionConstants.ERROR_MESSAGE_NOT_IN_ENUMERATION;
import static nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.util.PlooiExceptionUtil.contentToString;
import static nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.util.PlooiExceptionUtil.editValidationMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PlooiExceptionUtilTest {
    private final ValidationMessage message =
            new ValidationMessage.Builder()
                    .format(new MessageFormat("{0}"))
                    .path("$.document")
                    .customMessage("$.document.publish: is missing but it is required")
                    .build();

    @Test
    void generateSchemaPointer() {
        assertEquals("/document/publish", PlooiExceptionUtil.generateSchemaPointer(message).toString());
    }

    @Test
    void generateDataPointer() {
        assertEquals("/document", PlooiExceptionUtil.generateDataPointer(message).toString());
    }

    @Test
    void generateContentForPlooiJsonParseException() {
        val msg = """
                $.document.identifiers: is missing but it is required
                $.document.language: integer found, string expected
                $.document.documenthandelingen: is missing but it is required
                """;
        assertEquals(
                "document.identifiers: is missing but it is required  |  document.language: integer found, string expected  |  document.documenthandelingen: is missing but it is required",
                PlooiExceptionUtil.generateContentForPlooiJsonParseException(msg));
    }

    @Test
    void generateContentForYAMLException() {
        var msg = "Unexpected character ('\"' (code 34)): was expecting comma to separate Object entries\n" +
                " at [Source: (String)\"{";
        var exception = new YAMLException(msg);
        assertEquals("Unexpected character ('\"' (code 34)): was expecting comma to separate Object entries", PlooiExceptionUtil.generateContentForYAMLException(exception));
    }

    @Test
    void testEditValidationMessageAndConnectToString() {
        var content = new ArrayList<String>();
        var errorList = new ArrayList<String>();

        errorList.add("Error 1");
        errorList.add("Error 2" + ERROR_MESSAGE_NOT_IN_ENUMERATION + " dit moet niet in het resultaat staan");

        for (var error : errorList) {
            var newContent = editValidationMessage(error);
            if (!content.contains(newContent)) {
                content.add(newContent);
            }
        }

        var result = contentToString(content);
        assertEquals("Error 1\nError 2" + ERROR_MESSAGE_NOT_IN_ENUMERATION, result);
    }
}

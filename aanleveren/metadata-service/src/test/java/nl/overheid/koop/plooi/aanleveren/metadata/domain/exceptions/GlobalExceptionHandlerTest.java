package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

import com.networknt.schema.ValidationMessage;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {
    private static final String KOOP_URL = "https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen";
    private static final String KOOP_URL_INDEX = KOOP_URL + "/index.html#";
    private static final String MORE_INFO = "Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).";

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();


    @BeforeAll
    static void init() {
        MDC.put("ProcessIdentifier", UUID.randomUUID().toString());
    }

    @Test
    void handlePlooiJsonParseException() {
        val response = new GlobalExceptionHandler()
                .handlePlooiJsonParseException(new PlooiJsonParseException("Testbericht"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Parsefout", response.getBody().title());
        assertEquals(
                "Het aanleveren of bijwerken is niet gelukt. De aangeleverde gegevens kunnen niet ingelezen worden. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).",
                response.getBody().detail());
        assertEquals("Testbericht", response.getBody().messages().get(0));
    }

    @Test
    void handlePlooiJsonValidationException() {
        val validationMessage = new ValidationMessage.Builder()
                .type("required")
                .code("1028")
                .path("$.document")
                .arguments("publisher")
                .details(Map.of("De aangeleverde metadata voldoet niet aan het schema. Raadpleeg voor meer informatie de documentatie", ""))
                .format(new MessageFormat("{0}"))
                .customMessage("$.document.publisher: is missing but it is required")
                .build();

        val response = new GlobalExceptionHandler()
                .handlePlooiJsonValidationException(new PlooiJsonValidationException(Set.of(validationMessage)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ValidatieMetadatafout", response.getBody().title());
        assertEquals(
                "De aangeleverde metadata voldoen niet aan het schema. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).",
                response.getBody().detail());
        assertTrue(response.getBody().messages().get(0).content().contains("$.document.publisher: is missing but it is required"));
    }

    @Test
    void handleNotValidUUIDException() {
        val response = new GlobalExceptionHandler()
                .handleNotValidUUIDException();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validatiefout", response.getBody().title());
        assertEquals(
                "De meegegeven identifier is niet geldig. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).",
                response.getBody().detail());
    }

    @Test
    void handleForbiddenException() {
        val response = new GlobalExceptionHandler()
                .handleForbiddenException();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Autorisatiefout", response.getBody().title());
        assertEquals(
                "U bent niet geautoriseerd voor deze actie. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).",
                response.getBody().detail());
    }

    @Test
    void handleNotFoundException() {
        val response = new GlobalExceptionHandler()
                .handleNotFoundException();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Niet-gevonden-fout", response.getBody().title());
        assertEquals(
                "De gevraagde bron kon niet worden gevonden. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).",
                response.getBody().detail());
    }

    @Test
    void handleConflictException() {
        val response = new GlobalExceptionHandler()
                .handleConflictException();

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflictfout", response.getBody().title());
        assertEquals(
                "Deze wijziging bevat aanpassingen op velden die niet mogen worden aangepast. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).",
                response.getBody().detail());
    }

    @Test
    void handleHttpMediaTypeNotSupportedException() {
        val response = globalExceptionHandler.handleHttpMediaTypeNotSupportedException();

        assertEquals(
                KOOP_URL_INDEX + "mediatype-fout-docu",
                response.getBody().type());
        assertEquals("MediatypeMetadatafout", response.getBody().title());
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), response.getBody().status());
        assertEquals(
                "Het formaat van de aangeleverde gegevens wordt niet ondersteund. Gebruik een van de ondersteunde formaten. " + MORE_INFO,
                response.getBody().detail());
        assertEquals(KOOP_URL, response.getBody().instance());
        assertEquals("application/json", response.getBody().supportedMediaTypes().get(0));
        assertEquals("application/hal+json", response.getBody().supportedMediaTypes().get(1));
    }
}

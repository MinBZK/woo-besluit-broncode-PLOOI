package nl.overheid.koop.plooi.aanleveren.document.domain.exceptions;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
    void handleResourceTypeException() {
        val response = globalExceptionHandler.handleResourceTypeException();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(KOOP_URL_INDEX + "bestandstype-fout", response.getBody().type());
        assertEquals("Bestandstype-fout", response.getBody().title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status());
        assertEquals("Het bestand kon niet verwerkt worden. " + MORE_INFO, response.getBody().detail());
        assertEquals(KOOP_URL, response.getBody().instance());
        assertEquals("De inhoud van het bestand wijkt af van de extensie.", response.getBody().supportedMediaTypes().get(0));
    }

    @Test
    void handleResourceTooLargeException() {
        val response = globalExceptionHandler.handleResourceTooLargeException();

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertEquals(KOOP_URL_INDEX + "bestand-te-groot-fout", response.getBody().type());
        assertEquals("BestandTeGrootFout", response.getBody().title());
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE.value(), response.getBody().status());
        assertEquals("Het bestand is te groot om verwerkt te kunnen worden. " + MORE_INFO, response.getBody().detail());
        assertEquals(KOOP_URL, response.getBody().instance());
        assertNull(response.getBody().supportedMediaTypes());
    }

    @Test
    void handleNotFoundException() {
        val response =  globalExceptionHandler.handleNotFoundException();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(KOOP_URL_INDEX + "niet-gevonden-fout", response.getBody().type());
        assertEquals("Niet-gevonden-fout", response.getBody().title());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().status());
        assertEquals("De gevraagde bron kon niet worden gevonden. " + MORE_INFO, response.getBody().detail());
        assertEquals(KOOP_URL, response.getBody().instance());
        assertNull(response.getBody().supportedMediaTypes());
    }

    @Test
    void handleIdentifierException() {
        val response = globalExceptionHandler.handleIdentifierException();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(KOOP_URL_INDEX + "identifier-fout", response.getBody().type());
        assertEquals("Validatiefout", response.getBody().title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status());
        assertEquals("De meegegeven identifier is niet geldig. " + MORE_INFO, response.getBody().detail());
        assertEquals(KOOP_URL, response.getBody().instance());
        assertNull(response.getBody().supportedMediaTypes());
    }

    @Test
    void handleHttpMediaTypeNotSupportedException() {
        val response = globalExceptionHandler.handleHttpMediaTypeNotSupportedException();

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertEquals(KOOP_URL_INDEX + "mediatype-fout-docu", response.getBody().type());
        assertEquals("Mediatypefout", response.getBody().title());
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), response.getBody().status());
        assertEquals(
                "Het formaat van de aangeleverde gegevens wordt niet ondersteund. Gebruik een van de ondersteunde formaten. " + MORE_INFO,
                response.getBody().detail());
        assertEquals(KOOP_URL, response.getBody().instance());
        assertNotNull(response.getBody().supportedMediaTypes());
    }

    @Test
    void handleIllegalArgumentException(){
        val notValidUUID = new IllegalArgumentException("UUID is niet geldig");
        val response = globalExceptionHandler.handleIllegalArgumentException(notValidUUID);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(KOOP_URL_INDEX + "identifier-fout", response.getBody().type());
        assertEquals("Validatiefout", response.getBody().title());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status());
        assertEquals("De meegegeven identifier is niet geldig. " + MORE_INFO, response.getBody().detail());
        assertEquals(KOOP_URL, response.getBody().instance());
        assertNull(response.getBody().supportedMediaTypes());
    }


}

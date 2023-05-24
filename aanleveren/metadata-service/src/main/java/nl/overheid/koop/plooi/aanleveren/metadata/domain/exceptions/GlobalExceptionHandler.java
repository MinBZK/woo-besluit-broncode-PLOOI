package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.metadata.web.response.AanleverenResponse;
import nl.overheid.koop.plooi.aanleveren.metadata.web.response.AanleverenResponseValidatiebericht;
import nl.overheid.koop.plooi.aanleveren.metadata.web.response.ValidatieBericht;
import nl.overheid.koop.plooi.aanleveren.models.MeasuringPoint;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.marker.Markers.appendRaw;
import static nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.ExceptionConstants.*;
import static nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.util.PlooiExceptionUtil.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final String INSTANCE = "https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen";
    private static final String CONSULT_DOC = "Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).";

    @ExceptionHandler(PlooiJsonParseException.class)
    public ResponseEntity<AanleverenResponse> handlePlooiJsonParseException(final PlooiJsonParseException ex) {
        logMeasuringPoint(BAD_REQUEST);
        return ResponseEntity.status(BAD_REQUEST).body(
                new AanleverenResponse(
                        INSTANCE + "/index.html#parse-fout",
                        "Parsefout",
                        400,
                        "Het aanleveren of bijwerken is niet gelukt. De aangeleverde gegevens kunnen niet ingelezen worden. " + CONSULT_DOC,
                        INSTANCE,
                        List.of(generateContentForYAMLException(ex))));
    }

    @ExceptionHandler(PlooiJsonValidationException.class)
    public ResponseEntity<AanleverenResponseValidatiebericht> handlePlooiJsonValidationException(final PlooiJsonValidationException ex) {
        logMeasuringPoint(BAD_REQUEST);
        return ResponseEntity.badRequest().body(
                new AanleverenResponseValidatiebericht(
                        INSTANCE + "/index.html#validatie-basis-fout-meta",
                        "ValidatieMetadatafout",
                        400,
                        "De aangeleverde metadata voldoen niet aan het schema. " + CONSULT_DOC,
                        INSTANCE,
                        List.of(getMessagesValidationException(ex))));
    }

    @ExceptionHandler({NotValidUUIDException.class, IdentifierException.class})
    public ResponseEntity<AanleverenResponse> handleNotValidUUIDException() {
        logMeasuringPoint(BAD_REQUEST);
        return ResponseEntity.status(BAD_REQUEST).body(
                new AanleverenResponse(
                        INSTANCE + "/index.html#identifier-fout",
                        "Validatiefout",
                        400,
                        "De meegegeven identifier is niet geldig. " + CONSULT_DOC,
                        INSTANCE,
                        null));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<AanleverenResponse> handleForbiddenException() {
        logMeasuringPoint(FORBIDDEN);
        return ResponseEntity.status(FORBIDDEN).body(
                new AanleverenResponse(
                        INSTANCE + "/index.html#autorisatie-fout",
                        "Autorisatiefout",
                        403,
                        "U bent niet geautoriseerd voor deze actie. " + CONSULT_DOC,
                        INSTANCE,
                        List.of("Aan de identificerende gegevens in dit verzoek zijn geen privileges tot deze handeling verbonden. Geef andere identificerende gegevens mee.")));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<AanleverenResponse> handleNotFoundException() {
        logMeasuringPoint(NOT_FOUND);
        return ResponseEntity.status(NOT_FOUND).body(
                new AanleverenResponse(
                        INSTANCE + "/index.html#niet-gevonden-fout",
                        "Niet-gevonden-fout",
                        404,
                        "De gevraagde bron kon niet worden gevonden. " + CONSULT_DOC,
                        INSTANCE,
                        null));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<AanleverenResponse> handleConflictException() {
        logMeasuringPoint(CONFLICT);
        return ResponseEntity.status(CONFLICT).body(
                new AanleverenResponse(
                        INSTANCE + "/index.html#conflict-fout",
                        "Conflictfout",
                        409,
                        "Deze wijziging bevat aanpassingen op velden die niet mogen worden aangepast. " + CONSULT_DOC,
                        INSTANCE,
                        List.of("De wijziging conflicteert met de eerder aangeleverde gegevens. Controleer de Publisher en de PID, deze mogen niet worden gewijzigd.")));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<AanleverenResponse> handleHttpMediaTypeNotSupportedException(){
        logMeasuringPoint(UNSUPPORTED_MEDIA_TYPE);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                new AanleverenResponse(
                        INSTANCE + "/index.html#mediatype-fout-docu",
                        "MediatypeMetadatafout",
                        415,
                        "Het formaat van de aangeleverde gegevens wordt niet ondersteund. Gebruik een van de ondersteunde formaten. " + CONSULT_DOC,
                        INSTANCE,
                        null,
                        List.of("application/json", "application/hal+json")
                ));
    }

    private ValidatieBericht getMessagesValidationException(PlooiJsonValidationException e) {
        val schemaPointers = new ArrayList<String>();
        val dataPointers = new ArrayList<String>();
        val content = new ArrayList<String>();

        val validationMessages = e.getValidationMessages();
        for (val validationMessage : validationMessages) {
            val newSchemaPointerItem = generateSchemaPointer(validationMessage).toString();
            if (!schemaPointers.contains(newSchemaPointerItem)) {
                schemaPointers.add(newSchemaPointerItem);
            }

            val newDataPointerItem = generateDataPointer(validationMessage).toString();
            if (!dataPointers.contains(newDataPointerItem)) {
                dataPointers.add(newDataPointerItem);
            }

            val newContent = editValidationMessage(validationMessage.getMessage());
            if (!content.contains(newContent)) {
                content.add(newContent);
            }
        }

        return new ValidatieBericht(schemaPointers, dataPointers, contentToString(content));
    }

    private void logMeasuringPoint(HttpStatus httpStatus) {
        log.info(appendRaw(MEASURINGPOINT, MeasuringPoint.builder()
                .description(LOG_MESSAGE)
                .processIdentifier(MDC.get(PROCESSIDENTIFIER))
                .statuscode(httpStatus.value())
                .build().toString()), MEASURINGPOINT_ADDED);
    }
}

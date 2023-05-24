package nl.overheid.koop.plooi.aanleveren.document.domain.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.overheid.koop.plooi.aanleveren.document.web.response.AanleverenResponseError;
import nl.overheid.koop.plooi.aanleveren.models.MeasuringPoint;
import nl.overheid.koop.plooi.repository.client.ClientException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

import static net.logstash.logback.marker.Markers.appendRaw;


@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String INSTANCE = "https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen";
    private static final String CONSULT_DOC = "Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).";
    private static final String MEASURINGPOINT = "measuringpoint";
    private static final String MEASURINGPOINT_ADDED = "MeasuringPoint marker added";

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<AanleverenResponseError> handleClientException(ClientException e) {
        log.info(e.getLocalizedMessage());
        if(e.getStatusCode() == 400 || e.getStatusCode() == 404){
            return handleNotFoundException();
        } else if (e.getStatusCode() >= 500) {
            return handleResourceTypeException();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceTypeException.class)
    public ResponseEntity<AanleverenResponseError> handleResourceTypeException(){
        logMeasuringPoint(HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest().body(
                new AanleverenResponseError(
                        INSTANCE + "/index.html#bestandstype-fout",
                        "Bestandstype-fout",
                        400,
                        "Het bestand kon niet verwerkt worden. " + CONSULT_DOC,
                        INSTANCE,
                        List.of("De inhoud van het bestand wijkt af van de extensie.")
                ));
    }

    @ExceptionHandler(ResourceTooLargeException.class)
    public ResponseEntity<AanleverenResponseError> handleResourceTooLargeException() {
        logMeasuringPoint(HttpStatus.PAYLOAD_TOO_LARGE);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                new AanleverenResponseError(
                        INSTANCE + "/index.html#bestand-te-groot-fout",
                        "BestandTeGrootFout",
                        413,
                        "Het bestand is te groot om verwerkt te kunnen worden. " +  CONSULT_DOC,
                        INSTANCE
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<AanleverenResponseError> handleNotFoundException() {
        logMeasuringPoint(HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(
                new AanleverenResponseError(
                            INSTANCE + "/index.html#niet-gevonden-fout",
                            "Niet-gevonden-fout",
                            404,
                            "De gevraagde bron kon niet worden gevonden. " + CONSULT_DOC,
                            INSTANCE
                ));
    }

    @ExceptionHandler(IdentifierException.class)
    public ResponseEntity<AanleverenResponseError> handleIdentifierException() {
        logMeasuringPoint(HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(
                new AanleverenResponseError(
                        INSTANCE + "/index.html#identifier-fout",
                        "Validatiefout",
                        400,
                        "De meegegeven identifier is niet geldig. " + CONSULT_DOC,
                        INSTANCE
                ));
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<AanleverenResponseError> handleHttpMediaTypeNotSupportedException(){
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
                new AanleverenResponseError(
                        INSTANCE + "/index.html#mediatype-fout-docu",
                        "Mediatypefout",
                        415,
                        "Het formaat van de aangeleverde gegevens wordt niet ondersteund. Gebruik een van de ondersteunde formaten. " + CONSULT_DOC,
                        INSTANCE,
                        List.of("application/pdf", "application/zip")
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AanleverenResponseError> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException){
        return ResponseEntity.badRequest().body(
                new AanleverenResponseError(
                        INSTANCE + "/index.html#identifier-fout",
                        "Validatiefout",
                        400,
                        "De meegegeven identifier is niet geldig. " + CONSULT_DOC,
                        INSTANCE
                ));
    }

    private void logMeasuringPoint(HttpStatus httpStatus) {
        log.info(appendRaw(MEASURINGPOINT, MeasuringPoint.builder()
                .description("Validatie Document Afgerond")
                .processIdentifier(MDC.get(ExceptionConstants.PROCESS_IDENTIFIER))
                .pid(MDC.get(ExceptionConstants.PID))
                .statuscode(httpStatus.value())
                .build().toString()), MEASURINGPOINT_ADDED);
    }

}

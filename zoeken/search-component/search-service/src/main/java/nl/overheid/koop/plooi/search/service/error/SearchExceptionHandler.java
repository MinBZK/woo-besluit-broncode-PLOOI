package nl.overheid.koop.plooi.search.service.error;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.net.UnknownHostException;
import java.time.format.DateTimeParseException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.solr.client.solrj.impl.BaseHttpSolrClient.RemoteSolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SearchExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<String> handelHttpStatusException(HttpServletRequest request, HttpStatusException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }

    @ExceptionHandler(RemoteSolrException.class)
    public ResponseEntity<String> handleRemoteSolrException(HttpServletRequest request, RemoteSolrException ex) {
        var error = "Unexpected RemoteSolrException : " + ExceptionUtils.getRootCauseMessage(ex);
        this.logger.error(error);
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(SolrQueryException.class)
    public ResponseEntity<String> handleSolrQueryException(HttpServletRequest request, SolrQueryException ex) {
        var error = "Unexpected SolrQueryException : " + ExceptionUtils.getRootCauseMessage(ex);

        if (ex.getCause() instanceof UnknownHostException) {
            error = "Cannot communicate with internal service.";
        }
        this.logger.error(error);
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        String rootCauseException = ExceptionUtils.getRootCauseMessage(ex);
        String error = "Unexpected HttpMessageNotReadableException : " + rootCauseException;
        this.logger.error(error); // this will be logged on OpenShift, visibility of code

        Throwable throwable = ex.getCause();

        if (throwable instanceof InvalidFormatException) {
            if (throwable.getCause() instanceof DateTimeParseException) {
                error = ((DateTimeParseException) throwable.getCause()).getLocalizedMessage();
            } else {
                error = ((InvalidFormatException) throwable).getOriginalMessage();
            }
        }

        if (throwable instanceof JsonParseException) {
            error = ((JsonParseException) throwable).getOriginalMessage();
        }

        if (throwable instanceof UnrecognizedPropertyException) {
            // visibility on UI is different, code related exception is hidden and missing property provided
            String unknownProperty = getUnknownProperty(rootCauseException);
            error = unknownProperty != null ? String.format("Unknown property in request %s", unknownProperty) : null;
        }

        return ResponseEntity.badRequest().body(error);
    }

    private static String getUnknownProperty(String unrecognizedPropertyException) {
        String splitString = "UnrecognizedPropertyException: Unrecognized field";
        String unknownProperty = null;
        if (unrecognizedPropertyException != null && unrecognizedPropertyException.contains(splitString)) {
            String[] parts = unrecognizedPropertyException.split(splitString);

            if (parts.length > 1 && parts[1].contains(" (class ")) {
                unknownProperty = parts[1].split(" \\(class ")[0].trim();
            }
        }

        return unknownProperty;
    }

}

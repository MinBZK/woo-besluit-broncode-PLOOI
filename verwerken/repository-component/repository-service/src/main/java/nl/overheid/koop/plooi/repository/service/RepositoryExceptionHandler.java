package nl.overheid.koop.plooi.repository.service;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RepositoryExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Deals with general database exception thrown by Spring Data JPA
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessResourceFailureException(HttpServletRequest request, DataAccessException ex) {
        var error = "Unexpected DataAccessException : " + ExceptionUtils.getRootCauseMessage(ex);
        this.logger.error(error);
        return ResponseEntity.internalServerError().body(error);
    }
}

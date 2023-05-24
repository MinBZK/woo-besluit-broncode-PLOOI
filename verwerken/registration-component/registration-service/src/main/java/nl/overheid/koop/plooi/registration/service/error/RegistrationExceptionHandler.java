package nl.overheid.koop.plooi.registration.service.error;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RegistrationExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ErrorResponse> handleErrorResponseException(HttpServletRequest request, ErrorResponseException ex) {
        return build(ex.getErrorResponse());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrityConstraintViolationException(HttpServletRequest request, SQLIntegrityConstraintViolationException ex) {
        String error = "Unable to submit post : " + ex.getMessage() + " for request " + request.getRequestURL();
        this.logger.error(error);
        return build(new ErrorResponse(HttpStatus.BAD_REQUEST, error));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(HttpServletRequest request, NoSuchElementException ex) {
        String error = "the row for the element is not exist" + request.getRequestURL();
        this.logger.error(error);
        return build(new ErrorResponse(HttpStatus.NOT_FOUND, error));
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessResourceFailureException(HttpServletRequest request, DataAccessResourceFailureException ex) {
        String error = "Unable to reach database by request : " + request.getRequestURL();
        this.logger.error(error);
        return build(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, error));
    }

    @ExceptionHandler({ InvalidDataAccessResourceUsageException.class, DataIntegrityViolationException.class, })
    public ResponseEntity<ErrorResponse> handleInvalidDataAccessResourceUsageException(HttpServletRequest request, DataAccessException ex) {
        String error = "Unable to execute sql query" + ex.getMessage() + "for request " + request.getRequestURL();
        this.logger.error(error);
        return build(new ErrorResponse(HttpStatus.BAD_REQUEST, error));
    }

    private ResponseEntity<ErrorResponse> build(ErrorResponse errorResponse) {
        return new ResponseEntity<>(errorResponse, errorResponse.getStatus());
    }
}

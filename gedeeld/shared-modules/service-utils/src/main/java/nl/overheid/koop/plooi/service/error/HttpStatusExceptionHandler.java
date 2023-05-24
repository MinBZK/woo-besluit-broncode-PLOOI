package nl.overheid.koop.plooi.service.error;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HttpStatusExceptionHandler {

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<String> handelHttpStatusException(HttpServletRequest request, HttpStatusException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
}

package nl.overheid.koop.plooi.service.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class HttpStatusException extends RuntimeException {

    private static final long serialVersionUID = 678540900988649816L;
    private final transient Logger logger = LoggerFactory.getLogger(getClass());
    private final HttpStatus status;

    HttpStatusException(HttpStatus httpStatus, String message) {
        super(message);
        this.status = httpStatus;
        this.logger.info("returning {} status for reason {}", httpStatus, message);
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}

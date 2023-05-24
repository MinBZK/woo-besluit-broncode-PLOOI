package nl.overheid.koop.plooi.search.service.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class HttpStatusException extends RuntimeException {

    private static final long serialVersionUID = -9122228206814578245L;
    private final Logger logger = LoggerFactory.getLogger(getClass());
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

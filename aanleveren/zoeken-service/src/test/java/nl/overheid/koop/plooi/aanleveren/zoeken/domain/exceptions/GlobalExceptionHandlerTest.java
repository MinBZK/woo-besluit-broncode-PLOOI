package nl.overheid.koop.plooi.aanleveren.zoeken.domain.exceptions;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler sut = new GlobalExceptionHandler();

    @Test
    void handlePageNotValidException() {
        val response = sut.handleIllegalArgumentException();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

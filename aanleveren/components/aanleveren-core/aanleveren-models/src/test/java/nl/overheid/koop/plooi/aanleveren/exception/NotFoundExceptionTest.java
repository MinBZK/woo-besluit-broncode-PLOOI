package nl.overheid.koop.plooi.aanleveren.exception;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotFoundExceptionTest {

    private final static String EXCEPTION = "Exception";

    @Test
    void testValidNotFoundException() {
        val ex =  new NotFoundException(EXCEPTION);

        assertEquals(EXCEPTION, ex.getMessage());
    }

}

package nl.overheid.koop.plooi.aanleveren.exception;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotValidUUIDExeceptionTest {

    private final static String EXCEPTION = "Exception";

    @Test
    void testNotValidUUIDExeception() {
        val ex =  new NotValidUUIDExeception(EXCEPTION);

        assertEquals(EXCEPTION, ex.getMessage());
    }

}

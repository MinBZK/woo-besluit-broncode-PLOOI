package nl.overheid.koop.plooi.aanleveren.models;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeasuringpointTest {
    private final static String DESCRIPTION = "Test description";
    private final static String PROCESSIDENTIFIER = "ID123456";
    private final static String PID = "UUID-12345";
    private final static Integer STATUSCODE = 218;

    @Test
    void testMeasuringpoint() {
        val measuringpoint = new MeasuringPoint(DESCRIPTION, PROCESSIDENTIFIER, PID, STATUSCODE);

        assertEquals(DESCRIPTION, measuringpoint.description());
        assertEquals(PROCESSIDENTIFIER, measuringpoint.processIdentifier());
        assertEquals(PID, measuringpoint.pid());
        assertEquals(STATUSCODE, measuringpoint.statuscode());
    }

    @Test
    void testNullPointerExceptionsInMeasuringpoint() {
        assertThrows(NullPointerException.class, () ->
                new MeasuringPoint(null, PROCESSIDENTIFIER, PID, STATUSCODE));

        assertThrows(NullPointerException.class, () ->
                new MeasuringPoint(DESCRIPTION, null, PID, STATUSCODE));
    }
}

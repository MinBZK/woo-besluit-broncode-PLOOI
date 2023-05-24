package nl.overheid.koop.plooi.aanleveren.fout;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlooiAanleverFoutTest {

    private final static String TYPE = "type";
    private final static String TITLE = "title";
    private final static Integer STATUS = 400;
    private final static String DETAIL = "detail";
    private final static String INSTANCE = "instance";
    private final static String MESSAGE = "message";

    @Test
    void testPlooiAanleverFout() {
        val messages = new ArrayList<String>();
        messages.add(MESSAGE);
        val plooiAanleverFout = new PlooiAanleverFout(TYPE, TITLE, STATUS, DETAIL, INSTANCE, messages);

        assertEquals(TYPE, plooiAanleverFout.type());
        assertEquals(TITLE, plooiAanleverFout.title());
        assertEquals(STATUS, plooiAanleverFout.status());
        assertEquals(DETAIL, plooiAanleverFout.detail());
        assertEquals(INSTANCE, plooiAanleverFout.instance());
        assertEquals(MESSAGE, plooiAanleverFout.messages().get(0));
    }

    @Test
    void testNullPointerExceptionsInPlooiAanleverFout() {
        val messages = new ArrayList<String>();
        messages.add(MESSAGE);

        assertThrows(NullPointerException.class, () ->
                new PlooiAanleverFout(null, TITLE, STATUS, DETAIL, INSTANCE, messages)
        );

        assertThrows(NullPointerException.class, () ->
                new PlooiAanleverFout(TYPE, null, STATUS, DETAIL, INSTANCE, messages)
        );

        assertThrows(NullPointerException.class, () ->
                new PlooiAanleverFout(TYPE, TITLE, null, DETAIL, INSTANCE, messages)
        );

        assertThrows(NullPointerException.class, () ->
                new PlooiAanleverFout(TYPE, TITLE, STATUS, null, INSTANCE, messages)
        );

        assertThrows(NullPointerException.class, () ->
                new PlooiAanleverFout(TYPE, TITLE, STATUS, DETAIL, null, messages)
        );

        assertDoesNotThrow(() ->
                new PlooiAanleverFout(TYPE, TITLE, STATUS, DETAIL, INSTANCE, null)
        );
    }


}

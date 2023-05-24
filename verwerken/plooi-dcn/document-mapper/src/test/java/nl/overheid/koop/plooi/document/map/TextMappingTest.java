package nl.overheid.koop.plooi.document.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TextMappingTest {

    private static final String TEST_TEXT = "one <b>two</b> three";
    private static final String CLEANED_TEXT = "one two three";

    @Test
    void getText_plain() {
        assertEquals(TEST_TEXT, TextMapping.getText(TextMapping.plain("dummy"), TEST_TEXT));
    }

    @Test
    void getText_embeddded() {
        assertEquals(CLEANED_TEXT, TextMapping.getText(TextMapping.embeddedHTML("dummy"), TEST_TEXT));
    }

    @Test
    void getTexts_plain() {
        assertEquals(List.of(TEST_TEXT), TextMapping.getTexts(TextMapping.plain("dummy"), new ArrayList<>(List.of(TEST_TEXT))));
    }

    @Test
    void getTexts_embeddded() {
        assertEquals(List.of(CLEANED_TEXT), TextMapping.getTexts(TextMapping.embeddedHTML("dummy"), new ArrayList<>(List.of(TEST_TEXT))));
    }
}

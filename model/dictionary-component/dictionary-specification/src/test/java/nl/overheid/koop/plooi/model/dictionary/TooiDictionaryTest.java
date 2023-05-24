package nl.overheid.koop.plooi.model.dictionary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TooiDictionaryTest {

    private final TooiDictionary tooiDictionary = new TooiDictionary("overheid:Informatietype");

    @BeforeEach
    void init() {
        new TooiJsonReader().read(this.tooiDictionary, getClass().getResourceAsStream("/tooi/ccw_plooi_documentsoorten_3.json"));
    }

    @Test
    void findStaatscourant() {
        assertEquals("Staatscourant", this.tooiDictionary.findUri("https://identifier.overheid.nl/tooi/def/thes/kern/c_0670bae1").get());
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/kern/c_0670bae1", this.tooiDictionary.findPrefLabel("Staatscourant").get());
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/kern/c_0670bae1", this.tooiDictionary.findAltLabel("Stcrt").get());
    }

    @Test
    void unknown() {
        assertFalse(this.tooiDictionary.findUri("https://identifier.overheid.nl/tooi/def/thes/kern/unknown").isPresent());
        assertFalse(this.tooiDictionary.findPrefLabel("UNKNOWN").isPresent());
        assertFalse(this.tooiDictionary.findAltLabel("UNKNOWN").isPresent());
    }
}

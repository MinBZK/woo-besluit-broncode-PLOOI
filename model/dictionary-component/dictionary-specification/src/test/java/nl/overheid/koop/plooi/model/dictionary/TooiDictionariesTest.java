package nl.overheid.koop.plooi.model.dictionary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TooiDictionariesTest {

    @Test
    void findStaatscourant() {
        var tooiDictionary = new TooiDictionaries().getDictionary("overheid:Informatietype").get();
        assertEquals("Staatscourant", tooiDictionary.findUri("https://identifier.overheid.nl/tooi/def/thes/kern/c_0670bae1").get());
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/kern/c_0670bae1", tooiDictionary.findPrefLabel("Staatscourant").get());
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/kern/c_0670bae1", tooiDictionary.findAltLabel("Stcrt").get());
        assertFalse(tooiDictionary.findAltLabel("Straatskoerantski").isPresent());
    }

    @Test
    void findStaatscourant_extraSynonyms() {
        var tooiDict = new TooiDictionaries();
        var tooiDictionary = tooiDict.getDictionary("overheid:Informatietype").get();
        tooiDict.readIntoDictionary(tooiDictionary, "/nl/overheid/koop/plooi/model/waardelijsten/documentsoorten_test_synonym.json");
        assertTrue(tooiDictionary.findAltLabel("Straatskoerantski").isPresent());
    }

    @Test
    void emptyExtraDictionary_isSafe() {
        var tooiDict = new TooiDictionaries();
        var tooiDictionary = tooiDict.getDictionary("overheid:Informatietype").get();
        tooiDict.readIntoDictionary(tooiDictionary, null);
        assertFalse(tooiDictionary.findAltLabel("Straatskoerantski").isPresent());
    }

    @Test
    void unknownExtraDictionary() {
        assertThrows(TooiDictionaryException.class, () -> {
            var tooiDict = new TooiDictionaries();
            tooiDict.readIntoDictionary(tooiDict.getDictionary("overheid:Informatietype").get(), "not_existing.json");
        });
    }

    @Test
    void unknown() {
        assertFalse(new TooiDictionaries().getDictionary("unknown").isPresent());
    }
}

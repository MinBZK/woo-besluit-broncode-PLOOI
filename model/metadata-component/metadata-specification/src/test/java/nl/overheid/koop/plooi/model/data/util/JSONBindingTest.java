package nl.overheid.koop.plooi.model.data.util;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class JSONBindingTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @ParameterizedTest
    @CsvSource({
            "test_api_plooi.json",
            "test_full_plooi.json",
            "test_full_plooi_versions.json",
            "manifest_versies.json",
            "../legacy/plooi_example.json",
            "../legacy/plooi_example_full.json",
            "../legacy/plooi_example_xhtml.json" })
    void plooiToFromJson(String file) {
        var jsonBinding = PlooiBindings.plooiBinding();
        assertEquals(
                readFileAsString(getClass(), file),
                jsonBinding.marshalToString(
                        jsonBinding.unmarshalFromFile(resolve(getClass(), file).getPath())));
    }
}

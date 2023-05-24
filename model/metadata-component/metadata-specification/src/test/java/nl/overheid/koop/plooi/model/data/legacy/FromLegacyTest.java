package nl.overheid.koop.plooi.model.data.legacy;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FromLegacyTest {

    private final FromLegacy fromLegacy = new FromLegacy();

    @ParameterizedTest
    @CsvSource({
            "plooi_example.xml,       plooi_example.json",
            "plooi_example_full.xml,  plooi_example_full.json",
            "plooi_example_xhtml.xml, plooi_example_xhtml.json" })
    void convert(String in, String out) throws IOException {
        assertEquals(
                readFileAsString(getClass(), out),
                this.fromLegacy.convert(Files.newInputStream(resolve(this.getClass(), in).toPath())));
    }
}

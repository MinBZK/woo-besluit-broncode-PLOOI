package nl.overheid.koop.plooi.dcn.aanleverloket;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.charset.StandardCharsets;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.test.util.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AanLoDocumentCollectorTest {

    private final AanLoDocumentCollector collector = new AanLoDocumentCollector(AanLoRoute.SOURCE_LABEL);

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    // TODO: Re-enable after this code moved into repository-service
    @DisabledOnOs(value = { OS.WINDOWS }, disabledReason = """
            see: DCN-Components - PlooiFile#fixup
            fixup method is using Files.probeContentType which is not OS-agnostic thus behaves different in Windows and *nix systems

            Java NIO bug: https://bugs.openjdk.org/browse/JDK-8186071
            """)
    @ParameterizedTest
    @CsvSource({
            // Zip File,
            "bijwerken_plooicb-2021-33.zip, bijwerken_plooicb-2021-33.xml, bijwerken_manifest.json",
            "toevoegenwork_plooicb-2021-4.zip, toevoegen-plooicb-2021-4.xml, toevoegen_manifest.json",
            "verwijderenwork_plooicb-2021-4.zip, verwijderen-plooicb-2021-4.xml, verwijderen_manifest.json" })
    void collectSinglePlooiDoc(String mapFile, String expectMetaFile, String expectPlooiManifest) {
        var plooiDocs = this.collector.mapAndCollect(TestUtils.readFileAsBytes(getClass(), mapFile), mapFile);
        assertEquals(1, plooiDocs.size());
        // String compare works and byte compare doesn't somehow
        assertEquals(readFileAsString(getClass(), expectMetaFile),
                stripWindowsCR(new String(
                        plooiDocs.get(0).getPlooiFiles().stream().filter(f -> "xml".equals(f.getFile().getLabel())).findAny().orElseThrow().getContent(),
                        StandardCharsets.UTF_8)));
        assertEquals(readFileAsString(getClass(), expectPlooiManifest),
                stripWindowsCR(PlooiBindings.filesBinding().marshalToString(plooiDocs.get(0).getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
    }

    @ParameterizedTest
    @CsvSource({
            // Zip File,
            "bijwerken_plooicb-2021-33.zip, plooicb-2021-33", "toevoegenwork_plooicb-2021-4.zip, plooicb-2021-4",
            "verwijderenwork_plooicb-2021-4.zip, plooicb-2021-4" })
    void testExternalIdExtractor(String fileName, String expected) {
        assertEquals(expected, this.collector.extractExternalId(fileName));
    }
}

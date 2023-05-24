package nl.overheid.koop.plooi.dcn.oep;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.List;
import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OepDocumentCollectorTest {

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
            "sru_record_kst.xml,            plooidoc-kst_collected.json",
            "sru_record_bijlage.xml,        plooidoc-blg-kst_collected.json",
            "sru_record_handeling.xml,      plooidoc-h-ek_collected.json",
            "sru_record_kv_onopgemaakt.xml, plooidoc-kv_collected.json",
    })
    void collectSinglePlooiDoc(String mapFile, String expectFile) throws IOException {
        var plooiDocs = mapAndCollect(mapFile);
        assertEquals(1, plooiDocs.size());
        assertEquals(
                readFileAsString(getClass(), expectFile),
                stripWindowsCR(PlooiBindings.filesBinding().marshalToString(plooiDocs.get(0).getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
        assertTrue(plooiDocs.get(0).status().getDiagnoses().isEmpty());
    }

    private List<DeliveryEnvelope> mapAndCollect(String mapFile) throws IOException {
        var plooiFile = OepSruRoute.OEP_FILEMAPPER.populate(readFileAsString(getClass(), mapFile));
        var plooiDocs = OepSruRoute.OEP_COLLECTOR.collect(plooiFile);
        return plooiDocs;
    }
}

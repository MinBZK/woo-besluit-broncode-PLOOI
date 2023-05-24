package nl.overheid.koop.plooi.dcn.ronl.archief;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RonlArchiefFileMapperTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    void mapDocument() throws IOException {
        var plooiFile = RonlArchiefRoute.RONL_FILEMAPPER.populate(readFileAsString(getClass(), "ronl-2011-5647.xml"));
        assertEquals("plooicb-2011-5647", plooiFile.getFile().getId());
        assertEquals("xml", plooiFile.getFile().getLabel());
        assertEquals(
                "https://archief06.archiefweb.eu/archives/archiefweb/20180422095750/https://www.rijksoverheid.nl/documenten/kamerstukken/2011/02/25/frauderapport-ov-chipkaart",
                plooiFile.getFile().getUrl());
        assertEquals("Frauderapport OV-chipkaart", plooiFile.getFile().getTitel());
        assertEquals(1, plooiFile.getChildren().size());
        var child = plooiFile.getChildren().get(0);
        assertEquals("201125843.pdf", child.getFile().getBestandsnaam());
        assertEquals("pdf", child.getFile().getLabel());
    }
}

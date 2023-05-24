package nl.overheid.koop.plooi.dcn.ronl.api;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import nl.overheid.koop.plooi.model.data.util.PlooiFileUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RonlApiFileMapperTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    void mapBundle() throws IOException {
        var plooiFile = RonlApiRoute.RONL_FILEMAPPER.populate(readFileAsString(getClass(), "bundle/0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml"));
        PlooiFileUtil.populate(plooiFile.getFile());
        assertEquals("0acb97b8-b3cf-4654-92c2-0fa4199c100c", plooiFile.getFile().getId());
        assertEquals("xml", plooiFile.getFile().getLabel());
        assertEquals("https://opendata.rijksoverheid.nl/v1/sources/rijksoverheid/documents/0acb97b8-b3cf-4654-92c2-0fa4199c100c", plooiFile.getFile().getUrl());
        assertEquals(2, plooiFile.getChildren().size());
        var firstChild = plooiFile.getChildren().get(0);
        PlooiFileUtil.populate(firstChild.getFile());
        assertEquals("Akte van aanstelling_001474.pdf", firstChild.getFile().getBestandsnaam());
        assertEquals("pdf", firstChild.getFile().getLabel());
        assertEquals(
                "https://opendata.rijksoverheid.nl/binaries/rijksoverheid/documenten/publicaties/1985/08/05/akte-van-aanstelling/Akte+van+aanstelling_001474.pdf",
                firstChild.getFile().getUrl());
        var secondChild = plooiFile.getChildren().get(1);
        PlooiFileUtil.populate(secondChild.getFile());
        assertEquals("Supplement akte van aanstelling_001474.pdf", secondChild.getFile().getId());
        assertEquals("pdf", secondChild.getFile().getLabel());
        assertEquals(
                "https://opendata.rijksoverheid.nl/binaries/rijksoverheid/documenten/publicaties/1985/08/05/akte-van-aanstelling/Supplement+akte+van+aanstelling_001474.pdf",
                secondChild.getFile().getUrl());
    }
}

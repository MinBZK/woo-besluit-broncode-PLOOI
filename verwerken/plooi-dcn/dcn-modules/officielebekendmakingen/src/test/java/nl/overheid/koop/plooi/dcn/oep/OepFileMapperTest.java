package nl.overheid.koop.plooi.dcn.oep;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OepFileMapperTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    void mapBijlage() throws IOException {
        var plooiFile = OepSruRoute.OEP_FILEMAPPER.populate(readFileAsString(getClass(), "sru_record_bijlage.xml"));
        assertEquals("blg-936562", plooiFile.getFile().getId());
        assertEquals("metadataowms", plooiFile.getFile().getLabel());
        assertEquals(
                "https://repository.overheid.nl/frbr/officielepublicaties/blg/onopgemaakt/blg-936562/1/metadataowms/metadata_owms.xml",
                plooiFile.getFile().getUrl());
        assertEquals(1, plooiFile.getChildren().size());
        var firstChild = plooiFile.getChildren().get(0);
        assertEquals("pdf", firstChild.getFile().getLabel());
        assertEquals(
                "https://repository.overheid.nl/frbr/officielepublicaties/blg/onopgemaakt/blg-936562/1/pdf/blg-936562.pdf",
                firstChild.getFile().getUrl());
    }

    @Test
    void mapHandeling() throws IOException {
        var plooiFile = OepSruRoute.OEP_FILEMAPPER.populate(readFileAsString(getClass(), "sru_record_handeling.xml"));
        assertEquals("h-ek-20202021-1-1", plooiFile.getFile().getId());
        assertEquals("metadataowms", plooiFile.getFile().getLabel());
        assertEquals(
                "https://repository.overheid.nl/frbr/officielepublicaties/h-ek/20202021/h-ek-20202021-1-1/1/metadataowms/metadata_owms.xml",
                plooiFile.getFile().getUrl());
        assertEquals(3, plooiFile.getChildren().size());
        var firstChild = plooiFile.getChildren().get(0);
        assertEquals("xml", firstChild.getFile().getLabel());
        assertEquals(
                "https://repository.overheid.nl/frbr/officielepublicaties/h-ek/20202021/h-ek-20202021-1-1/1/xml/h-ek-20202021-1-1.xml",
                firstChild.getFile().getUrl());
        var secondChild = plooiFile.getChildren().get(1);
        assertEquals("html", secondChild.getFile().getLabel());
        assertEquals(
                "https://repository.overheid.nl/frbr/officielepublicaties/h-ek/20202021/h-ek-20202021-1-1/1/html/h-ek-20202021-1-1.html",
                secondChild.getFile().getUrl());
        var thirdChild = plooiFile.getChildren().get(2);
        assertEquals("pdf", thirdChild.getFile().getLabel());
        assertEquals(
                "https://repository.overheid.nl/frbr/officielepublicaties/h-ek/20202021/h-ek-20202021-1-1/1/pdf/h-ek-20202021-1-1.pdf",
                thirdChild.getFile().getUrl());
    }

    @Test
    void mapKvOnopgemaakt() throws IOException {
        var plooiFile = OepSruRoute.OEP_FILEMAPPER.populate(readFileAsString(getClass(), "sru_record_kv_onopgemaakt.xml"));
        assertEquals("kv-2989900822", plooiFile.getFile().getId());
        assertEquals("metadataowms", plooiFile.getFile().getLabel());
        assertEquals(
                "https://repository.overheid.nl/frbr/officielepublicaties/kv/onopgemaakt/kv-2989900822/1/metadataowms/metadata_owms.xml",
                plooiFile.getFile().getUrl());
        assertEquals(2, plooiFile.getChildren().size());
        var firstChild = plooiFile.getChildren().get(0);
        assertEquals("xml", firstChild.getFile().getLabel());
        assertEquals(
                "https://repository.overheid.nl/frbr/officielepublicaties/kv/onopgemaakt/kv-2989900822/1/xml/kv-2989900822.xml",
                firstChild.getFile().getUrl());
        var secondChild = plooiFile.getChildren().get(1);
        assertEquals("html", secondChild.getFile().getLabel());
        assertEquals(
                "https://repository.overheid.nl/frbr/officielepublicaties/kv/onopgemaakt/kv-2989900822/1/html/kv-2989900822.html",
                secondChild.getFile().getUrl());
    }
}

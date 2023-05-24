package nl.overheid.koop.plooi.model.data.legacy;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Classificatiecollectie;
import nl.overheid.koop.plooi.model.data.Document;
import nl.overheid.koop.plooi.model.data.Geldigheid;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.model.data.Titelcollectie;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.model.data.util.XMLHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ToLegacyTest {

    private final ToLegacy toLegacy = new ToLegacy();

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    void constructedPlooi() {
        assertEquals(readFileAsString(getClass(), "constructedPlooi.xml"), this.toLegacy.convertPlooi(construct()));
    }

    @Test
    void constructedMeta() {
        assertFalse(this.toLegacy.convertPart(construct(), "plooi:meta").isEmpty());
    }

    @Test
    void constructedDocumenten() {
        assertEquals(
                """
                        <plooi:documenten xmlns:plooi="http://standaarden.overheid.nl/plooi/terms/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:overheid="http://standaarden.overheid.nl/owms/terms/">
                            <plooi:document published="false">
                                <plooi:manifestatie-label>pdf</plooi:manifestatie-label>
                                <plooi:bestandsnaam>bestand.pdf</plooi:bestandsnaam>
                            </plooi:document>
                        </plooi:documenten>
                        """,
                this.toLegacy.convertPart(construct(), "plooi:documenten"));
    }

    @Test
    void constructedUnknow() {
        assertNull(this.toLegacy.convertPart(construct(), "plooi:unknow"));
    }

    private Plooi construct() {
        var plooi = new Plooi();
        var plooiIntern = new PlooiIntern();
        plooi.setPlooiIntern(plooiIntern);
        String id = "test-dcnid";
        plooiIntern.setDcnId(id);
        plooiIntern.sourceLabel("test").addExtIdItem("test");
        plooiIntern.setAanbieder("Dummy");
        var docMeta = new Document().titelcollectie(new Titelcollectie()).classificatiecollectie(new Classificatiecollectie());
        plooi.setDocument(docMeta);
        docMeta.setPid(id);
        docMeta.getTitelcollectie().setOfficieleTitel("Dummy title");
        docMeta.getClassificatiecollectie().addDocumentsoortenItem(dummy(new IdentifiedResource(), "DocumentSoort"));
        docMeta.getClassificatiecollectie().addThemasItem(dummy(new IdentifiedResource(), "Thema"));
        docMeta.setOpsteller(dummy(new IdentifiedResource(), "Opsteller"));
        docMeta.setVerantwoordelijke(dummy(new IdentifiedResource(), "Verantwoordelijke"));
        docMeta.setPublisher(dummy(new IdentifiedResource(), "Publisher"));
        docMeta.setCreatiedatum("2021-08-01");
        docMeta.geldigheid(new Geldigheid().begindatum(LocalDate.parse("1970-07-01")).einddatum(LocalDate.parse("2025-01-01")));
        docMeta.setWeblocatie("http://www.example.com");
        var versies = new ArrayList<Versie>();
        versies.add(new Versie().nummer(1).addBestandenItem(new Bestand().bestandsnaam("bestand.pdf").label("pdf")));
        plooi.setVersies(versies);
        return plooi;
    }

    @SuppressWarnings("unchecked")
    private <T extends IdentifiedResource> T dummy(T resource, String value) {
        return (T) resource
                .id("http://dummy")
                .type("http://dummy")
                .label(value)
                .bronwaarde(value);
    }

    @ParameterizedTest
    @CsvSource({
            "plooi_example.xml,             plooi_example.json",
            "plooi_example_full.xml,        plooi_example_full.json",
            "plooi_example_xhtml.xml,       plooi_example_xhtml.json",
            "test_full_plooi_versions.xml,  ../util/test_full_plooi_versions.json",
    })
    void convert(String out, String in) throws IOException {
        assertEquals(
                readFileAsString(getClass(), out),
                XMLHelper.trim(this.toLegacy.convertPlooi(PlooiBindings.plooiBinding().unmarshalFromFile(resolve(this.getClass(), in).getPath()))));
    }
}

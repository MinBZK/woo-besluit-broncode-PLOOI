package nl.overheid.koop.plooi.dcn.roo;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentBuilder;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Versie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RooSolrDocumentBuilderTest {

    private SolrDocumentBuilder solrDocumentBuilder = new SolrDocumentBuilder(null);

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "false");
    }

    @Test
    void mapRooProDrn() throws IOException {
        var doc = RooNormalizationTest.map(
                new PlooiEnvelope("roo", "prodren-1234"),
                readFileAsString(getClass(), "metadata_roo-prdrn.xml"));
        doc.getVersies()
                .add(new Versie()
                        .nummer(1)
                        .openbaarmakingsdatum(LocalDate.parse("2020-07-24"))
                        .mutatiedatumtijd(OffsetDateTime.parse("2020-07-24T10:10:49.0Z"))
                        .addBestandenItem(
                                new Bestand()
                                        .gepubliceerd(false)
                                        .bestandsnaam("https___identifier.overheid.nl_tooi_id_provincie_xxx.xml")
                                        .label("xml")
                                        .mimeType("application/xml")
                                        .grootte(666l)));
        doc.getVersies().add(new Versie().mutatiedatumtijd(OffsetDateTime.parse("2020-07-24T10:10:49.0Z")));
        var solrized = SolrDocumentBuilder.jsonStr(this.solrDocumentBuilder.buildSolrDocument(doc));
        assertEquals(
                readFileAsString(getClass(), "solr-prov.json"),
                solrized);
    }
}

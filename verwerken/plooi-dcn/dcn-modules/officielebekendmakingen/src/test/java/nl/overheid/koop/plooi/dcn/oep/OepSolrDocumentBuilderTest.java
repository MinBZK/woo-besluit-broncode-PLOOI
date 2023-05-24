package nl.overheid.koop.plooi.dcn.oep;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentBuilder;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OepSolrDocumentBuilderTest {

    private SolrDocumentBuilder solrDocumentBuilder = new SolrDocumentBuilder(null);

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "false");
    }

    @Test
    void mapOepKst() throws IOException {
        var doc = OepNormalizationTest.map(new PlooiEnvelope("oep", "kst-35842-4"), readFileAsString(getClass(), "metadata_owms-kst.xml"));
        doc.getVersies()
                .add(new Versie()
                        .nummer(1)
                        .openbaarmakingsdatum(LocalDate.parse("2021-05-27"))
                        .mutatiedatumtijd(OffsetDateTime.parse("2021-05-27T13:08:55.5655901Z"))
                        .bestanden(PlooiBindings.filesBinding()
                                .unmarshalFromFile(
                                        resolve(getClass(), "plooidoc-kst_collected.json").getAbsolutePath())));
        var solrized = SolrDocumentBuilder.jsonStr(this.solrDocumentBuilder.buildSolrDocument(doc));
        assertEquals(
                readFileAsString(getClass(), "solr-kst.json"),
                solrized);
    }
}

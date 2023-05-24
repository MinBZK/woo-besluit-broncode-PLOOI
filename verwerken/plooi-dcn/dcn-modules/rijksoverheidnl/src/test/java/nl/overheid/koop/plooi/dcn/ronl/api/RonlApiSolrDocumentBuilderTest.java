package nl.overheid.koop.plooi.dcn.ronl.api;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentBuilder;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RonlApiSolrDocumentBuilderTest {

    @Mock(lenient = true)
    private PublicatieClient publicatieClient;

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "false");
    }

    @Test
    void mapSingleFile() throws IOException {
        var doc = RonlApiNormalizationTest.map(new PlooiEnvelope(RonlApiRoute.SOURCE_LABEL, "5afce6ec-a4ed-4c62-a6d5-168a59247678"),
                readFileAsString(getClass(), "single_file/5afce6ec-a4ed-4c62-a6d5-168a59247678.xml"));
        doc.getVersies()
                .add(new Versie()
                        .nummer(1)
                        .openbaarmakingsdatum(LocalDate.parse("1998-08-31"))
                        .mutatiedatumtijd(OffsetDateTime.parse("2020-07-24T10:10:49.0Z"))
                        .bestanden(PlooiBindings.filesBinding()
                                .unmarshalFromFile(
                                        resolve(getClass(), "single_file/16bac8e0eb30525cc2655d53d4fb50b5acabfb30_collected.json").getAbsolutePath())));
        var solrized = SolrDocumentBuilder.jsonStr(new SolrDocumentBuilder(null).buildSolrDocument(doc));
        assertEquals(
                readFileAsString(getClass(), "single_file/16bac8e0eb30525cc2655d53d4fb50b5acabfb30_solr.json"),
                solrized);
//      new SolrDocumentIndexer().process(this.solrDocumentBuilder.process(solrized));
    }

    @Test
    void mapBundleFile() throws IOException {
        var doc = RonlApiNormalizationTest.map(new PlooiEnvelope(RonlApiRoute.SOURCE_LABEL, "0acb97b8-b3cf-4654-92c2-0fa4199c100c"),
                readFileAsString(getClass(), "bundle/0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml"));
        doc.getVersies()
                .add(new Versie()
                        .nummer(1)
                        .openbaarmakingsdatum(LocalDate.parse("1985-08-05"))
                        .mutatiedatumtijd(OffsetDateTime.parse("2020-07-24T10:10:49.0Z"))
                        .bestanden(PlooiBindings.filesBinding()
                                .unmarshalFromFile(
                                        resolve(getClass(), "bundle/228036432630c580dcdddefd9c3a6de8e2587c1b_collected.json").getAbsolutePath())));
        when(this.publicatieClient.getPlooi(anyString()))
                .thenReturn(doc.getPlooi());
        when(this.publicatieClient.getText(anyString()))
                .thenReturn("Document tekst");
        when(this.publicatieClient.getRelations(anyString(), any()))
                .thenReturn(PlooiBindings.relationsBinding()
                        .unmarshalFromFile(resolve(getClass(), "bundle/228036432630c580dcdddefd9c3a6de8e2587c1b_relations.json").getAbsolutePath()));

        var solrized = SolrDocumentBuilder.jsonStr(new SolrDocumentBuilder(this.publicatieClient).process(doc.getPlooiIntern().getDcnId()));
        assertEquals(
                readFileAsString(getClass(), "bundle/228036432630c580dcdddefd9c3a6de8e2587c1b_solr.json"),
                solrized);
    }
}

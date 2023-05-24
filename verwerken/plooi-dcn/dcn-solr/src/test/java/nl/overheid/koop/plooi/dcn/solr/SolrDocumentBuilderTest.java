package nl.overheid.koop.plooi.dcn.solr;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.ExtraMetadata;
import nl.overheid.koop.plooi.model.data.ExtraMetadataVeld;
import nl.overheid.koop.plooi.model.data.Geldigheid;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.model.data.Versie;
import org.junit.jupiter.api.Test;

class SolrDocumentBuilderTest {

    private SolrDocumentBuilder solrDocumentBuilder = new SolrDocumentBuilder(null);

    @Test
    void buildSolrDocument() throws IOException {
        var plooi = somePlooi().addToDocumentText("Document text");
        var solrized = SolrDocumentBuilder.jsonStr(this.solrDocumentBuilder.buildSolrDocument(plooi));
        assertTrue(plooi.status().getDiagnoses().isEmpty());
        assertEquals(readFileAsString(getClass(), "solr.json"), solrized);
    }

    @Test
    void buildSolrDocumentWithoutText() throws IOException {
        var plooi = somePlooi();
        this.solrDocumentBuilder.buildSolrDocument(plooi);
        assertEquals(1, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.EMPTY_TEXT.name(), plooi.status().getDiagnoses().get(0).getCode());
    }

    private PlooiEnvelope somePlooi() {
        var plooi = new PlooiEnvelope("tst", "tst");
        plooi.getPlooiIntern().setAanbieder("Dummy");
        plooi.getTitelcollectie().setOfficieleTitel("Dummy title");
        plooi.getClassificatiecollectie().addDocumentsoortenItem(dummy(new IdentifiedResource(), "Documentsoorten"));
        plooi.getClassificatiecollectie().addThemasItem(dummy(new IdentifiedResource(), "Themas"));
        plooi.getClassificatiecollectie().addOnderwerpenRonlItem(dummy(new IdentifiedResource(), "OnderwerpenRonl"));
        plooi.getClassificatiecollectie().addTrefwoordenItem("Trefwoord");
        var docMeta = plooi.getDocumentMeta();
        docMeta.pid("https://open.overheid.nl/documenten/tst-0f0264a8c6fdc4de85420e56cade9087a9a99885");
        docMeta.setOnderwerpen(List.of("Onderwerp"));
        docMeta.setOmschrijvingen(List.of("Omschrijving"));
        docMeta.setOpsteller(dummy(new IdentifiedResource(), "Opsteller"));
        docMeta.setVerantwoordelijke(dummy(new IdentifiedResource(), "Verantwoordelijke"));
        docMeta.setPublisher(dummy(new IdentifiedResource(), "Publisher"));
        docMeta.setCreatiedatum("juni 2021");
        docMeta.geldigheid(new Geldigheid().begindatum(LocalDate.parse("2021-07-01")));
        docMeta.setWeblocatie("http://www.example.com");
        docMeta.addPublicatiebestemmingItem("rijksoverheid.nl");
        docMeta.extraMetadata(Stream.of(
                new ExtraMetadata()
                        .velden(Set.of(new ExtraMetadataVeld().key("extraMetadataKey1").values(List.of("extraMetadataValue1")))),
                new ExtraMetadata()
                        .prefix("plooi.displayfield")
                        .velden(Set.of(new ExtraMetadataVeld().key("extraMetadataKey2").values(List.of("extraMetadataValue2a", "extraMetadataValue2b")))))
                .toList());
        docMeta.setAggregatiekenmerk("Aggregatiekenmerk");
        docMeta.setIdentifiers(Set.of("BronIdentifier"));
        plooi.getVersies()
                .add(new Versie()
                        .nummer(1)
                        .openbaarmakingsdatum(LocalDate.parse("2020-07-24"))
                        .mutatiedatumtijd(OffsetDateTime.parse("2020-07-24T10:10:49.0Z"))
                        .zichtbaarheidsdatumtijd(OffsetDateTime.parse("2020-08-01T00:00:00.0Z"))
                        .addBestandenItem(
                                new Bestand()
                                        .gepubliceerd(true)
                                        .bestandsnaam("bestand.pdf")
                                        .label("pdf")
                                        .mimeType("application/pdf")
                                        .grootte(234234l)));
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
}

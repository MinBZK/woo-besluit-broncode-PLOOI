package nl.overheid.koop.plooi.document.map;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripTrailingSpaces;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PlooiDocumentJsonMapperTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    // Unrealistic mapper that tries to touch all PlooiJsonMapping logic using a modified API json
    public static final PlooiDocumentMapping PLOOI_MAPPER = ConfigurableDocumentMapper.configureWith(
            new PlooiJsonMapping())
            .setAanbieder("plooi")
            .mapOfficieleTitel(TextMapping.plain("$.document.titelcollectie.officieleTitel"))
            .mapVerkorteTitel(TextMapping.embeddedHTML("$.document.titelcollectie.verkorteTitels[*]"))
            .mapAlternatieveTitel(TextMapping.embeddedHTML("$.document.titelcollectie.alternatieveTitels[*]"))
            .mapDocumentsoort(ResourceMapping.full(null, "$.document.classificatiecollectie.documentsoort.label", null,
                    "$.document.classificatiecollectie.documentsoort.id"))
            .addThema(ResourceMapping.full("$.document.classificatiecollectie.themas[*]", "label", null, "id"))
            .mapTrefwoord("$.document.classificatiecollectie.trefwoorden[*]")
            .mapIdentifier("$.document.classificatiecollectie.identifiers[*]")
            .mapWeblocatie("$.document.identifiers[0]")
            .mapCreatiedatum("$.document.testdates[1]")
            .mapGeldigheidsstartdatum(DateMapping.isoDate("$.document.testdates[0]"))
            .mapVerantwoordelijke(ResourceMapping.full(null, "$.document.verantwoordelijke.label", null, null))
            .mapOpsteller(ResourceMapping.full("$.document.opsteller", "label", "type", null))
            .mapPublisher(ResourceMapping.full("$.document.publisher", "label", "type", null))
            .mapLanguage(ResourceMapping.full("$.document.language", "label", null, "id"))
            .mapOnderwerp("$.document.classificatiecollectie.themas[*].label")
            .mapAggregatiekenmerk("$.document.aggregatiekenmerk")
            .addBodyTekst(TextMapping.embeddedHTML("$.document.titelcollectie.alternatieveTitels[*]"))
            .addBodyTekst(TextMapping.embeddedHTML("$.body.tekst[*]"))
            .addExtraMetadata(ExtraMetadataMapping.basic("meta1", "$.document.metadata[0].value"))
            .addExtraMetadata(ExtraMetadataMapping.dynamic("$.document.metadata[1]", "key", "value"))
            .mapper();

    @Test
    void populateWithEmptyMapper() throws IOException {
        PlooiEnvelope plooiDoc = ConfigurableDocumentMapper.configureWith(
                new PlooiJsonMapping())
                .mapper()
                .populate(resolve(getClass(), "plooi-example-v0.9.json"), new PlooiEnvelope("tst", "tst"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "empty-plooidoc.json"),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi()))));
        assertTrue(plooiDoc.getDocumentText().isEmpty());
    }

    @Test
    void populateSuccess() throws IOException {
        PlooiEnvelope plooiDoc = PLOOI_MAPPER.populate(resolve(getClass(), "plooi-example-v0.9.json"),
                new PlooiEnvelope("plooi-api", "121a4567-e29b-12d3-a716-446655440000"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "plooidoc-json.json"),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi()))));
        assertEquals("""
                20210719 - Kapvergunning Hooigracht Den Haag \
                Een hele mooie titel over deze illustere vergunning om monumentale bomen aan de Hooigracht te kappen \
                Of zo iets""",
                plooiDoc.getDocumentText());
    }

    @Test
    void populateXhtmlBody() throws IOException {
        PlooiEnvelope plooiDoc = PLOOI_MAPPER.populate(resolve(getClass(), "plooi-xhtml.json"),
                new PlooiEnvelope("oep", "kst-32761-184"));
        assertTrue(plooiDoc.status().getDiagnoses().isEmpty(), "Diagnostics: " + plooiDoc.status());
        assertEquals(
                readFileAsString(getClass(), "plooi-xhtml.json"),
                stripWindowsCR(stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(plooiDoc.getPlooi())))));
        assertEquals("""
                32 761 \
                Verwerking en bescherming persoonsgegevens  \
                Nr. 184 \
                MOTIE VAN HET LID BELHAJ C.S.  \
                Voorgesteld\
                26 mei 2021   \
                De Kamer,  \
                gehoord de beraadslaging,  \
                overwegende dat er bij het Land Information Manoeuvre Centre geen duidelijke grenzen \
                waren tussen experiment en bijstand;  \
                overwegende dat het niet duidelijk is onder welke (kern)taken Defensie mag experimenteren;  \
                verzoekt de regering, duidelijke kaders op te stellen voor toekomstige experimenteeromgevingen \
                zoals het LIMC, zodat te allen tijde \
                gegarandeerd kan worden dat Defensie niet experimenteert \
                met grondwettelijke taken of andere grondslagen of militair-juridische kaders,  \
                en gaat over tot de orde van de dag.  \
                Belhaj  \
                Stoffer  \
                Boswijk  \
                Kuzu""",
                plooiDoc.getDocumentText());
    }
}

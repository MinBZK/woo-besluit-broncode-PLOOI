package nl.overheid.koop.plooi.document.normalize;

import static nl.overheid.koop.plooi.document.normalize.PlooiDocumentValidator.UNKNOWN;
import static nl.overheid.koop.plooi.document.normalize.PlooiDocumentValidator.UNKNOWN_DOCTYPE_ID;
import static nl.overheid.koop.plooi.document.normalize.PlooiDocumentValidator.UNKNOWN_ORG_ID;
import static nl.overheid.koop.plooi.document.normalize.PlooiDocumentValidator.UNKNOWN_THEME_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.document.map.ExtraMetadataMapping;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.ExtraMetadataVeld;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Versie;
import org.junit.jupiter.api.Test;

class PlooiDocumentValidatorTest {

    private static final PlooiDocumentValidator VALIDATOR = new PlooiDocumentValidator();

    @Test
    void validateRequired() {
        var emptyPlooi = new PlooiEnvelope(new Plooi());
        VALIDATOR.process(emptyPlooi);
        assertEquals(10, emptyPlooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.REQUIRED.name(), emptyPlooi.status().getDiagnoses().get(0).getCode());
        assertEquals("OfficieleTitel", emptyPlooi.status().getDiagnoses().get(0).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_DEFAULT.name(), emptyPlooi.status().getDiagnoses().get(1).getCode());
        assertEquals("Documentsoort", emptyPlooi.status().getDiagnoses().get(1).getTarget());
        assertEquals("Missing required field Documentsoort, set it to default value 'Onbekend'", emptyPlooi.status().getDiagnoses().get(1).getMessage());
        assertEquals(DiagnosticCode.REQUIRED_DEFAULT.name(), emptyPlooi.status().getDiagnoses().get(2).getCode());
        assertEquals("Thema", emptyPlooi.status().getDiagnoses().get(2).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_DEFAULT.name(), emptyPlooi.status().getDiagnoses().get(3).getCode());
        assertEquals("Opsteller", emptyPlooi.status().getDiagnoses().get(3).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_DEFAULT.name(), emptyPlooi.status().getDiagnoses().get(4).getCode());
        assertEquals("Verantwoordelijke", emptyPlooi.status().getDiagnoses().get(4).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_DEFAULT.name(), emptyPlooi.status().getDiagnoses().get(5).getCode());
        assertEquals("Publisher", emptyPlooi.status().getDiagnoses().get(5).getTarget());
        assertEquals(DiagnosticCode.REQUIRED.name(), emptyPlooi.status().getDiagnoses().get(6).getCode());
        assertEquals("Aanbieder", emptyPlooi.status().getDiagnoses().get(6).getTarget());
        assertEquals(DiagnosticCode.REQUIRED.name(), emptyPlooi.status().getDiagnoses().get(7).getCode());
        assertEquals("DcnId", emptyPlooi.status().getDiagnoses().get(7).getTarget());
        assertEquals(DiagnosticCode.REQUIRED.name(), emptyPlooi.status().getDiagnoses().get(8).getCode());
        assertEquals("ExtId", emptyPlooi.status().getDiagnoses().get(8).getTarget());
        assertEquals(DiagnosticCode.REQUIRED.name(), emptyPlooi.status().getDiagnoses().get(9).getCode());
        assertEquals("SourceLabel", emptyPlooi.status().getDiagnoses().get(9).getTarget());
    }

    @Test
    void emptyDefault() {
        var plooi = minimalPlooi(false, false, false);
        var documentMeta = plooi.getDocumentMeta();
        // empty resources added by minimalPlooi, removed in validation and defaults added
        VALIDATOR.process(plooi);
        assertEquals(1, documentMeta.getClassificatiecollectie().getDocumentsoorten().size());
        assertResource(documentMeta.getClassificatiecollectie().getDocumentsoorten().get(0), UNKNOWN_DOCTYPE_ID, UNKNOWN, null);
        assertEquals(1, documentMeta.getClassificatiecollectie().getThemas().size());
        assertResource(documentMeta.getClassificatiecollectie().getThemas().get(0), UNKNOWN_THEME_ID, UNKNOWN, null);
        assertResource(documentMeta.getOpsteller(), UNKNOWN_ORG_ID, UNKNOWN, null);
        assertResource(documentMeta.getVerantwoordelijke(), UNKNOWN_ORG_ID, UNKNOWN, null);
        assertResource(documentMeta.getPublisher(), UNKNOWN_ORG_ID, UNKNOWN, null);
    }

    @Test
    void emptyExtrametadata() {
        var plooi = minimalPlooi(false, false, false);
        var extraMetadata = plooi.getDocumentMeta().extraMetadata(new ArrayList<>()).getExtraMetadata();
        ExtraMetadataMapping
                .temp(null, null)
                .locateIn(extraMetadata)
                .getVelden()
                .add(new ExtraMetadataVeld().key("dummy").values(List.of("Dummmy")));
        VALIDATOR.process(plooi);
        assertTrue(extraMetadata.isEmpty()); // temp values are removed
    }

    @Test
    void validateResourceIds() {
        var plooi = minimalPlooi(false, true, true);
        VALIDATOR.process(plooi);
        assertEquals(7, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.REQUIRED_ID.name(), plooi.status().getDiagnoses().get(0).getCode());
        assertEquals("Documentsoort", plooi.status().getDiagnoses().get(0).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_ID.name(), plooi.status().getDiagnoses().get(1).getCode());
        assertEquals("Thema", plooi.status().getDiagnoses().get(1).getTarget());
        assertEquals("Missing required uri of field Thema with label 'dummy'", plooi.status().getDiagnoses().get(1).getMessage());
        assertEquals(DiagnosticCode.REQUIRED_ID.name(), plooi.status().getDiagnoses().get(2).getCode());
        assertEquals("Opsteller", plooi.status().getDiagnoses().get(2).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_ID.name(), plooi.status().getDiagnoses().get(3).getCode());
        assertEquals("Verantwoordelijke", plooi.status().getDiagnoses().get(3).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_ID.name(), plooi.status().getDiagnoses().get(4).getCode());
        assertEquals("Publisher", plooi.status().getDiagnoses().get(4).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_ID.name(), plooi.status().getDiagnoses().get(5).getCode());
        assertEquals("Format", plooi.status().getDiagnoses().get(5).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_ID.name(), plooi.status().getDiagnoses().get(6).getCode());
        assertEquals("Language", plooi.status().getDiagnoses().get(6).getTarget());
    }

    @Test
    void validateResourceLabels() {
        var plooi = minimalPlooi(true, false, true);
        VALIDATOR.process(plooi);
        assertEquals(6, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.REQUIRED_LABEL.name(), plooi.status().getDiagnoses().get(0).getCode());
        assertEquals("Documentsoort", plooi.status().getDiagnoses().get(0).getTarget());
        assertEquals("Missing required label of field Documentsoort with uri http://dummy", plooi.status().getDiagnoses().get(0).getMessage());
        assertEquals(DiagnosticCode.REQUIRED_LABEL.name(), plooi.status().getDiagnoses().get(1).getCode());
        assertEquals("Thema", plooi.status().getDiagnoses().get(1).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_LABEL.name(), plooi.status().getDiagnoses().get(2).getCode());
        assertEquals("Opsteller", plooi.status().getDiagnoses().get(2).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_LABEL.name(), plooi.status().getDiagnoses().get(3).getCode());
        assertEquals("Verantwoordelijke", plooi.status().getDiagnoses().get(3).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_LABEL.name(), plooi.status().getDiagnoses().get(4).getCode());
        assertEquals("Publisher", plooi.status().getDiagnoses().get(4).getTarget());
        assertEquals(DiagnosticCode.REQUIRED_LABEL.name(), plooi.status().getDiagnoses().get(5).getCode());
        assertEquals("Format", plooi.status().getDiagnoses().get(5).getTarget());
    }

    @Test
    void validateSupportedTypes() {
        PlooiEnvelope plooi = minimalPlooi(true, true, true);
        plooi.getVersies()
                .add(new Versie()
                        .nummer(1)
                        .mutatiedatumtijd(OffsetDateTime.parse("2020-07-24T10:10:49.0Z"))
                        .addBestandenItem(new Bestand().bestandsnaam("kip").label("pdf").gepubliceerd(true))
                        .addBestandenItem(new Bestand().bestandsnaam("kip").label("PDF").gepubliceerd(true))
                        .addBestandenItem(new Bestand().bestandsnaam("kip.pdf").label("document").gepubliceerd(true))
                        .addBestandenItem(new Bestand().bestandsnaam("kip.zip").label("zip").gepubliceerd(true))
                        .addBestandenItem(new Bestand().gepubliceerd(true)));
        VALIDATOR.process(plooi);
        assertTrue(plooi.status().getDiagnoses().isEmpty());
        var files = plooi.getVersies().get(0).getBestanden();
        assertTrue(files.get(0).isGepubliceerd());
        assertTrue(files.get(1).isGepubliceerd());
        assertTrue(files.get(2).isGepubliceerd());
        assertFalse(files.get(3).isGepubliceerd());
        assertFalse(files.get(4).isGepubliceerd());
    }

    private void assertResource(IdentifiedResource resource, String id, String val, String scheme) {
        assertEquals(id, resource.getId());
        assertEquals(val, resource.getLabel());
        assertEquals(scheme, resource.getType());
    }

    private PlooiEnvelope minimalPlooi(boolean withId, boolean withValue, boolean withScheme) {
        var plooi = new PlooiEnvelope("tst", "tst");
        plooi.getPlooiIntern().setAanbieder("Dummy");
        plooi.getTitelcollectie().setOfficieleTitel("Dummy title");
        plooi.getClassificatiecollectie().addDocumentsoortenItem(dummy(new IdentifiedResource(), withId, withValue, withScheme));
        plooi.getClassificatiecollectie().addThemasItem(dummy(new IdentifiedResource(), withId, withValue, withScheme));
        var docMeta = plooi.getDocumentMeta();
        docMeta.setOnderwerpen(List.of("Onderwerp"));
        docMeta.setOmschrijvingen(List.of("Omschrijving"));
        docMeta.setOpsteller(dummy(new IdentifiedResource(), withId, withValue, withScheme));
        docMeta.setVerantwoordelijke(dummy(new IdentifiedResource(), withId, withValue, withScheme));
        docMeta.setPublisher(dummy(new IdentifiedResource(), withId, withValue, withScheme));
        docMeta.setCreatiedatum("juni 2021");
        docMeta.setLanguage(dummy(new IdentifiedResource(), withId, withValue, withScheme));
        docMeta.setFormat(dummy(new IdentifiedResource(), withId, withValue, withScheme));
        docMeta.setWeblocatie("http://www.example.com");
        docMeta.setAggregatiekenmerk("Aggregatiekenmerk");
        docMeta.setIdentifiers(Set.of("BronIdentifier"));
        return plooi;
    }

    @SuppressWarnings("unchecked")
    private <T extends IdentifiedResource> T dummy(T resource, boolean withId, boolean withValue, boolean withScheme) {
        return (T) resource.id(withId ? "http://dummy" : null).type(withScheme ? "http://dummy" : null).label(withValue ? "dummy" : null);
    }
}

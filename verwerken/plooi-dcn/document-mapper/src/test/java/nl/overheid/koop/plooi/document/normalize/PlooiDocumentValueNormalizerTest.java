package nl.overheid.koop.plooi.document.normalize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.DiagnosticCode;
import nl.overheid.koop.plooi.model.data.IdentifiedResource;
import nl.overheid.koop.plooi.model.dictionary.TooiDictionaryException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PlooiDocumentValueNormalizerTest {

    private static final PlooiDocumentValueNormalizer NORMALIZER = buildNormalizer("waardelijsten_test.properties", null);

    private static PlooiDocumentValueNormalizer buildNormalizer(String extraDictionaryProperties, String extraFieldProperties) {
        return PlooiDocumentValueNormalizer
                .configure()
                .useExtraDictionaryProperties(extraDictionaryProperties)
                .useExtraFieldProperties(extraFieldProperties)
                .build();
    }

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    void unknownExtraPropsLocation() {
        assertThrows(TooiDictionaryException.class, () -> buildNormalizer("wrong_location", null));
    }

    @Test
    void brokenExtraPropsLocation() {
        assertThrows(TooiDictionaryException.class, () -> buildNormalizer("waardelijsten_test_broken.properties", null));
    }

    @Test
    void unknownSchemeReference() {
        assertThrows(PlooiNormalizationException.class, () -> buildNormalizer(null, "mapped_fields_test_unknownref.properties"));
    }

    @Test
    @Disabled("Till we have the full set of TOOI dictionaries")
    void multiDictLocations() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var documentMeta = plooi.getDocumentMeta();
        documentMeta.publisher(new IdentifiedResource().id("https://identifier.overheid.nl/tooi/id/gemeente/gm0363"));
        documentMeta.verantwoordelijke(new IdentifiedResource().id("https://identifier.overheid.nl/tooi/id/oorg/oorg10002"));
        buildNormalizer("mapped_fields_test.properties", null).process(plooi);
        assertEquals("gemeente Amsterdam", documentMeta.getPublisher().getLabel());
        assertEquals("overheid:Gemeente", documentMeta.getPublisher().getType());
        assertEquals("", documentMeta.getPublisher().getBronwaarde());
        assertEquals("Tweede Kamer", documentMeta.getVerantwoordelijke().getLabel());
        assertEquals("overheid:AndereOrganisatie", documentMeta.getVerantwoordelijke().getType());
        assertEquals("", documentMeta.getPublisher().getBronwaarde());
        assertEquals(2, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.EMPTY_LABEL.toString(), plooi.status().getDiagnoses().get(0).getCode());
        assertEquals(DiagnosticCode.EMPTY_LABEL.toString(), plooi.status().getDiagnoses().get(1).getCode());
    }

    /**
     * PlooiDocumentValueNormalizer does not care about fields that are not linked to a dictionary. Validator might complain
     * about the missing value and identifier below.
     */
    @Test
    void notNormalized_withId() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var documentMeta = plooi.getDocumentMeta();
        documentMeta.language(new IdentifiedResource().id("http://publications.europa.eu/resource/authority/language/NLD"));
        NORMALIZER.process(plooi);
        assertNull(documentMeta.getLanguage().getLabel());
        assertTrue(plooi.status().getDiagnoses().isEmpty());
    }

    @Test
    void notNormalized_withLabel() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var documentMeta = plooi.getDocumentMeta();
        documentMeta.language(new IdentifiedResource().label("Nederlands"));
        NORMALIZER.process(plooi);
        assertNull(documentMeta.getLanguage().getId());
        assertTrue(plooi.status().getDiagnoses().isEmpty());
    }

    @Test
    void normalizeEmpty() {
        var plooi = new PlooiEnvelope("tst", "tst");
        NORMALIZER.process(plooi);
        assertTrue(plooi.status().getDiagnoses().isEmpty());
    }

    @Test
    void noIdUnknownValue() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().label("Dunno this one"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertTrue(PlooiDocumentValueNormalizer.isEmpty(thema));
        assertEquals(2, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.EMPTY_ID.name(), plooi.status().getDiagnoses().get(0).getCode());
        assertEquals(DiagnosticCode.UNKNOWN_LABEL.name(), plooi.status().getDiagnoses().get(1).getCode());
    }

    @Test
    void noIdKnownValue() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().label("Privaatrecht"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff", thema.getId());
        assertEquals("privaatrecht", thema.getLabel());
        assertNull(thema.getBronwaarde());
        assertEquals(1, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.EMPTY_ID.name(), plooi.status().getDiagnoses().get(0).getCode());
    }

    @Test
    void noIdSynonymValue() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().label("Burgerlijk recht"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff", thema.getId());
        assertEquals("privaatrecht", thema.getLabel());
        assertEquals("Burgerlijk recht", thema.getBronwaarde());
        assertEquals(2, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.EMPTY_ID.name(), plooi.status().getDiagnoses().get(0).getCode());
        assertEquals(DiagnosticCode.ALTLABEL.name(), plooi.status().getDiagnoses().get(1).getCode());
    }

    @Test
    void noIdSynonymInconsistent() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().label("Inconsistent recht"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertTrue(PlooiDocumentValueNormalizer.isEmpty(thema));
        assertEquals(2, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.EMPTY_ID.name(), plooi.status().getDiagnoses().get(0).getCode());
        assertEquals(DiagnosticCode.INCONSISTENT_ID.name(), plooi.status().getDiagnoses().get(1).getCode());
    }

    @Test
    void noExtraProps() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().label("Burgerlijk recht"));
        PlooiDocumentValueNormalizer.configure().build().process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertTrue(PlooiDocumentValueNormalizer.isEmpty(thema));
        assertEquals(2, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.EMPTY_ID.name(), plooi.status().getDiagnoses().get(0).getCode());
        assertEquals(DiagnosticCode.UNKNOWN_LABEL.name(), plooi.status().getDiagnoses().get(1).getCode());
    }

    @Test
    void uknownId() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().id("https://identifier.overheid.nl/tooi/def/thes/top/UNKNOWN").label("Privaatrecht"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertTrue(PlooiDocumentValueNormalizer.isEmpty(thema));
        assertEquals(1, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.UNKNOWN_ID.name(), plooi.status().getDiagnoses().get(0).getCode());
    }

    @Test
    void idNoValue() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().id("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff", thema.getId());
        assertEquals("privaatrecht", thema.getLabel());
        assertNull(thema.getBronwaarde());
        assertEquals(1, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.EMPTY_LABEL.name(), plooi.status().getDiagnoses().get(0).getCode());
    }

    @Test
    void idDiffValue() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().id("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff").label("Water"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff", thema.getId());
        assertEquals("privaatrecht", thema.getLabel());
        assertEquals("Water", thema.getBronwaarde());
        assertEquals(1, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.DIFF_LABEL.name(), plooi.status().getDiagnoses().get(0).getCode());
    }

    @Test
    void idOkValue() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().id("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff").label("Privaatrecht"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff", thema.getId());
        assertEquals("privaatrecht", thema.getLabel());
        assertNull(thema.getBronwaarde());
        assertTrue(plooi.status().getDiagnoses().isEmpty());
    }

    @Test
    void idSynonymValue() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().id("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff").label("Burgerlijk recht"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff", thema.getId());
        assertEquals("privaatrecht", thema.getLabel());
        assertEquals("Burgerlijk recht", thema.getBronwaarde());
        assertEquals(1, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.ALTLABEL.name(), plooi.status().getDiagnoses().get(0).getCode());
    }

    @Test
    void idSynonymInconsistent() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().id("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff").label("Inconsistent recht"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/top/c_824ce7ff", thema.getId());
        assertEquals("privaatrecht", thema.getLabel());
        assertEquals("Inconsistent recht", thema.getBronwaarde());
        assertEquals(1, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.INCONSISTENT_LABEL.name(), plooi.status().getDiagnoses().get(0).getCode());
    }

    @Test
    void remappedSynonym() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties.addThemasItem(new IdentifiedResource().label("Scheepvaart"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getThemas().isEmpty());
        var thema = classificaties.getThemas().get(0);
        // Scheepvaaart has id c_d3b599f8 in the TOOI lijst, but is remapped to Economie in the mapping;
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/top/c_d363776f", thema.getId());
        assertEquals("economie", thema.getLabel());
        assertEquals("Scheepvaart", thema.getBronwaarde());
        assertEquals(2, plooi.status().getDiagnoses().size());
        assertEquals(DiagnosticCode.EMPTY_ID.name(), plooi.status().getDiagnoses().get(0).getCode());
        assertEquals(DiagnosticCode.ALTLABEL.name(), plooi.status().getDiagnoses().get(1).getCode());
    }

    @Test
    void missingScheme() {
        var plooi = new PlooiEnvelope("tst", "tst");
        var classificaties = plooi.getClassificatiecollectie();
        classificaties
                .addDocumentsoortenItem(new IdentifiedResource().id("https://identifier.overheid.nl/tooi/def/thes/kern/c_fbaa7e4b").label("beleidsregel"));
        NORMALIZER.process(plooi);
        assertFalse(classificaties.getDocumentsoorten().isEmpty());
        var docSoort = classificaties.getDocumentsoorten().get(0);
        assertEquals("https://identifier.overheid.nl/tooi/def/thes/kern/c_fbaa7e4b", docSoort.getId());
        assertEquals("beleidsregel", docSoort.getLabel());
        assertEquals("overheid:Informatietype", docSoort.getType());
        assertNull(docSoort.getBronwaarde());
        assertTrue(plooi.status().getDiagnoses().isEmpty());
    }
}

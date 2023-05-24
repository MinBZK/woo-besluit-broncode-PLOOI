package nl.overheid.koop.plooi.dcn.ronl.api;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripTrailingSpaces;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.ronl.RonlShared;
import nl.overheid.koop.plooi.document.normalize.BeslisnotaDetector;
import nl.overheid.koop.plooi.document.normalize.PlooiDocumentValidator;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RonlApiNormalizationTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    void mapNoFiles() throws IOException {
        PlooiEnvelope mappedDoc = map(new PlooiEnvelope(RonlApiRoute.SOURCE_LABEL, "02dbce21-c2a6-4310-9ed5-aab7b27467ac"),
                readFileAsString(getClass(), "no_files/02dbce21-c2a6-4310-9ed5-aab7b27467ac.xml"));
        assertEquals(
                readFileAsString(getClass(), "no_files/1df1e6db16b1cb344ec81fcd79a25d45f9c47cb7_mapped.json"),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(mappedDoc.getPlooi()))));
        assertEquals(
                "Documentsoort:ALTLABEL Thema:REQUIRED_DEFAULT",
                mappedDoc.status().getDiagnosticSummary());
        assertEquals("Inleidende verklaring viceminister-president De Jonge: "
                + "Goedemiddag. Minister-president Rutte is op dit moment in Göteborg voor een Europese Sociale Top. En dat is de reden dat ik vandaag in zijn afwezigheid de ministerraad heb voorgezeten. En dat is dus ook de reden dat ik hier vanmiddag voor u sta. Dat was mijn eerste keer, overigens. Een bijzonder moment. En dit is ook mijn eerste keer en een even bijzonder moment, natuurlijk. Dat begrijpt u.\n"
                + " Laat ik beginnen met terug te blikken op de deze week gepubliceerde CBS-cijfers. Want uit die cijfers over het derde kwartaal blijkt dat de Nederlandse economie gestaag blijft groeien. Die groei, die leidt tot meer banen. Tot een afnemende werkloosheid. Een grotere daling van het aantal WW-uitkeringen. Min achttien procent in één jaar. En verder blijkt dat de arbeidsmarkt geleidelijk aan wat krapper wordt en we zien daardoor een toename van het aantal vaste contracten. En dat is goed nieuws. Voor heel veel mensen goed nieuws. En economische groei, die heeft pas echt betekenis natuurlijk als mensen daar ook wat van merken. En juist ook in dat opzicht zijn dit bemoedigende signalen.\n"
                + " En dat mensen zelf moeten merken dat het beter gaat, zelf moeten ervaren dat het beter gaat, is een belangrijke drijfveer voor dit kabinet. En daar zijn in het regeerakkoord meerdere maatregelen voor genomen, ook op mijn terrein. Op het terrein van de zorg. Ik zie dat veel mensen onzeker zijn of ze de zorg en de ondersteuning die zij nodig hebben eigenlijk nog wel kunnen betalen. Dat die wel beschikbaar blijft, betaalbaar blijft. En als kabinet nemen we daarom ook gerichte maatregelen om de stapeling van eigen bijdragen in de zorg te verminderen. En in een brief die vandaag naar de Tweede Kamer gaat, hebben we die maatregelen op een rij gezet. Wat mensen daar in 2018 en in 2019 van gaan merken. We zetten namelijk al in 2018 de eerste stappen, door het verplicht eigen risico in de zorgverzekeringswet te bevriezen en door de eigen bijdrage voor de langdurige zorg te verlagen voor mensen die blijvend intensieve zorg nodig hebben aan huis of zelf in een verpleeghuis wonen terwijl hun partner nog thuis woont. En dat is een belangrijke maatregel. Die treft 30.000 mensen die erop vooruitgaan. Dat zijn vooral mensen met een middeninkomen, die er per maand 30 tot 150 euro minder zorgkosten aan gaan betalen.\n"
                + " In 2019 gaan we verder met de uitvoering van overige voornemens ten aanzien van de eigen betalingen uit het regeerakkoord op dit terrein, en daardoor – en dat is belangrijk – blijven de zorg en ondersteuning betaalbaar voor mensen die te maken hebben met verschillende soorten eigen betalingen in de zorg. Dan een ander onderwerp dat aan de orde is geweest vandaag. Dat zijn de investeringen in Defensie. Het kabinet heeft vandaag de eerste stap gezet met de extra intensiveringen die in het regeerakkoord zijn opgenomen. En we maken nu 400 miljoen vrij voor de komende begroting van Defensie. En met dat geld kunnen we knelpunten zoals die ervaren worden bij Defensie aanpakken. En dat is ook hard nodig. Daarmee wordt de operationele inzetbaarheid van de krijgsmacht versterkt. Minister Bijleveld heeft daar zojuist al een aantal van uw collega’s te woord gestaan bij het naar buiten lopen van de ministerraad.\n"
                + "\n"
                + "Afschaffing referendumwet\n"
                + "\n"
                + " BORGMAN (RADIO 1) \n"
                + "Meneer De Jonge, deze week was in de Tweede Kamer het debat over de begroting Binnenlandse Zaken. Daarbij kwam aan de orde de afschaffing van de referendumwet. Is het voor het kabinet wenselijk dat een referendum wordt gehouden over de afschaffing van die wet?\n"
                + " DE JONGE \n"
                + "Collega Ollongren heeft in dat debat aangegeven dat zij werkt aan een voorstel voor die intrekkingswet. Ze heeft ook aangegeven dat dit element daar, of de intrekking an sich referendabel zou moeten zijn, dat die onderdeel wordt van dat voorstel, omdat zij eraan hecht om dat te bespreken met de collega’s in het kabinet. En die bespreking heeft nog niet plaatsgevonden, dus ik wil op die bespreking ook maar even niet vooruitlopen.\n"
                + " BORGMAN \n"
                + "...", mappedDoc.getDocumentText());
    }

    @Test
    void mapSingleFile() throws IOException {
        PlooiEnvelope mappedDoc = map(new PlooiEnvelope(RonlApiRoute.SOURCE_LABEL, "5afce6ec-a4ed-4c62-a6d5-168a59247678"),
                readFileAsString(getClass(), "single_file/5afce6ec-a4ed-4c62-a6d5-168a59247678.xml"));
        assertEquals(
                readFileAsString(getClass(), "single_file/16bac8e0eb30525cc2655d53d4fb50b5acabfb30_mapped.json"),
                stripWindowsCR(PlooiBindings.plooiBinding().marshalToString(mappedDoc.getPlooi())));
        assertEquals(
                "Thema:REQUIRED_DEFAULT",
                mappedDoc.status().getDiagnosticSummary());
        assertTrue(mappedDoc.getDocumentText().isEmpty());
    }

    @Test
    void mapSingleFileNotPDF() throws IOException {
        PlooiEnvelope mappedDoc = map(new PlooiEnvelope(RonlApiRoute.SOURCE_LABEL, "3cd2285c-b7bc-46e7-9fcf-9bf3c9a3c20d"),
                readFileAsString(getClass(), "single_file_notpdf/3cd2285c-b7bc-46e7-9fcf-9bf3c9a3c20d.xml"));
        assertEquals(
                readFileAsString(getClass(), "single_file_notpdf/f216053aec4c0753c51329404a34e3e3d31c7065_mapped.json"),
                stripWindowsCR(stripTrailingSpaces(PlooiBindings.plooiBinding().marshalToString(mappedDoc.getPlooi()))));
        assertEquals(
                "Thema:ALTLABEL Documentsoort:REQUIRED_DEFAULT",
                mappedDoc.status().getDiagnosticSummary());
        assertEquals("Dit zip-bestand bevat de volgende documenten:\n"
                + "\n"
                + "\n"
                + " DA Pamphlet 205_1 Energy Conservation Guidelines CH Warehouses\n"
                + " rapport Nationale MAC Commissie grenswaarde Chroom en Chroomverbindingen 1985"
                + " Documenten uit andere jaren "
                + "Kijk voor een pdf-bestand met een lijst van alle documenten over CARC en chroom-6 in het "
                + "overzichtsdocument publicaties. De documenten zelf vindt u via "
                + "de pagina's met documenten per jaartal.", mappedDoc.getDocumentText());
    }

    @Test
    void mapBundle() throws IOException {
        PlooiEnvelope mappedDoc = map(new PlooiEnvelope(RonlApiRoute.SOURCE_LABEL, "0acb97b8-b3cf-4654-92c2-0fa4199c100c"),
                readFileAsString(getClass(), "bundle/0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml"));
        assertEquals(
                readFileAsString(getClass(), "bundle/228036432630c580dcdddefd9c3a6de8e2587c1b_mapped.json"),
                stripWindowsCR(PlooiBindings.plooiBinding().marshalToString(mappedDoc.getPlooi())));
        assertEquals(
                "Documentsoort:REQUIRED_DEFAULT Thema:REQUIRED_DEFAULT",
                mappedDoc.status().getDiagnosticSummary());
        assertTrue(mappedDoc.getDocumentText().isEmpty());
    }

    @Test
    void mapBundle_wobverzoek() throws IOException {
        PlooiEnvelope mappedDoc = map(new PlooiEnvelope(RonlApiRoute.SOURCE_LABEL, "b47815af-2b19-4268-a961-58b9591f4477"),
                readFileAsString(getClass(), "bundle_wobverzoek/b47815af-2b19-4268-a961-58b9591f4477.xml"));
        assertEquals(
                readFileAsString(getClass(), "bundle_wobverzoek/da6e58c6e8ea4fea47cedfaaec0368567464976b_mapped.json"),
                stripWindowsCR(PlooiBindings.plooiBinding().marshalToString(mappedDoc.getPlooi())));
        assertEquals(
                "Documentsoort:ALTLABEL Thema:REQUIRED_DEFAULT",
                mappedDoc.status().getDiagnosticSummary());
        assertTrue(mappedDoc.getDocumentText().isEmpty());
    }

    static PlooiEnvelope map(PlooiEnvelope plooi, String ronlXml) throws IOException {
        try (InputStream ronlStream = new ByteArrayInputStream(ronlXml.getBytes(StandardCharsets.UTF_8))) {
            RonlShared.RONL_DIAGNOSTIC_FILTER.process(plooi);
            RonlApiRoute.RONL_META_MAPPER.populate(ronlStream, plooi);
            BeslisnotaDetector.handleBeslisnota(plooi);
            RonlShared.RONL_NORMALIZER.process(plooi);
            new PlooiDocumentValidator().process(plooi);
            return plooi;
        }
    }
}

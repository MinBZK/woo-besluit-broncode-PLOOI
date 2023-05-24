package nl.overheid.koop.plooi.dcn.ronl.api;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.stripWindowsCR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.List;
import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RonlApiDocumentCollectorTest {

    public final RonlDocumentCollector ronlCollector = new RonlDocumentCollector(RonlApiRoute.SOURCE_LABEL, true);

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    // TODO: Re-enable after this code moved into repository-service
    @DisabledOnOs(value = { OS.WINDOWS }, disabledReason = """
            see: DCN-Components - PlooiFile#fixup
            fixup method is using Files.probeContentType which is not OS-agnostic thus behaves different in Windows and *nix systems

            Java NIO bug: https://bugs.openjdk.org/browse/JDK-8186071
            """)
    @ParameterizedTest
    @CsvSource({
            // No Files,
            // see http://solrcloud-lb-tst.overheid.nl:8983/solr/plooi/select?q=id:ronl-a94cdd62-7926-42c1-a0ae-4ad565045b85_1
            "no_files/02dbce21-c2a6-4310-9ed5-aab7b27467ac.xml, no_files/1df1e6db16b1cb344ec81fcd79a25d45f9c47cb7_collected.json, Letterlijke tekst persconferentie na ministerraad 17 november 2017",
            // Single File,
            // see http://solrcloud-lb-tst.overheid.nl:8983/solr/plooi/select?q=id:ronl-81610edf-9bfe-4465-8c7d-8220c6aca4f5_1
            "single_file/5afce6ec-a4ed-4c62-a6d5-168a59247678.xml, single_file/16bac8e0eb30525cc2655d53d4fb50b5acabfb30_collected.json, Advies nieuwe media",
            // Not PDF,
            "single_file_notpdf/3cd2285c-b7bc-46e7-9fcf-9bf3c9a3c20d.xml, single_file_notpdf/f216053aec4c0753c51329404a34e3e3d31c7065_collected.json, Documenten uit 1985 over CARC en chroom-6",
            // Linked to plooi,
            "linked_to_plooi_pdf/cb0edf75-382a-4048-82bb-6ae83aa615ce.xml, linked_to_plooi_pdf/cb0edf75-382a-4048-82bb-6ae83aa615ce_discarded.json, Antwoord van Kenniscoalitie Optimale synergie tussen het\r\n        missiegedreven topsectoren- en innovatiebeleid en de NWA",

    })
    void collectSinglePlooiDoc(String mapFile, String expectFile, String title) throws IOException {
        var plooiDocs = mapAndCollect(mapFile, title);
        assertEquals(1, plooiDocs.size());
        assertEquals(
                readFileAsString(getClass(), expectFile),
                stripWindowsCR(PlooiBindings.filesBinding().marshalToString(plooiDocs.get(0).getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
        // Assertion for discarded files
        if (expectFile.contains("discarded")) {
            assertTrue(!plooiDocs.get(0).status().getDiagnoses().isEmpty());
        }
    }

    // TODO: Re-enable after this code moved into repository-service
    @DisabledOnOs(value = { OS.WINDOWS }, disabledReason = """
            see: DCN-Components - PlooiFile#fixup
            fixup method is using Files.probeContentType which is not OS-agnostic thus behaves different in Windows and *nix systems

            Java NIO bug: https://bugs.openjdk.org/browse/JDK-8186071
            """)
    // see http://solrcloud-lb-tst.overheid.nl:8983/solr/plooi/select?q=id:ronl-1cb0d2ed-551f-4256-ae72-b8b0ddcbe9d3_1
    // and http://solrcloud-lb-tst.overheid.nl:8983/solr/plooi/select?q=id:ronl-c90cd629-94e9-482c-9233-070e05c5335f_1
    @Test
    void collectBundle() throws IOException {
        var plooiDocs = mapAndCollect("bundle/0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml", "Akte van aanstelling 001474");
        assertEquals(3, plooiDocs.size());
        assertEquals(
                readFileAsString(getClass(), "bundle/228036432630c580dcdddefd9c3a6de8e2587c1b_collected.json"),
                stripWindowsCR(PlooiBindings.filesBinding().marshalToString(plooiDocs.get(0).getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
        assertEquals(
                readFileAsString(getClass(), "bundle/228036432630c580dcdddefd9c3a6de8e2587c1b_relations.json")
                        .replaceAll("https://test.open.overheid.nl/documenten/", ""),
                stripWindowsCR(PlooiBindings.relationsBinding().marshalToString(plooiDocs.get(0).getRelationsForStage())));
        assertEquals(
                readFileAsString(getClass(), "bundle/684333ccbc1afcdb275529c54261dff4061180ea_collected.json"),
                stripWindowsCR(PlooiBindings.filesBinding().marshalToString(plooiDocs.get(1).getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
        assertEquals(
                readFileAsString(getClass(), "bundle/684333ccbc1afcdb275529c54261dff4061180ea_relations.json"),
                stripWindowsCR(PlooiBindings.relationsBinding().marshalToString(plooiDocs.get(1).getRelationsForStage())));
        assertEquals(
                readFileAsString(getClass(), "bundle/a83c9806d87ce33e632c253eb2166cc937f0bef1_collected.json"),
                stripWindowsCR(PlooiBindings.filesBinding().marshalToString(plooiDocs.get(2).getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
        assertEquals(
                readFileAsString(getClass(), "bundle/a83c9806d87ce33e632c253eb2166cc937f0bef1_relations.json"),
                stripWindowsCR(PlooiBindings.relationsBinding().marshalToString(plooiDocs.get(2).getRelationsForStage())));
    }

    // TODO: Re-enable after this code moved into repository-service
    @DisabledOnOs(value = { OS.WINDOWS }, disabledReason = """
            see: DCN-Components - PlooiFile#fixup
            fixup method is using Files.probeContentType which is not OS-agnostic thus behaves different in Windows and *nix systems

            Java NIO bug: https://bugs.openjdk.org/browse/JDK-8186071
            """)
    // see http://solrcloud-lb-tst.overheid.nl:8983/solr/plooi/select?q=id:ronl-4d40fb7a-0a78-433a-a000-b973ff9a5bd9_1
    // and http://solrcloud-lb-tst.overheid.nl:8983/solr/plooi/select?q=id:ronl-72768aa6-b842-40d7-b9dd-a4b7f005e33f_1 (pdf)
    // and http://solrcloud-lb-tst.overheid.nl:8983/solr/plooi/select?q=id:ronl-e11ca13c-6228-47d3-8a2a-2622df1b0eab_1 (ods)
    // Test data however is a modified of the above
    @Test
    void collectBundle_noPDFs() throws IOException {
        var plooiDocs = mapAndCollect("bundle_nopdf/0acb97b8-b3cf-4654-92c2-0fa4199c100c.xml", "Akte van aanstelling 001474");
        assertEquals(3, plooiDocs.size());
        assertEquals(
                readFileAsString(getClass(), "bundle_nopdf/228036432630c580dcdddefd9c3a6de8e2587c1b_collected.json"),
                stripWindowsCR(PlooiBindings.filesBinding().marshalToString(plooiDocs.get(0).getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
        assertEquals(
                readFileAsString(getClass(), "bundle_nopdf/228036432630c580dcdddefd9c3a6de8e2587c1b_relations.json"),
                stripWindowsCR(PlooiBindings.relationsBinding().marshalToString(plooiDocs.get(0).getRelationsForStage())));
        assertEquals(
                readFileAsString(getClass(), "bundle_nopdf/684333ccbc1afcdb275529c54261dff4061180ea_collected.json"),
                stripWindowsCR(PlooiBindings.filesBinding().marshalToString(plooiDocs.get(1).getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
        assertEquals(
                readFileAsString(getClass(), "bundle_nopdf/684333ccbc1afcdb275529c54261dff4061180ea_relations.json"),
                stripWindowsCR(PlooiBindings.relationsBinding().marshalToString(plooiDocs.get(1).getRelationsForStage())));
        assertEquals(
                readFileAsString(getClass(), "bundle_nopdf/86b84179af44e7b4db60962d71117bfbfb0469fe_collected.json"),
                stripWindowsCR(PlooiBindings.filesBinding().marshalToString(plooiDocs.get(2).getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
        assertEquals(
                readFileAsString(getClass(), "bundle_nopdf/86b84179af44e7b4db60962d71117bfbfb0469fe_relations.json"),
                stripWindowsCR(PlooiBindings.relationsBinding().marshalToString(plooiDocs.get(2).getRelationsForStage())));
    }

    private List<DeliveryEnvelope> mapAndCollect(String mapFile, String title) throws IOException {
        var plooiFile = RonlApiRoute.RONL_FILEMAPPER.populate(readFileAsString(getClass(), mapFile));
        var plooiDocs = this.ronlCollector.collect(plooiFile);
        return plooiDocs;
    }
}

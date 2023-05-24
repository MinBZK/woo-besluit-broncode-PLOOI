package nl.overheid.koop.plooi.dcn.component.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.List;
import nl.overheid.koop.plooi.dcn.component.common.DefaultDocumentCollector;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.Relatie;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.model.data.util.PlooiFileUtil;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import nl.overheid.koop.plooi.repository.data.RelationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultDocumentCollectorTest {

    @Mock
    private PublicatieClient publicatieClient;

    @BeforeEach
     void init() {
        when(this.publicatieClient.getRelations(anyString(), any()))
            .thenReturn(List.of())
            .thenReturn(List.of(new Relatie().role(RelationType.BUNDEL.getUri())));
    }

    @Test
    void collect_nobundle() {
        var metaFile = new PlooiFile();
        metaFile.getFile().url("http://example.com/meta.xml").id("bundleId");
        var pdfFile = metaFile.newChild();
        pdfFile.getFile().url("http://example.com/document.pdf");
        pdfFile.getFile().gepubliceerd(true);

        var deliveryEnvelopes = new DefaultDocumentCollector("tst", true).collect(metaFile);
        assertEquals(1, deliveryEnvelopes.size());
        var plooiDoc = deliveryEnvelopes.get(0);
        assertTrue(plooiDoc.getRelationsForStage().isEmpty());
        assertEquals(2, plooiDoc.getPlooiFiles().size());
        assertEquals("http://example.com/meta.xml", plooiDoc.getPlooiFiles().get(0).getFile().getUrl());
        assertEquals("http://example.com/document.pdf", plooiDoc.getPlooiFiles().get(1).getFile().getUrl());

        var plooiEnv = deliveryToPlooiEnvelope(deliveryEnvelopes.get(0));
        plooiEnv.getTitelcollectie().setOfficieleTitel("Bundle Title");
        // postCollect
        plooiEnv.fixBundlePartTitle(this.publicatieClient);

        assertEquals("Bundle Title", plooiEnv.getTitelcollectie().getOfficieleTitel());
    }

    @Test
    void collect_bundle_notsplit() {
        // collect
        var metaFile = new PlooiFile();
        metaFile.getFile().url("http://example.com/meta.xml").id("bundleId");
        var pdf1File = metaFile.newChild();
        pdf1File.getFile().url("http://example.com/document1.pdf");
        pdf1File.getFile().gepubliceerd(true);
        var pdf2File = metaFile.newChild();
        pdf2File.getFile().url("http://example.com/document2.pdf");
        pdf2File.getFile().gepubliceerd(true);

        var deliveryEnvelopes = new DefaultDocumentCollector("tst", false).collect(metaFile);
        assertEquals(1, deliveryEnvelopes.size());
        var plooiDoc = deliveryEnvelopes.get(0);
        assertTrue(plooiDoc.getRelationsForStage().isEmpty());
        assertEquals(3, plooiDoc.getPlooiFiles().size());
        assertEquals("http://example.com/meta.xml", plooiDoc.getPlooiFiles().get(0).getFile().getUrl());
        assertEquals("http://example.com/document1.pdf", plooiDoc.getPlooiFiles().get(1).getFile().getUrl());
        assertEquals("http://example.com/document2.pdf", plooiDoc.getPlooiFiles().get(2).getFile().getUrl());

        var plooiEnv = deliveryToPlooiEnvelope(deliveryEnvelopes.get(0));
        plooiEnv.getTitelcollectie().setOfficieleTitel("Bundle Title");
        // postCollect
        plooiEnv.fixBundlePartTitle(this.publicatieClient);

        assertEquals("Bundle Title", plooiEnv.getTitelcollectie().getOfficieleTitel());
    }

    @Test
    void collect_bundle_split() {
        // collect
        var metaFile = new PlooiFile();
        metaFile.getFile().url("http://example.com/meta.xml").id("bundleId");
        PlooiFileUtil.populate(metaFile.getFile());
        var pdf1File = metaFile.newChild();
        pdf1File.getFile().url("http://example.com/document1.pdf");
        pdf1File.getFile().gepubliceerd(true);
        PlooiFileUtil.populate(pdf1File.getFile());
        var pdf2File = metaFile.newChild();
        pdf2File.getFile().url("http://example.com/document2.pdf");
        pdf2File.getFile().gepubliceerd(true);
        PlooiFileUtil.populate(pdf2File.getFile());

        var deliveryEnvelopes = new DefaultDocumentCollector("tst", true).collect(metaFile);
        assertEquals(3, deliveryEnvelopes.size());

        var bundleDoc = deliveryEnvelopes.get(0);
        assertEquals(2, bundleDoc.getRelationsForStage().size());
        assertEquals(RelationType.ONDERDEEL.getUri(), bundleDoc.getRelationsForStage().get(0).getRole());
        assertEquals("tst-8016ad970e4ebf26fd791f0bc8fedc0d6d357687", bundleDoc.getRelationsForStage().get(0).getRelation());
        assertEquals(RelationType.ONDERDEEL.getUri(), bundleDoc.getRelationsForStage().get(1).getRole());
        assertEquals(1, bundleDoc.getPlooiFiles().size());
        assertEquals("http://example.com/meta.xml", bundleDoc.getPlooiFiles().get(0).getFile().getUrl());

        var partDoc = deliveryEnvelopes.get(1);
        assertEquals(1, partDoc.getRelationsForStage().size());
        assertEquals(RelationType.BUNDEL.getUri(), partDoc.getRelationsForStage().get(0).getRole());
        assertEquals("tst-d5b6be3b3e39680b64846f809d1140ab1cfa9f97", partDoc.getRelationsForStage().get(0).getRelation());
        assertEquals("http://example.com/meta.xml", partDoc.getPlooiFiles().get(0).getFile().getUrl());
        assertEquals("http://example.com/document1.pdf", partDoc.getPlooiFiles().get(1).getFile().getUrl());

        var bundleEnv = deliveryToPlooiEnvelope(bundleDoc);
        var partEnv = deliveryToPlooiEnvelope(partDoc);
        bundleEnv.getTitelcollectie().setOfficieleTitel("Bundle Title");
        partEnv.getTitelcollectie().setOfficieleTitel("Bundle Title");
        // postCollect
        bundleEnv.fixBundlePartTitle(this.publicatieClient);
        partEnv.fixBundlePartTitle(this.publicatieClient);

        assertEquals("Bundle Title", bundleEnv.getTitelcollectie().getOfficieleTitel());
        assertEquals("Bundle Title - document1.pdf", partEnv.getTitelcollectie().getOfficieleTitel());
    }

    static PlooiEnvelope deliveryToPlooiEnvelope(DeliveryEnvelope input) {
        var plooi = new Plooi().plooiIntern(input.getPlooiIntern());
        plooi.versies(List.of(new Versie().nummer(1).bestanden(input.getPlooiFiles().stream().map(PlooiFile::getFile).toList())));
        var output = new PlooiEnvelope(plooi);
        input.status().getDiagnoses().forEach(d -> output.status().addDiagnose(d));
        input.getRelationsForStage().forEach(r -> output.addRelation(r));
        return output;
    }
}

package nl.overheid.koop.plooi.dcn.component.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.dcn.model.IdentityGroup;
import nl.overheid.koop.plooi.dcn.model.IdentityGroupRepository;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Versie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IdentityGroupProcessingTest {

    @Mock(lenient = true)
    private IdentityGroupRepository identityGroupRepository;
    private IdentityGroupProcessing identityGroupProcessing;

    @BeforeEach
    void init() {
        when(this.identityGroupRepository.save(any(IdentityGroup.class))).thenAnswer(invocation -> invocation.getArgument(0, IdentityGroup.class));
        this.identityGroupProcessing = new IdentityGroupProcessing(this.identityGroupRepository);
    }

    @Test
    void fileWithoutDocumentTest() {
        var plooi = new PlooiEnvelope("test", "9495beca-ff69-4005-bb29-ffffffffffff");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("meta.json").label("meta").hash("test")));
        this.identityGroupProcessing.process(plooi);
        assertTrue(plooi.getRelationsForStage().isEmpty());
    }

    @Test
    void fileWithDocumentTest() {
        var plooi = new PlooiEnvelope("test", "9495beca-ff69-4005-bb29-ffffffffffff");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("meta.json").label("meta").hash("test"))
                        .addBestandenItem(new Bestand().bestandsnaam("document.pdf").label("pdf").hash("test").gepubliceerd(true)));
        this.identityGroupProcessing.process(plooi);
        assertEquals(1, plooi.getRelationsForStage().size());
    }

    @Test
    void fileWithDocumentTestNoContent() {
        var plooi = new PlooiEnvelope("test", "9495beca-ff69-4005-bb29-ffffffffffff");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("meta.json").label("meta").hash("test"))
                        .addBestandenItem(new Bestand().bestandsnaam("document.pdf").label("pdf").gepubliceerd(true)));
        this.identityGroupProcessing.process(plooi);
        assertTrue(plooi.getRelationsForStage().isEmpty());
    }

    @Test
    void fileWithDocumentTestNotPublished() {
        var plooi = new PlooiEnvelope("test", "9495beca-ff69-4005-bb29-ffffffffffff");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("meta.json").label("meta").hash("test"))
                        .addBestandenItem(new Bestand().bestandsnaam("document.pdf").label("pdf").hash("test")));
        this.identityGroupProcessing.process(plooi);
        assertTrue(plooi.getRelationsForStage().isEmpty());
    }

    @Test
    void withMultiDocumentsTest() {
        var plooi = new PlooiEnvelope("test", "9495beca-ff69-4005-bb29-ffffffffffff");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("meta.json").label("meta").hash("test"))
                        .addBestandenItem(new Bestand().bestandsnaam("document1.pdf").label("doc1").hash("test1").gepubliceerd(true))
                        .addBestandenItem(new Bestand().bestandsnaam("document2.pdf").label("doc2").hash("test2").gepubliceerd(true)));
        this.identityGroupProcessing.process(plooi);
        assertEquals(1, plooi.getRelationsForStage().size());
    }

    @Test
    void withMultiFilesOneHasContentTest() {
        var plooi = new PlooiEnvelope("test", "9495beca-ff69-4005-bb29-ffffffffffff");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("meta.json").label("meta").hash("test1"))
                        .addBestandenItem(new Bestand().bestandsnaam("document1.pdf").label("doc1").gepubliceerd(true))
                        .addBestandenItem(new Bestand().bestandsnaam("document2.pdf").label("doc2").hash("test").gepubliceerd(true)));
        this.identityGroupProcessing.process(plooi);
        assertEquals(1, plooi.getRelationsForStage().size());
    }
}

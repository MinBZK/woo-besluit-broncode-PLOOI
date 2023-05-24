package nl.overheid.koop.plooi.repository.idmapping;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static nl.overheid.koop.plooi.test.util.TestUtils.resolve;
import static org.junit.jupiter.api.Assertions.assertEquals;
import nl.overheid.koop.plooi.model.data.Document;
import nl.overheid.koop.plooi.model.data.Plooi;
import nl.overheid.koop.plooi.model.data.PlooiIntern;
import nl.overheid.koop.plooi.model.data.util.DcnIdentifierUtil;
import nl.overheid.koop.plooi.model.data.util.PlooiBinding;
import nl.overheid.koop.plooi.model.data.util.PlooiBindings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IdentifierMappingTest {

    private final PlooiBinding<Plooi> plooiBinding = PlooiBindings.plooiBinding();
    private final IdentifierMapping identifierMapping = new IdentifierMapping(null);

    @BeforeEach
    void init() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }

    @Test
    void toDcn_api() {
        assertEquals("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a",
                this.identifierMapping.toDcn("b36dda59-1b79-4893-882e-45032b0b728a"));
        assertEquals("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a",
                this.identifierMapping.toDcn("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a"));
    }

    @Test
    void toDcn_mapped() {
        assertEquals("ronl-92c663168bacd5f1bb0b6053818c2632e3de01a5",
                this.identifierMapping.toDcn("ronl-38915c0e-5015-490a-b886-4ef6bb5248d7"));
    }

    @Test
    void toDcn_unmapped() {
        assertEquals("ronl-92c663168bacd5f1bb0b6053818c2632e3de01a5",
                this.identifierMapping.toDcn("ronl-92c663168bacd5f1bb0b6053818c2632e3de01a5"));
    }

    @Test
    void mapPid_api() {
        assertEquals("https://open.overheid.nl/documenten/b36dda59-1b79-4893-882e-45032b0b728a",
                this.identifierMapping
                        .mapPid(new Plooi()
                                .document(new Document())
                                .plooiIntern(
                                        new PlooiIntern()
                                                .sourceLabel(DcnIdentifierUtil.PLOOI_API_SRC)
                                                .dcnId("plooi-api-b36dda59-1b79-4893-882e-45032b0b728a")))
                        .getDocument()
                        .getPid());
    }

    @Test
    void mapPid_mapped() {
        assertEquals("https://open.overheid.nl/documenten/ronl-38915c0e-5015-490a-b886-4ef6bb5248d7",
                this.identifierMapping
                        .mapPid(new Plooi()
                                .document(new Document())
                                .plooiIntern(new PlooiIntern()
                                        .sourceLabel("ronl")
                                        .dcnId("ronl-92c663168bacd5f1bb0b6053818c2632e3de01a5")))
                        .getDocument()
                        .getPid());
    }

    @Test
    void mapPid_unmapped() {
        assertEquals("https://open.overheid.nl/documenten/ronl-92c663168bacd5f1bb0b6053818c263-notthere",
                this.identifierMapping
                        .mapPid(new Plooi()
                                .document(new Document())
                                .plooiIntern(new PlooiIntern()
                                        .sourceLabel("ronl")
                                        .dcnId("ronl-92c663168bacd5f1bb0b6053818c263-notthere")))
                        .getDocument()
                        .getPid());
    }

    @ParameterizedTest
    @CsvSource({
            "plooi_unmapped.json,   plooi_unmapped.json",
            "plooi_nofile_out.json, plooi_nofile_in.json",
            "plooi_bundle_out.json, plooi_bundle_in.json",
    })
    void mapId(String expectPlooi, String plooiIn) {
        var plooi = this.plooiBinding.unmarshalFromFile(resolve(getClass(), plooiIn).getPath());
        assertEquals(
                readFileAsString(getClass(), expectPlooi),
                this.plooiBinding.marshalToString(this.identifierMapping.mapPid(plooi)));
    }
}

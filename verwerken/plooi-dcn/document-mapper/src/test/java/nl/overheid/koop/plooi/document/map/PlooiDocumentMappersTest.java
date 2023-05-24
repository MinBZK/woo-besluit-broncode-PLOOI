package nl.overheid.koop.plooi.document.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import nl.overheid.koop.plooi.dcn.component.types.PlooiEnvelope;
import nl.overheid.koop.plooi.model.data.Bestand;
import nl.overheid.koop.plooi.model.data.Versie;
import nl.overheid.koop.plooi.repository.client.PublicatieClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlooiDocumentMappersTest {

    private static final PlooiMapping XML_MAPPING = new PlooiXmlMapping();

    @Mock(lenient = true)
    private PublicatieClient publicatieClient;

    @BeforeEach
    public void init() {
        when(this.publicatieClient.getVersionedFile(anyString(), anyString(), eq("xml"))).thenReturn(new ByteArrayInputStream("<metadata>xml</metadata>".getBytes(StandardCharsets.UTF_8)));
        when(this.publicatieClient.getVersionedFile(anyString(), anyString(), eq("pdf"))).thenReturn(new ByteArrayInputStream("<text>pdf</text>".getBytes(StandardCharsets.UTF_8)));
        when(this.publicatieClient.getVersionedFile(anyString(), anyString(), eq("doc"))).thenReturn(new ByteArrayInputStream("<text>doc</text>".getBytes(StandardCharsets.UTF_8)));
        when(this.publicatieClient.getVersionedFile(anyString(), anyString(), eq("csv"))).thenReturn(new ByteArrayInputStream("<text>csv</text>".getBytes(StandardCharsets.UTF_8)));
        when(this.publicatieClient.getVersionedFile(anyString(), anyString(), eq("xls"))).thenReturn(new ByteArrayInputStream("<text>xls</text>".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void mapAll() {
        var mappers = new PlooiDocumentMappers()
                .withPublicatieClient(this.publicatieClient)
                .addMapper("xml", buildMapper("metadata"))
                .addMapper("pdf", buildMapper("text"));
        var plooi = new PlooiEnvelope("test", "test");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("xml"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("pdf")));
        var mapped = mappers.process(plooi);
        assertEquals("xml pdf", mapped.getDocumentText());
    }

    @Test
    void mapXML() {
        var mappers = new PlooiDocumentMappers()
                .withPublicatieClient(this.publicatieClient)
                .addMapper("xml", buildMapper("metadata"));
        var plooi = new PlooiEnvelope("test", "test");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("xml"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("pdf")));
        var mapped = mappers.process(plooi);
        assertEquals("xml", mapped.getDocumentText());
    }

    @Test
    void mapAlternatives() {
        var mappers = new PlooiDocumentMappers()
                .withPublicatieClient(this.publicatieClient)
                .addMapper("xml", buildMapper("metadata"))
                .addAltMappers(List.of("pdf", "doc"), List.of(buildMapper("text"), buildMapper("text")));
        var plooi = new PlooiEnvelope("test", "test");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("xml"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("doc"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("pdf")));
        var mapped = mappers.process(plooi);
        assertEquals("xml doc", mapped.getDocumentText());
    }

    @Test
    void mapAlternatives_OtherWayAround() {
        // same mappers as above
        var mappers = new PlooiDocumentMappers()
                .withPublicatieClient(this.publicatieClient)
                .addMapper("xml", buildMapper("metadata"))
                .addAltMappers(List.of("pdf", "doc"), List.of(buildMapper("text"), buildMapper("text")));
        var plooi = new PlooiEnvelope("test", "test");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("xml"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("pdf"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("doc")));
        var mapped = mappers.process(plooi);
        assertEquals("xml pdf", mapped.getDocumentText());
    }

    @Test
    void mapAlternativesMulti() {
        var mappers = new PlooiDocumentMappers()
                .withPublicatieClient(this.publicatieClient)
                .addMapper("xml", buildMapper("metadata"))
                .addAltMappers(List.of("pdf", "doc"), List.of(buildMapper("text"), buildMapper("text")))
                .addAltMappers(List.of("csv", "xls"), List.of(buildMapper("text"), buildMapper("text")));
        var plooi = new PlooiEnvelope("test", "test");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("xml"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("doc"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("csv"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("xls"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("pdf")));
        var mapped = mappers.process(plooi);
        assertEquals("xml doc csv", mapped.getDocumentText());
    }

    @Test
    void mapAlternativesMultiMapper() {
        var mappers = new PlooiDocumentMappers()
                .withPublicatieClient(this.publicatieClient)
                .addMapper("xml", buildMapper("metadata"))
                .addAltMappers(List.of("pdf", "doc"), buildMapper("text"));
        var plooi = new PlooiEnvelope("test", "test");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("xml"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("doc"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("pdf")));
        var mapped = mappers.process(plooi);
        assertEquals("xml doc", mapped.getDocumentText());
    }

    @Test
    void mapWildcard() {
        var mappers = new PlooiDocumentMappers()
                .withPublicatieClient(this.publicatieClient)
                .addMapper("xml", buildMapper("metadata"))
                .addCatchallMapper(buildMapper("text"));
        var plooi = new PlooiEnvelope("test", "test");
        plooi.getVersies()
                .add(new Versie().nummer(1)
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("xml"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("doc"))
                        .addBestandenItem(new Bestand().bestandsnaam("test").label("pdf")));
        var mapped = mappers.process(plooi);
        assertEquals("xml doc pdf", mapped.getDocumentText());
    }

    private ConfigurableDocumentMapper buildMapper(String tag) {
        return ConfigurableDocumentMapper.configureWith(XML_MAPPING).addDocumentText(tag).mapper();
    }
}

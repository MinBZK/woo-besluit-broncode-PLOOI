package nl.overheid.koop.plooi.document.map;

import static nl.overheid.koop.plooi.test.util.TestUtils.readFileAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ConfigurableFileMapperTest {

    @BeforeAll
    static void setup() {
        System.setProperty("nl.overheid.koop.plooi.model.data.PrettyPrint", "true");
    }


    private static Stream<Arguments> provideMappers() {
        return Stream.of(
                Arguments.of("plooifiles-in.xml", ConfigurableFileMapper.configureWith(new PlooiXmlMapping())
                        .mapIdentifier("//documenten/document[1]/id")
                        .mapTimestamp(DateMapping.isoDateTime("/documenten/document[1]/timestamp"))
                        .mapTitel(TextMapping.plain("/documenten/document[1]/titel"))
                        .mapBestandsnaam("/documenten/document[1]/bestandsnaam")
                        .mapUrl("/documenten/document[1]/ref")
                        .mapMimeType("/documenten/document[1]/type")
                        .setLabel("xml")
                        .addChildMapper("/documenten/document[2]", ConfigurableFileMapper.childConfiguration()
                                .mapIdentifier("id")
                                .mapTimestamp(DateMapping.isoDateTime("timestamp"))
                                .mapTitel(TextMapping.plain("titel"))
                                .mapBestandsnaam("bestandsnaam")
                                .mapUrl("ref")
                                .mapMimeType("type")
                                .setLabel("pdf")
                                .publish())
                        .mapper()),
                Arguments.of("plooifiles-in.json", ConfigurableFileMapper.configureWith(new PlooiJsonMapping())
                        .mapIdentifier("$[0].id")
                        .mapTimestamp(DateMapping.isoDateTime("$[0].timestamp"))
                        .mapTitel(TextMapping.plain("$[0].titel"))
                        .mapBestandsnaam("$[0].bestandsnaam")
                        .mapUrl("$[0].ref")
                        .mapMimeType("$[0].type")
                        .setLabel("xml")
                        .addChildMapper("$[1]", ConfigurableFileMapper.childConfiguration()
                                .mapIdentifier("id")
                                .mapTimestamp(DateMapping.isoDateTime("timestamp"))
                                .mapTitel(TextMapping.plain("titel"))
                                .mapBestandsnaam("bestandsnaam")
                                .mapUrl("ref")
                                .mapMimeType("type")
                                .setLabel("pdf")
                                .publish())
                        .mapper()));
    }

    @ParameterizedTest
    @MethodSource("provideMappers")
    void populateSuccess(String inputFile, ConfigurableFileMapper mapper) throws IOException {
        var plooiFile = mapper.populate(readFileAsString(getClass(), inputFile));
        assertEquals(1, plooiFile.getChildren().size());
        assertEquals("1", plooiFile.getFile().getId());
        assertEquals("xml", plooiFile.getFile().getLabel());
        assertEquals("http://example.com/parent.xml", plooiFile.getFile().getUrl());
        assertEquals("2", plooiFile.getChildren().get(0).getFile().getId());
        assertEquals("pdf", plooiFile.getChildren().get(0).getFile().getLabel());
        assertEquals("http://example.com/attachment.pdf", plooiFile.getChildren().get(0).getFile().getUrl());
    }
}

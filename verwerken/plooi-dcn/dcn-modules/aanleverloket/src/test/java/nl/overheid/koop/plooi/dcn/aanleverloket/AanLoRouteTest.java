package nl.overheid.koop.plooi.dcn.aanleverloket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.List;
import nl.overheid.koop.plooi.dcn.component.types.DeliveryEnvelope;
import nl.overheid.koop.plooi.dcn.component.types.PlooiFile;
import nl.overheid.koop.plooi.test.util.TestUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AanLoRouteTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message message;

    @ParameterizedTest
    @CsvSource({
            "bijwerken_plooicb-2021-33.xml, false",
            "verwijderen-plooicb-2021-4.xml, true",
            "toevoegen-plooicb-2021-4.xml, false"
    })
    void removeCheckTest(String fileName, Boolean removal) {
        DeliveryEnvelope plooiDoc = new DeliveryEnvelope("test", "test");
        when(this.exchange.getIn()).thenReturn(this.message);
        when(this.message.getBody(List.class)).thenReturn(List.of(
                plooiDoc.addPlooiFile(new PlooiFile(fileName, "xml").content(TestUtils.readFileAsBytes(getClass(), fileName)))));

        assertEquals(removal, AanLoRoute.REMOVE_CHECK.matches(this.exchange));
    }
}

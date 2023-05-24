package nl.overheid.koop.plooi.aanleveren.zoeken.domain;

import lombok.val;
import nl.overheid.koop.plooi.aanleveren.zoeken.infrastructure.dcn.DcnClientService;
import nl.overheid.koop.plooi.plooisecurity.domain.authorization.JwtAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ZoekenServiceTest {
    @Mock private DcnClientService dcnClientService;
    @InjectMocks private ZoekenService sut;

    @Captor
    ArgumentCaptor<List<String>> publishersArg;

    @Test
    void getPublisher() {
        var organisationCode = "mnre1034";
        organisationCode = organisationCode.replaceAll("\\d*$", "");

        assertEquals("mnre", organisationCode);
    }

    @ParameterizedTest
    @CsvSource({
            "mnre1034,ministerie/mnre1034",
            "col03,col/col03",
            "gm0397,gemeente/gm0397",
            "oorg10004,oorg/oorg10004",
            "pv26,provincie/pv26",
            "ws0202,waterschap/ws0202",
            "onorg12,onbekende_organisatie/onorg12"
    })
    void getDocuments(String organisationCode, String identifier) {
        val authentication = JwtAuthenticationToken.authenticated(
                null, List.of(organisationCode), null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        sut.getDocuments(0L);

        verify(dcnClientService).searchDocuments(publishersArg.capture(), eq(0L));
        val publishers = publishersArg.getValue();
        assertEquals("https://identifier.overheid.nl/tooi/id/%s".formatted(identifier), publishers.get(0));
    }
}

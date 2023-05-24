package nl.overheid.koop.plooi.plooiiamservice.domain.claims;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static nl.overheid.koop.plooi.plooiiamservice.domain.claims.Organisation.ORGANISATION_CLAIM_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrganisationTest {
    private final Organisation sut = new Organisation(new ObjectMapper());

    @ParameterizedTest
    @CsvSource(value = {
            "Saba:col03",
            "Ministerie van Veiligheid en Justitie:mnre1058",
            "Ministerie van Economische Zaken, Landbouw en Innovatie:mnre1045",
            "Ministerie van Economische Zaken:mnre1045",
            "Aa en Hunze:gm1680",
            "Zwolle:gm0193",
            "Den Haag:gm0518",
            "Verenigde Vergadering der Staten-Generaal:oorg10000",
            "Rijkswaterstaat:oorg10004",
            "Eerste Kamer der Staten-Generaal:oorg10001",
            "Limburg:pv31",
            "Zeeland:pv29",
            "Hoogheemraadschap Amstel, Gooi en Vecht:ws0155",
            "Wetterskip Fryslân:ws0653",
            "Waterschap Veluwe:ws0153"
    }, delimiter = ':')
    void getAttribute(String orgClaimValue, String orgCode) {
        val principal = new DefaultSaml2AuthenticatedPrincipal(
                "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                Map.of(ORGANISATION_CLAIM_KEY, List.of(orgClaimValue)));

        val attribute = sut.getAttribute(principal);

        assertEquals(orgCode, attribute.get(0));
    }

    @Test
    void getAttribute_notAllowed() {
        val principal = new DefaultSaml2AuthenticatedPrincipal(
                "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                Map.of(ORGANISATION_CLAIM_KEY, List.of("Kennis- en Exploitatiecentrum Officiële Overheidspublicaties")));

        val attribute = sut.getAttribute(principal);

        assertTrue(attribute.isEmpty());
    }

    @Test
    void getAttribute_unableToReadWaardelijst() throws IOException {
        val principal = new DefaultSaml2AuthenticatedPrincipal(
                "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS",
                Map.of(ORGANISATION_CLAIM_KEY, List.of("Kennis- en Exploitatiecentrum Officiële Overheidspublicaties")));
        val mockMapper = mock(ObjectMapper.class);
        when(mockMapper.readTree(any(InputStream.class))).thenThrow(IOException.class);
        val sut = new Organisation(mockMapper);

        val exception = assertThrows(RuntimeException.class, () -> sut.getAttribute(principal));
        assertEquals("Unable to read waardelijst", exception.getMessage());
    }
}

package nl.overheid.koop.plooi.aanleveren.metadata.domain.service.identifier;

import lombok.val;
import nl.overheid.koop.plooi.aanleveren.metadata.configuration.ApplicationProperties;
import nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.IdentifierException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlooiIdentifierServiceTest {

    @Test
    void getPID() throws IdentifierException {
        val applicationProperties = new ApplicationProperties("http://localhost:8080/overheid/openbaarmaken/api/v0/documenten/", "https://open.overheid.nl/documenten/", null);
        val pi = new PlooiIdentifierService(applicationProperties);

        val uuid = pi.createDocumentId();
        val pid = pi.getPID(uuid);
        val pidLength = applicationProperties.pidEndpoint().length();

        assertEquals('-', uuid.charAt(8));
        assertEquals('-', uuid.charAt(13));
        assertEquals('4', uuid.charAt(14));
        assertEquals('-', uuid.charAt(18));
        assertEquals('-', uuid.charAt(23));
        assertEquals(pidLength + 36, pid.length());
    }

    @Test
    void getPID_withoutIdentifier() {
        val applicationProperties = new ApplicationProperties("", "", null);
        val pi = new PlooiIdentifierService(applicationProperties);

        val exception = assertThrows(IdentifierException.class, () ->
                pi.getPID(""));
        assertEquals("UUID is missing.", exception.getMessage());
    }

    @Test
    void getPID_withoutPIDEndppoint() {
        val applicationProperties = new ApplicationProperties("", "", null);
        val pi = new PlooiIdentifierService(applicationProperties);

        val exception = assertThrows(IdentifierException.class, () ->
                pi.getPID("uuid"));

        assertEquals("pid.endpoint has no value", exception.getMessage());
    }

    @Test
    void getPID_withoutLastSlash() throws IdentifierException {
        val applicationProperties = new ApplicationProperties("http://localhost:8080/overheid/openbaarmaken/api/v0/documenten/", "https://open.overheid.nl/documenten", null);
        val pi = new PlooiIdentifierService(applicationProperties);

        val uuid = pi.createDocumentId();
        val pid = pi.getPID(uuid);

        val pidLength = applicationProperties.pidEndpoint().length() + 1;
        assertEquals(pidLength + 36, pid.length());
    }
}

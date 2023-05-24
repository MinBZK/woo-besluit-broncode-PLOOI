package nl.overheid.koop.plooi.aanleveren.clamav.endpoints;

import nl.overheid.koop.plooi.aanleveren.clamav.service.ClamAVService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PingClamAVEndpointTest {
    @Mock
    private ClamAVService clamAVService;
    @InjectMocks
    private PingClamAVEndpoint pingClamAVEndpoint;

    @Test
    void pingDeamon() {
        assertEquals(HttpStatus.OK, pingClamAVEndpoint.pingDeamon().getStatusCode());
    }
}

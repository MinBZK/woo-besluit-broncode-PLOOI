package nl.overheid.koop.plooi.aanleveren.clamav.endpoints;

import lombok.val;
import nl.overheid.koop.plooi.aanleveren.clamav.service.ClamAVService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static nl.overheid.koop.plooi.aanleveren.clamav.util.PdfReader.getValidPDF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClamAVEndpointTest {
    @Mock
    private ClamAVService clamAVService;
    @InjectMocks
    private ClamAVEndpoint clamAVEndpoint;

    @Test
    void scanDocument_ok() {
        val pdf = getValidPDF();
        when(clamAVService.isFileOk(pdf)).thenReturn(true);

        val response = clamAVEndpoint.scanDocument(pdf);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void scanDocument_badRequest() {
        val pdf = getValidPDF();
        when(clamAVService.isFileOk(pdf)).thenReturn(false);

        val response = clamAVEndpoint.scanDocument(pdf);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

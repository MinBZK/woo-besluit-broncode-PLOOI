package nl.overheid.koop.plooi.aanleveren.clamav.service;

import fi.solita.clamav.ClamAVClient;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.clamav.exceptions.ScanServiceException;
import nl.overheid.koop.plooi.aanleveren.clamav.util.PdfReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static nl.overheid.koop.plooi.aanleveren.clamav.util.TestConstants.CLAM_AV_CLIENT_RESPONSE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClamAVServiceTest {
    @Mock
    private ClamAVClient clamAVClient;
    @InjectMocks
    private ClamAVService clamAVService;

    @Test
    void isFileOk() throws IOException {
        val pdf = PdfReader.getValidPDF();
        when(clamAVClient.scan(pdf)).thenReturn(CLAM_AV_CLIENT_RESPONSE);

        assertTrue(clamAVService.isFileOk(pdf));
    }

    @Test
    void isFileOk_scanServiceException() throws IOException {
        byte[] pdf = PdfReader.getValidPDF();
        when(clamAVClient.scan(pdf)).thenThrow(IOException.class);

        assertThrows(ScanServiceException.class, () -> clamAVService.isFileOk(pdf));
    }

    @Test
    void testPositive() throws IOException {
        byte[] EICAR = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*".getBytes(StandardCharsets.US_ASCII);
        when(clamAVClient.scan(EICAR)).thenReturn("FOUND".getBytes());

        assertFalse(clamAVService.isFileOk(EICAR));
    }

    @Test
    void pingClamAVDeamon() throws IOException {
        clamAVService.pingClamAVDeamon();

        verify(clamAVClient).ping();
    }

    @Test
    void pingClamAVDeamon_scanServiceException() throws IOException {
        clamAVService.pingClamAVDeamon();
        when(clamAVClient.ping()).thenThrow(IOException.class);

        assertThrows(ScanServiceException.class, () -> clamAVService.pingClamAVDeamon());
    }
}

package nl.overheid.koop.plooi.aanleveren.clamav.exceptionHandling;

import nl.overheid.koop.plooi.aanleveren.clamav.exceptions.GlobalExceptionHandler;
import nl.overheid.koop.plooi.aanleveren.clamav.exceptions.ScanServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleScanServiceExceptionWithScanServiceException() {
        final ScanServiceException scanServiceException = new ScanServiceException("Test message");
        final ResponseEntity<String> result = globalExceptionHandler.handleScanServiceException(scanServiceException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}

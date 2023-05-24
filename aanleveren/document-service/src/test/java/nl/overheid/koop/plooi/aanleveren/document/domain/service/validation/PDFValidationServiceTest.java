package nl.overheid.koop.plooi.aanleveren.document.domain.service.validation;

import lombok.val;
import nl.overheid.koop.plooi.aanleveren.document.configuration.ApplicationProperties;
import nl.overheid.koop.plooi.aanleveren.document.domain.exceptions.ResourceTooLargeException;
import nl.overheid.koop.plooi.aanleveren.document.domain.service.validation.PDFValidationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class PDFValidationServiceTest {
    private static PDFValidationService sut;

    @BeforeAll
    static void init() {
        MDC.put("ProcessIdentifier", "12345");
        val properties = new ApplicationProperties( 20971520L, null);
        sut = new PDFValidationService(properties);
    }

    @Test
    void testWithCorrectTypePDF() throws IOException {
        File resource = new ClassPathResource("pdf/Nummer1.pdf").getFile();
        byte[] resourceContent = Files.readAllBytes(resource.toPath());

        assertTrue(sut.isCorrectType(resourceContent));
    }

    @Test
    void testWithValidPDF() throws IOException {
        File resource = new ClassPathResource("pdf/Nummer1.pdf").getFile();
        byte[] resourceContent = Files.readAllBytes(resource.toPath());

        assertTrue(sut.isValid(resourceContent));
    }

    @Test
    void testWithInvalidPDF() throws IOException {
        File resource = new ClassPathResource("pdf/IAmWrongPDF.pdf").getFile();
        byte[] resourceContent = Files.readAllBytes(resource.toPath());

        assertFalse(sut.isCorrectType(resourceContent));
    }

    @Test
    void testWithTooLargeByteArray() {
        byte[] resourceContent = new byte[1548576000];

        assertThrows(ResourceTooLargeException.class, () -> sut.isValid(resourceContent));
    }

    @Test
    void testWithEmptyByteArray() {
        byte[] resourceContent = new byte[0];

        assertFalse(sut.isValid(resourceContent));
    }
}

package nl.overheid.koop.plooi.aanleveren.clamav.util;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;

public class PdfReader {

    @SneakyThrows
    public static byte[] getValidPDF() {
        val file = new ClassPathResource("pdf/Nummer20219495.pdf").getFile();
        return Files.readAllBytes(file.toPath());
    }

    @SneakyThrows
    public static byte[] getPDF(final String path) {
        val file = new ClassPathResource(path).getFile();
        return Files.readAllBytes(file.toPath());
    }
}

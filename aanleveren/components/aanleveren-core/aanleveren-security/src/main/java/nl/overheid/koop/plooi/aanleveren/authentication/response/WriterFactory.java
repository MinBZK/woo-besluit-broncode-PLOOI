package nl.overheid.koop.plooi.aanleveren.authentication.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@RequiredArgsConstructor
public class WriterFactory {
    private final List<Writer> writers;

    public Writer getResponseWriter(final HttpStatus httpStatus) {
        return writers.stream()
                .filter(writer -> writer.isCorrectStatus(httpStatus))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}

package nl.overheid.koop.plooi.aanleveren.authentication.response;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class WriterFactoryTest {
    private final WriterFactory writerFactory =
            new WriterFactory(List.of(new ExpiredToken(), new InvalidToken(), new MissingToken()));

    @Test
    void getResponseWriter_OK() {
        assertThrows(RuntimeException.class, () -> writerFactory.getResponseWriter(HttpStatus.OK));
    }

    @Test
    void getResponseWriter_missingToken() {
        val responseWriter = writerFactory.getResponseWriter(HttpStatus.FORBIDDEN);
        assertTrue(responseWriter instanceof MissingToken);
    }

    @Test
    void getResponseWriter_expiredToken() {
        val responseWriter = writerFactory.getResponseWriter(HttpStatus.UNAUTHORIZED);
        assertTrue(responseWriter instanceof ExpiredToken);
    }

    @Test
    void getResponseWriter() {
        val responseWriter = writerFactory.getResponseWriter(HttpStatus.BAD_REQUEST);
        assertTrue(responseWriter instanceof InvalidToken);
    }
}

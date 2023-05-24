package nl.overheid.koop.plooi.aanleveren.authentication.response;

import nl.overheid.koop.plooi.aanleveren.fout.PlooiAanleverFout;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InvalidToken implements Writer {

    @Override
    public void write(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(mapToJson(
                PlooiAanleverFout.builder()
                        .type("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen/index.html#identifier-fout")
                        .title("identifier-fout")
                        .status(400)
                        .detail("De meegegeven identifier is niet geldig. Raadpleeg voor meer informatie de documentatie.")
                        .instance("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen")
                        .messages(null)
                        .build()));
    }

    @Override
    public boolean isCorrectStatus(final HttpStatus httpStatus) {
        return HttpStatus.BAD_REQUEST.equals(httpStatus);
    }
}

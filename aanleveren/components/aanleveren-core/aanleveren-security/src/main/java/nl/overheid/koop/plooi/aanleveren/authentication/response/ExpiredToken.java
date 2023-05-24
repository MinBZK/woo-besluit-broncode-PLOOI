package nl.overheid.koop.plooi.aanleveren.authentication.response;

import nl.overheid.koop.plooi.aanleveren.fout.PlooiAanleverFout;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ExpiredToken implements Writer {

    @Override
    public void write(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(mapToJson(
                PlooiAanleverFout.builder()
                        .type("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen/index.html#identificatie-fout")
                        .title("Identificatiefout")
                        .status(401)
                        .detail("Deze handeling is alleen mogelijk voor geautoriseerden. Raadpleeg voor meer informatie de documentatie.")
                        .instance("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen")
                        .messages(List.of("De identificerende gegevens in dit verzoek ontbreken of zijn onjuist. Geef (andere) identificerende gegevens mee."))
                        .build()));
    }

    @Override
    public boolean isCorrectStatus(final HttpStatus httpStatus) {
        return HttpStatus.UNAUTHORIZED.equals(httpStatus);
    }
}

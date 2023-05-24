package nl.overheid.koop.plooi.aanleveren.authentication.response;

import nl.overheid.koop.plooi.aanleveren.fout.PlooiAanleverFout;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MissingToken implements Writer {

    @Override
    public void write(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(mapToJson(
                PlooiAanleverFout.builder()
                        .type("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen/index.html#autorisatie-fout")
                        .title("Autorisatiefout")
                        .status(403)
                        .detail("U bent niet geautoriseerd voor deze actie. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).")
                        .instance("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen")
                        .messages(List.of("Aan de identificerende gegevens in dit verzoek zijn geen privileges tot deze handeling verbonden. Geef andere identificerende gegevens mee."))
                        .build()));
    }

    @Override
    public boolean isCorrectStatus(final HttpStatus httpStatus) {
        return HttpStatus.FORBIDDEN.equals(httpStatus);
    }
}

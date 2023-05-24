package nl.overheid.koop.plooi.plooisecurity.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.overheid.koop.plooi.plooisecurity.domain.response.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ResponseService {
    private final ObjectMapper mapper;

    public ResponseService() {
        this.mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void forbidden(final HttpServletResponse response) throws IOException {
        var autorisatiefout = ErrorResponse.builder()
                .type("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen/index.html#autorisatie-fout")
                .title("Autorisatiefout")
                .status(403)
                .detail("U bent niet geautoriseerd voor deze actie. Raadpleeg voor meer informatie [de documentatie](https://koop.gitlab.io/plooi/aanleveren/openapi-spec/).")
                .instance("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen")
                .messages(List.of("Aan de identificerende gegevens in dit verzoek zijn geen privileges tot deze handeling verbonden. Geef andere identificerende gegevens mee."))
                .build();
        writeResponse(response, HttpStatus.FORBIDDEN.value(), autorisatiefout);
    }

    public void unauthorized(final HttpServletResponse response) throws IOException {
        var identificatiefout = ErrorResponse.builder()
                .type("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen/index.html#identificatie-fout")
                .title("Identificatiefout")
                .status(401)
                .detail("Deze handeling is alleen mogelijk voor geautoriseerden. Raadpleeg voor meer informatie de documentatie.")
                .instance("https://koop.gitlab.io/plooi/aanleveren/openapi-spec/foutenbeschrijvingen")
                .messages(List.of("De identificerende gegevens in dit verzoek ontbreken of zijn onjuist. Geef (andere) identificerende gegevens mee."))
                .build();

        writeResponse(response, HttpStatus.UNAUTHORIZED.value(), identificatiefout);
    }

    private void writeResponse(
            final HttpServletResponse response,
            final int status,
            final ErrorResponse errorResponse) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        mapper.writeValue(response.getWriter(), errorResponse);
    }
}

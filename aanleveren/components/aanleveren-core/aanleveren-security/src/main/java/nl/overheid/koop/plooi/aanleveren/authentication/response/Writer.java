package nl.overheid.koop.plooi.aanleveren.authentication.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.fout.PlooiAanleverFout;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Writer {
    void write(HttpServletResponse response) throws IOException;
    boolean isCorrectStatus(final HttpStatus status);

    default String mapToJson(PlooiAanleverFout plooiAanleverFout) throws JsonProcessingException {
        val objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(plooiAanleverFout);
    }
}

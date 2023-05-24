package nl.overheid.koop.plooi.aanleveren.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.requireNonNull;

@Slf4j
@Builder
public record MeasuringPoint(String description, String processIdentifier, String pid, Integer statuscode) {

    public MeasuringPoint {
        requireNonNull(description);
        requireNonNull(processIdentifier);
    }

    @Override
    public String toString() {

        try {
            return new ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .disable(SerializationFeature.INDENT_OUTPUT)
                    .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("Measuringpoint could not be parsed to String", e);
            return "";
        }
    }
}

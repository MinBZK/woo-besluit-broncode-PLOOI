package nl.overheid.koop.plooi.model.data.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import nl.overheid.koop.plooi.model.data.Plooi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONBinding<T> implements PlooiBinding<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Class<T> unmarshalTo;

    public JSONBinding(Class<T> toClass) {
        this.unmarshalTo = toClass;
    }

    private static final ObjectMapper MAPPER;
    static {
        MAPPER = JsonMapper.builder()
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .addModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();
    }

    public static void main(String[] args) {
        new JSONBinding<>(Plooi.class).demo();
    }

    private void demo() {
        var remarshalled = marshalToString(unmarshalFromFile("src/test/resources/nl/overheid/koop/plooi/model/data/test_api_plooi.json"));
        this.logger.info("Remarshalled:\n{}", remarshalled);
    }

    @Override
    public T unmarshal(Reader reader) {
        try {
            var plooi = MAPPER.readValue(reader, this.unmarshalTo);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Unmarshalled {}", PlooiBinding.toStringTruncated(plooi));
            }
            return plooi;
        } catch (IOException e) {
            throw new PlooiBindingException(e);
        }
    }

    @Override
    public void marshal(T plooi, Writer writer) {
        try {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Marshalling {}", PlooiBinding.toStringTruncated(plooi));
            }
            var jsonWriter = Boolean.getBoolean("nl.overheid.koop.plooi.model.data.PrettyPrint")
                    ? MAPPER.writerWithDefaultPrettyPrinter()
                    : MAPPER.writer();
            jsonWriter.writeValue(writer, plooi);
        } catch (IOException e) {
            throw new PlooiBindingException(e);
        }
    }
}

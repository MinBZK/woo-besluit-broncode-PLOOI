package nl.overheid.koop.plooi.model.data.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public interface PlooiBinding<T> {

    default T unmarshalFromFile(String file) {
        try (var reader = new FileReader(file, StandardCharsets.UTF_8)) {
            return unmarshal(reader);
        } catch (IOException e) {
            throw new PlooiBindingException(e);
        }
    }

    default T unmarshalFromStream(InputStream stream) {
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return unmarshal(reader);
        } catch (IOException e) {
            throw new PlooiBindingException(e);
        }
    }

    default T unmarshalFromString(String string) {
        return unmarshal(new StringReader(string));
    }

    T unmarshal(Reader reader);

    default String marshalToString(T toMarshall) {
        try (var writer = new StringWriter()) {
            marshal(toMarshall, writer);
            return writer.toString();
        } catch (IOException e) {
            throw new PlooiBindingException(e);
        }
    }

    default void marshalToFile(T toMarshall, File file) {
        try (var writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            marshal(toMarshall, writer);
        } catch (IOException e) {
            throw new PlooiBindingException(e);
        }
    }

    void marshal(T toMarshall, Writer writer);

    static String toStringTruncated(Object str) {
        var count = new AtomicInteger(0);
        return str.toString()
                .lines()
                .filter(word -> count.getAndIncrement() < 5)
                .collect(Collectors.joining("\n")) + " ...";
    }
}

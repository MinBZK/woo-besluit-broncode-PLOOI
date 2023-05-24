package nl.overheid.koop.plooi.model.dictionary;

import java.io.IOException;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public final class PropertyFileHelper {

    private PropertyFileHelper() {
        // Can't instantiate util class
    }

    public static Properties readProperties(Class<?> loadVia, String propsLocation, Properties props) {
        if (!StringUtils.isEmpty(propsLocation)) {
            try (var propStream = loadVia.getResourceAsStream(propsLocation)) {
                props.load(propStream);
            } catch (IOException | NullPointerException e) {
                throw new TooiDictionaryException("Cannot read properties " + propsLocation, e);
            }
        }
        return props;
    }
}

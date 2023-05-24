package nl.overheid.koop.plooi.model.dictionary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public final class TooiDictionaries {

    private static final String GLOBALDICTPROPS_LOCATION = "/tooi/waardelijsten.properties";

    private final TooiJsonReader reader = new TooiJsonReader();
    private final Map<String, String> globalDictProps;
    private final Map<String, TooiDictionary> dictionaries = new HashMap<>();

    public TooiDictionaries() {
        this.globalDictProps = PropertyFileHelper.readProperties(this.getClass(), GLOBALDICTPROPS_LOCATION, new Properties())
                .entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
        this.globalDictProps.keySet()
                .stream()
                .forEach(d -> readIntoDictionary(this.dictionaries.computeIfAbsent(d, TooiDictionary::new), d));
    }

    public Map<String, String> getDictionaries() {
        return this.globalDictProps;
    }

    public void readIntoDictionary(TooiDictionary dict, String dictNameOrLocation) {
        if (!StringUtils.isBlank(dictNameOrLocation)) {
            String dictLocation = this.globalDictProps.getOrDefault(dictNameOrLocation, dictNameOrLocation);
            try (var dictStream = getClass().getResourceAsStream(dictLocation.trim())) {
                this.reader.read(dict, dictStream);
            } catch (IOException | NullPointerException e) {
                throw new TooiDictionaryException("Cannot read dictionary " + dictLocation, e);
            }
        }
    }

    public Optional<TooiDictionary> getDictionary(String dictionary) {
        return Optional.ofNullable(this.dictionaries.get(dictionary));
    }
}

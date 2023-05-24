package nl.overheid.koop.plooi.model.dictionary.service;

import java.util.ArrayList;
import java.util.List;
import nl.overheid.koop.plooi.model.dictionary.TooiDictionaries;
import nl.overheid.koop.plooi.model.dictionary.model.Label;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LijstenService implements LijstenApiDelegate {

    private static final String UNKNOWN_SCHEME = "Unknown scheme ";
    private static final String UNKNOWN_URI = "Unknown uri ";

    private TooiDictionaries tooiDictionaries;

    public LijstenService(TooiDictionaries dictionaries) {
        this.tooiDictionaries = dictionaries;
    }

    @Override
    public ResponseEntity<List<String>> getDictionaries() {
        return ResponseEntity.ok(new ArrayList<>(this.tooiDictionaries.getDictionaries().keySet()));
    }

    @Override
    public ResponseEntity<List<Label>> getDictionary(String scheme) {
        return ResponseEntity.ok(
                this.tooiDictionaries
                        .getDictionary(scheme)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, UNKNOWN_SCHEME + scheme))
                        .getDisplayLabels()
                        .stream()
                        .toList());
    }

    @Override
    public ResponseEntity<Resource> getTooi(String scheme) {
        var fileName = this.tooiDictionaries.getDictionaries().get(scheme);
        if (fileName == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, UNKNOWN_SCHEME + scheme);
        } else {
            return ResponseEntity.ok(new ClassPathResource(fileName));
        }
    }

    @Override
    public ResponseEntity<Label> getLabel(String scheme, String suffix) {
        var uri = suffix.substring(1); // Strip leading slash
        return ResponseEntity.ok(
                new Label()
                        .uri(uri)
                        .label(this.tooiDictionaries
                                .getDictionary(scheme)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, UNKNOWN_SCHEME + scheme))
                                .findUri(uri)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, UNKNOWN_URI + uri))));
    }
}

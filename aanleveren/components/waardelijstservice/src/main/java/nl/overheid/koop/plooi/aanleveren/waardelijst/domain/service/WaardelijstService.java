package nl.overheid.koop.plooi.aanleveren.waardelijst.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nl.overheid.koop.plooi.aanleveren.waardelijst.domain.model.AanleverenServiceException;
import nl.overheid.koop.plooi.aanleveren.waardelijst.domain.model.WaardeItem;
import nl.overheid.koop.plooi.aanleveren.waardelijst.domain.model.WaardelijstSoort;
import nl.overheid.koop.plooi.aanleveren.waardelijst.infrastructure.WaardelijstResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WaardelijstService {
    private final WaardelijstResource waardelijstResource;
    private final ObjectMapper mapper;

    public List<WaardeItem> getWaardelijst(final WaardelijstSoort soort) {
        try {
            return Arrays.stream(
                            mapper.readValue(
                                    waardelijstResource.loadWaardelijst(soort),
                                    WaardeItem[].class))
                    .filter(waardeItem -> waardeItem.getLabel() != null)
                    .toList();
        } catch (IOException e) {
            throw new AanleverenServiceException("Conversie van de waardelijst is niet gelukt");
        }
    }

    public Optional<WaardelijstSoort> getWaardelijstSoort(final String soort) {
        try {
            return Optional.of(WaardelijstSoort.valueOf(soort.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}

package nl.overheid.koop.plooi.aanleveren.waardelijst.web;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.api.WaardelijstenApi;
import nl.overheid.koop.plooi.aanleveren.models.Waarde;
import nl.overheid.koop.plooi.aanleveren.waardelijst.domain.model.WaardeItem;
import nl.overheid.koop.plooi.aanleveren.waardelijst.domain.service.WaardelijstService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class WaardelijstEndpoint implements WaardelijstenApi {
    private final WaardelijstService waardelijstService;

    @Override
    public ResponseEntity<List<Waarde>> getWaardelijst(final String soort) {
        val possibleSoort = waardelijstService.getWaardelijstSoort(soort);
        if (possibleSoort.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        val waardelijst =
                waardelijstService.getWaardelijst(possibleSoort.get());
        val response = waardelijst.stream().map(this::toWaardeDTO).toList();

        return ResponseEntity.ok(response);
    }

    private Waarde toWaardeDTO(final WaardeItem waardeItem) {
        val waardeDTO = new Waarde();
        waardeDTO.setIdentifier(waardeItem.getIdentifier());
        waardeDTO.setWaarde(waardeItem.getLabel());
        return waardeDTO;
    }
}

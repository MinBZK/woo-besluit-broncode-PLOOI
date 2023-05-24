package nl.overheid.koop.plooi.aanleveren.waardelijst.infrastructure;

import lombok.val;
import nl.overheid.koop.plooi.aanleveren.waardelijst.domain.model.AanleverenServiceException;
import nl.overheid.koop.plooi.aanleveren.waardelijst.domain.model.WaardelijstSoort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class WaardelijstResource {

    public InputStream loadWaardelijst(final WaardelijstSoort soort) {
        val filepath = "waardelijsten/" + switch (soort) {
            case CARIBISCHE_OPENBARE_LICHAMEN -> "register-caribische-openbare-lichamen-v1.json";
            case DOCUMENTSOORTEN -> "plooi-documentsoorten-v3.json";
            case GEMEENTEN -> "register-gemeenten-compleet-v3.json";
            case MINISTERIES -> "register-ministeries-compleet-v1.json";
            case OVERIGE_OVERHEIDSORGANISATIES -> "register-overige-overheidsorganisaties-compleet-v2.json";
            case PROVINCIES -> "register-provincies-compleet-v1.json";
            case WATERSCHAPPEN -> "register-waterschappen-compleet-v1.json";
            case FILETYPES -> "filetypes-v1.json";
            case TALEN -> "talen-v1.json";
            case DOCUMENTHANDELINGEN -> "plooi-documenthandelingen-v1.json";
            case TOPLIJST -> "top-lijst-v1.json";
        };

        try {
            return new ClassPathResource(filepath).getInputStream();
        } catch (IOException e) {
            throw new AanleverenServiceException("De waardelijst kon niet worden geopend");
        }
    }
}

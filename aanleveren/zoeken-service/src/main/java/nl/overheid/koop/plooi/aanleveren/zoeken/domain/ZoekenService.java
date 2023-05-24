package nl.overheid.koop.plooi.aanleveren.zoeken.domain;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.aanleveren.zoeken.infrastructure.dcn.DcnClientService;
import nl.overheid.koop.plooi.plooisecurity.domain.authorization.JwtAuthenticationToken;
import nl.overheid.koop.plooi.search.model.SearchResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoekenService {
    private final DcnClientService dcnClientService;

    public SearchResponse getDocuments(final long start) {
        return dcnClientService.searchDocuments(getPublishers(), start);
    }

    private List<String> getPublishers() {
        val authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!String.valueOf(authentication.getPrincipal()).equals("anonymousUser")) {
            val claims = (JwtAuthenticationToken) authentication;

            return claims.getOrganisations().stream()
                    .map(this::getPublisherIdentifier)
                    .toList();
        }
        return Collections.emptyList();
    }

    private String getPublisherIdentifier(final String organisationCode) {
        val type = switch (stripTrailingNumbers(organisationCode)) {
            case "col" -> "col";
            case "gm" -> "gemeente";
            case "mnre" -> "ministerie";
            case "oorg" -> "oorg";
            case "pv" -> "provincie";
            case "ws" -> "waterschap";
            default -> "onbekende_organisatie";
        };

        return "https://identifier.overheid.nl/tooi/id/%s/%s".formatted(type, organisationCode);
    }

    private String stripTrailingNumbers(final String organisationCode) {
        return organisationCode.replaceAll("\\d*$", "");
    }
}

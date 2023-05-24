package nl.overheid.koop.plooi.plooiiamservice.domain.claims;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class Organisation implements Claim {
    public static final String ORGANISATION_CLAIM_KEY = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/locality";
    private static final String LABEL_KEY = "http://www.w3.org/2000/01/rdf-schema#label";
    private static final String ORGANISATION_CODE_KEY = "https://identifier.overheid.nl/tooi/def/ont/organisatiecode";
    private final ObjectMapper objectMapper;

    public List<String> getAttribute(final Saml2AuthenticatedPrincipal principal) {
        val organisationNames = principal.getAttribute(ORGANISATION_CLAIM_KEY);

        return organisationNames != null ? getOrganisationIdentifiers(organisationNames) : Collections.emptyList();
    }

    private List<String> getOrganisationIdentifiers(final List<Object> organisationNames) {
        return organisationNames.stream()
                .map(String::valueOf)
                .map(this::getOrganisationIdentifier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<String> getOrganisationIdentifier(final String organisationName) {
        return Stream.of(
                        "ministeries_compleet_1",
                        "gemeenten_compleet_4",
                        "provincies_compleet_1",
                        "overige_overheidsorganisaties_compleet_3",
                        "waterschappen_compleet_1",
                        "caribische_openbare_lichamen_compleet_1"
                )
                .map(this::getWaardelijst)
                .map(waardelijst -> searchWaardelijst(waardelijst, organisationName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private JsonNode getWaardelijst(final String file) {
        try {
            return objectMapper.readTree(new ClassPathResource("waardelijsten/rwc_%s.json".formatted(file)).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Unable to read waardelijst");
        }
    }

    private Optional<String> searchWaardelijst(final JsonNode waardelijst, final String organisationName) {
        for (int i = 0; i < waardelijst.size(); i++) {
            val organisation = waardelijst.get(i);
            if (organisation.has(LABEL_KEY)) {
                val label = getValue(organisation, LABEL_KEY);
                if (matchesOrganisation(label, organisationName)) {
                    if (organisation.has(ORGANISATION_CODE_KEY)) {
                        return Optional.ofNullable(getValue(organisation, ORGANISATION_CODE_KEY));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private String getValue(final JsonNode waardelijst, final String key) {
        return waardelijst.get(key).get(0).get("@value").textValue();
    }

    private boolean matchesOrganisation(final String label, final String organisationName) {
        return label != null
                && (label.toLowerCase().contains(organisationName.toLowerCase())
                || organisationName.startsWith(label));
    }

    @Override
    public boolean getClaim(String claimType) {
        return claimType.equalsIgnoreCase(ORGANISATION_CLAIM_KEY);
    }
}

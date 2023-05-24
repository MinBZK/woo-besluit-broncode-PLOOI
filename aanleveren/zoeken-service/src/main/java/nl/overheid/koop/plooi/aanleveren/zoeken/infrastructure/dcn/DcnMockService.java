package nl.overheid.koop.plooi.aanleveren.zoeken.infrastructure.dcn;

import lombok.val;
import nl.overheid.koop.plooi.search.model.*;

import java.util.ArrayList;
import java.util.List;

public class DcnMockService {

    public SearchResponse getMockSearchresponse(final Long start) {
        val searchResponse = new SearchResponse();

        searchResponse.setTotaal(25L);
        searchResponse.setStart(start);
        searchResponse.setAantal(10L);
        searchResponse.setResultaten(getResultaten(start));
        return searchResponse;
    }

    private List<Plooi> getResultaten(final long start) {
        int aantal;
        if (start < 20) {
            aantal = 10;
        } else if (start <= 25) {
            aantal = 5;
        } else {
            aantal = 0;
        }

        val resultaten = new ArrayList<Plooi>();
        for (int i = 0; i < aantal; i++) {
            resultaten.add(getResultaat());
        }
        return resultaten;
    }

    private Plooi getResultaat() {
        val plooi = new Plooi();
        plooi.setDocument(getDocument());
        plooi.setDocumentrelaties(getDocumentrelaties());
        return plooi;
    }

    private List<Relatie> getDocumentrelaties() {
        val relatie = new Relatie();
        relatie.setRelation("https://open.overheid.nl/documenten/2f1244a2-cde6-4d27-abd2-6aae05eae23e");
        relatie.setRole("https://identifier.overheid.nl/tooi/def/thes/kern/c_4d1ea9ba");
        return List.of(relatie);
    }

    private Document getDocument() {
        val document = new Document();
        document.setPid("https://open.overheid.nl/documenten/2f1244a2-cde6-4d27-abd2-6aae05eae23e");
        document.setCreatiedatum("2022-06-14T00:00:00Z");
        document.setVerantwoordelijke(getIdentifiedResource());
        document.setPublisher(getIdentifiedResource());
        document.setTitelcollectie(getTitelcollectie());
        document.setClassificatiecollectie(getClassificatiecollectie());
        return document;
    }

    private IdentifiedResource getIdentifiedResource() {
        val verantwoordelijke = new IdentifiedResource();
        verantwoordelijke.setId("https://identifier.overheid.nl/tooi/id/ministerie/mnre1034");
        verantwoordelijke.setLabel("ministerie van Binnenlandse Zaken en Koninkrijksrelaties");
        return verantwoordelijke;
    }

    private Titelcollectie getTitelcollectie() {
        val titelcollectie = new Titelcollectie();
        titelcollectie.officieleTitel("Kamerbrief over de nieuwe kamerstukkenmodule");
        titelcollectie.verkorteTitels(List.of("Voorbereiding implementatie Wet open overheid"));
        titelcollectie.alternatieveTitels(List.of("Een hele mooie mooie brief over Wet open overheid", "Of zo iets"));
        return titelcollectie;
    }

    private Classificatiecollectie getClassificatiecollectie() {
        val classificatiecollectie = new Classificatiecollectie();
        classificatiecollectie.addDocumentsoortenItem(getDocumentsoort());
        classificatiecollectie.addThemasItem(getThema());
        return classificatiecollectie;
    }

    private IdentifiedResource getDocumentsoort() {
        val identifiedResource = new IdentifiedResource();
        identifiedResource.setId("https://identifier.overheid.nl/tooi/def/thes/kern/c_056a75e1");
        identifiedResource.setLabel("Kamerstuk");
        return identifiedResource;
    }

    private IdentifiedResource getThema() {
        val identifiedResource = new IdentifiedResource();
        identifiedResource.setId("https://identifier.overheid.nl/tooi/def/thes/top/c_ee06665e");
        identifiedResource.setLabel("Informatievoorziening en ICT");
        return identifiedResource;
    }
}

package nl.overheid.koop.plooi.aanleveren.zoeken.infrastructure.dcn;

import lombok.RequiredArgsConstructor;
import lombok.val;
import nl.overheid.koop.plooi.search.client.SearchClient;
import nl.overheid.koop.plooi.search.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DcnClientService {
    private static final String DOCUMENTSOORT_KAMERSTUK = "https://identifier.overheid.nl/tooi/def/thes/kern/c_056a75e1";
    private static final long AANTAL_RESULTATEN = 25L;
    private final SearchClient searchClient;

    public SearchResponse searchDocuments(final List<String> publishers, final long start) {
        val searchRequest = new SearchRequest();
        searchRequest.setAantal(AANTAL_RESULTATEN);
        searchRequest.setStart(start);
        searchRequest.setFilters(addSearchFilter(publishers));

//        return searchClient.search(searchRequest, "v1");
        return new DcnMockService().getMockSearchresponse(start);
    }

    private SearchFilter addSearchFilter(final List<String> publishers) {
        val searchFilter = new SearchFilter();
        searchFilter.classificatiecollectie(addDocumentsoort());
        searchFilter.publisher(publishers);
        return searchFilter;
    }

    private SearchFilterClassificatiecollectie addDocumentsoort() {
        val classificatiecollectie = new SearchFilterClassificatiecollectie();
        classificatiecollectie.addDocumentsoortenItem(DOCUMENTSOORT_KAMERSTUK);
        return classificatiecollectie;
    }
}


















package nl.overheid.koop.plooi.search.controller;

import javax.validation.Valid;
import nl.overheid.koop.plooi.search.model.SearchRequest;
import nl.overheid.koop.plooi.search.model.SearchResponse;
import nl.overheid.koop.plooi.search.service.SearchService;
import nl.overheid.koop.plooi.search.service.ZoekApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SearchController implements ZoekApi {

    private SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public ResponseEntity<SearchResponse> search(@Valid @RequestBody SearchRequest searchRequest) {
        SearchResponse searchResponse = this.searchService.handleSearchResponse(searchRequest);

        return ResponseEntity.ok(searchResponse);
    }

}

package nl.overheid.koop.plooi.search.client;

import nl.overheid.koop.plooi.search.model.SearchRequest;
import nl.overheid.koop.plooi.search.model.SearchResponse;

public class TestSearchClient {

    private static final String SEARCH_API_URL = "SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS";

    public static void main(String[] args) {
        SearchClient client = new SearchClient(SEARCH_API_URL);

        SearchRequest searchRequest = new SearchRequest();
        SearchResponse searchResponse = client.search(searchRequest, "v1");

        System.out.println(searchResponse);
        System.out.println(searchResponse.getTotaal());

        String searchResponseJSON = client.search("{}", "v1");

        System.out.println(searchResponseJSON);
    }

}

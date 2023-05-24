package nl.overheid.koop.plooi.search.client;

import java.io.IOException;
import java.net.http.HttpRequest;
import nl.overheid.koop.plooi.model.data.util.JSONBinding;
import nl.overheid.koop.plooi.model.data.util.PlooiBinding;
import nl.overheid.koop.plooi.search.model.SearchRequest;
import nl.overheid.koop.plooi.search.model.SearchResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SearchClient extends ApiClient {

    private static final String DEFAULT_API_VERSION = "v1";
    private static final String METHOD_PATH = "_zoek";

    private final PlooiBinding<SearchRequest> searchRequestBinding = new JSONBinding<>(SearchRequest.class);
    private final PlooiBinding<SearchResponse> searchResponseBinding = new JSONBinding<>(SearchResponse.class);

    public SearchClient(String base) {
        super(base);
    }

    public SearchResponse search(SearchRequest searchRequest, String apiVersion) {
        return this.handleSearch(searchRequest, apiVersion);
    }

    public String search(String searchRequest, String apiVersion) {
        return this.searchResponseBinding.marshalToString(this.handleSearch(searchRequest, apiVersion));
    }

    private SearchResponse handleSearch(SearchRequest searchRequest, String apiVersion) {
        var requestBody = this.searchRequestBinding.marshalToString(searchRequest);
        SearchResponse searchResponse = null;

        String func = "postSearch";
        try (var searchResponseStream = sendAndVerify(func, HttpRequest.newBuilder()
                .uri(getUri("/{version}/{methodPath}"
                        .replace("{version}", StringUtils.isBlank(apiVersion) ? DEFAULT_API_VERSION : apiVersion)
                        .replace("{methodPath}", METHOD_PATH)))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)))) {

            searchResponse = this.searchResponseBinding.unmarshalFromStream(searchResponseStream);
        } catch (IOException e) {
            throw ClientException.getClientException(func, e);
        }

        return searchResponse;
    }

    private SearchResponse handleSearch(String requestBody, String apiVersion) {
        SearchResponse searchResponse = null;

        String func = "postSearch";
        try (var searchResponseStream = sendAndVerify(func, HttpRequest.newBuilder()
                .uri(getUri("/{version}/{methodPath}"
                        .replace("{version}", StringUtils.isBlank(apiVersion) ? DEFAULT_API_VERSION : apiVersion)
                        .replace("{methodPath}", METHOD_PATH)))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)))) {

            searchResponse = this.searchResponseBinding.unmarshalFromStream(searchResponseStream);
        } catch (IOException e) {
            throw ClientException.getClientException(func, e);
        }

        return searchResponse;
    }

}

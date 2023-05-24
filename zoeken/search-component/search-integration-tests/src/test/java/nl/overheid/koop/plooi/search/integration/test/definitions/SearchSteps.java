package nl.overheid.koop.plooi.search.integration.test.definitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import java.util.List;
import nl.overheid.koop.plooi.search.client.SearchClient;
import org.springframework.beans.factory.annotation.Value;

public class SearchSteps {

    private static final String SEARCH_API_VERSION = "v1";

    private SearchClient searchClient;

    private String searchRequest = null;
    private String searchResponse = null;
    private DocumentContext responseDocumentContext = null;
    private JsonObjectBuilder filters = null;
    private boolean toonNietGepubliceerd = false;
    private JsonObjectBuilder searchBuilder = null;
    private JsonObjectBuilder classificatiecollectie = null;
    private JsonObjectBuilder plooiIntern = null;
    private JsonObjectBuilder mutatiedatumtijd = null;
    private JsonObjectBuilder openbaarmakingsdatum = null;

    public SearchSteps(@Value("${integration.search.service.base.url}") String searchServiceBaseUrl) {
        this.searchClient = new SearchClient(searchServiceBaseUrl);
    }

    @When("user searches on {string}")
    public void user_searches_on(String zoektekst) {
        this.searchBuilder = Json.createObjectBuilder()
                .add("zoektekst", zoektekst);
        this.filters = Json.createObjectBuilder();
        this.classificatiecollectie = Json.createObjectBuilder();
        this.plooiIntern = Json.createObjectBuilder();
        this.mutatiedatumtijd = Json.createObjectBuilder();
        this.openbaarmakingsdatum = Json.createObjectBuilder();
    }

    // filter params
    @When("filter param toonNietGepubliceerd set to {string}")
    public void filter_param_toonNietGepubliceerd_set_to(String param) {
        this.toonNietGepubliceerd = Boolean.valueOf(param.toLowerCase());
    }

    // filters
    @And("filter list on {string}")
    public void filter_list_on_filterName(String filterName, List<String> values) {
        String parentObject = filterName.split("\\.")[0];

        if (parentObject.equals("classificatiecollectie")) {
            this.classificatiecollectie.add(filterName.split("\\.")[1], handleListFilter(values));
        } else {
            this.filters.add(filterName, handleListFilter(values));
        }
    }

    private JsonArrayBuilder handleListFilter(List<String> valueList) {
        JsonArrayBuilder list = Json.createArrayBuilder();

        valueList.forEach(item -> list.add(item));

        return list;
    }

    @And("filter object on {string}: {string}")
    public void filter_object_on_filterName(String filterName, String value) {
        String parentObject = filterName.split("\\.")[0];

        if (parentObject.equals("plooiIntern")) {
            this.plooiIntern.add(filterName.split("\\.")[1], (String) value);
        }

    }

    @And("filter date on {string} van: {string}")
    public void filter_date_on_van(String dateType, String van) {
        if (dateType.equals("mutatiedatumtijd")) {
            this.mutatiedatumtijd.add("van", van);
        } else if (dateType.equals("openbaarmakingsdatum")) {
            this.openbaarmakingsdatum.add("van", van);
        }
    }

    @And("filter date on {string} van: {string} tot: {string}")
    public void filter_date_on_van_tot(String dateType, String van, String tot) {
        if (dateType.equals("mutatiedatumtijd")) {
            this.mutatiedatumtijd.add("van", van);
            this.mutatiedatumtijd.add("tot", tot);
        } else if (dateType.equals("openbaarmakingsdatum")) {
            this.openbaarmakingsdatum.add("van", van);
            this.openbaarmakingsdatum.add("tot", tot);
        }
    }

    @And("performing search")
    public void performing_search() {
        this.filters.add("classificatiecollectie", this.classificatiecollectie);
        this.filters.add("plooiIntern", this.plooiIntern);
        this.filters.add("mutatiedatumtijd", this.mutatiedatumtijd);
        this.filters.add("openbaarmakingsdatum", this.openbaarmakingsdatum);
        this.searchBuilder.add("toonNietGepubliceerd", this.toonNietGepubliceerd);
        this.searchBuilder.add("filters", this.filters);
        this.searchRequest = this.searchBuilder.build().toString();

        this.searchResponse = this.searchClient.search(this.searchRequest, SEARCH_API_VERSION);
        this.responseDocumentContext = JsonPath.parse(this.searchResponse);
    }

    @Then("ensure that pid {string} publicatiestatus is {string}")
    public void ensure_that_pid_publicatiestatus_is(String pid, String value) {
        assertEquals(value,
                this.responseDocumentContext.read("$.resultaten[?(@.plooiIntern.dcnId=='" + pid + "')].plooiIntern.publicatiestatus", List.class).get(0));
    }

    @Then("the number of search results is {int}")
    public void the_number_of_search_results_is(Integer expected) {
        assertEquals(Long.valueOf(expected), this.responseDocumentContext.read("$.totaal", Long.class));
    }

    @Then("search results include the following documents")
    public void search_results_include_the_following_documents(List<String> list) {
        List<String> pidList = this.responseDocumentContext.read("$..pid");

        for (int i = 1; i < list.size(); i++) {
            assertEquals(list.get(i), pidList.get(i - 1));
        }
    }

}

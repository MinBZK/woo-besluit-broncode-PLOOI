package nl.overheid.koop.plooi.aanleveren.zoeken.web.endpoints;

import lombok.val;
import nl.overheid.koop.plooi.aanleveren.zoeken.domain.ZoekenService;
import nl.overheid.koop.plooi.search.model.SearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZoekenEndpointTest {
    @Mock private ZoekenService zoekenService;
    @InjectMocks private ZoekenEndpoint sut;

    @Test
    void getListOfResults() {
        val pagina = 1;
        val searchResponse = new SearchResponse();
        when(zoekenService.getDocuments(pagina)).thenReturn(searchResponse);

        val response = sut.searchDocuments(pagina);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

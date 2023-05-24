package nl.overheid.koop.plooi.dcn.route.prep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PageQueryPrepTest {

    @Test
    void defaultPrep() {
        var pageQueryPrep = new PageQueryPrep()
                .setSinceDateParam("lastmodifiedsince");
        var headers = pageQueryPrep.prepare(setupHeaders());
        pageQueryPrep.prepareNext(headers);
        assertEquals("rows=25&lastmodifiedsince=20211001", headers.get(Exchange.HTTP_QUERY));
        assertNull(headers.get(Exchange.HTTP_URI));

        pageQueryPrep.prepareNext(headers);
        assertEquals("rows=25&start=25&lastmodifiedsince=20211001", headers.get(Exchange.HTTP_QUERY));

        headers.put(PageQueryPrep.DCN_OFFSET_VALUE_HEADER, Integer.valueOf(99));
        pageQueryPrep.prepareNext(headers);
        assertEquals("rows=25&start=99&lastmodifiedsince=20211001", headers.get(Exchange.HTTP_QUERY));
    }

    @Test
    void prepared() {
        var pageQueryPrep = new PageQueryPrep()
                .setBaseUrl("https://opendata.rijksoverheid.nl/v1/sources/rijksoverheid/documents")
                .setSinceDateParam("lastmodifiedsince")
                .setSinceDateFormat("yyyyMMdd")
                .setSinceDateOffset(Period.ofWeeks(1))
                .setPageOffsetParam("offset")
                .setPageSizeParam("rows")
                .setPageSize(100);
        var headers = pageQueryPrep.prepare(setupHeaders());
        pageQueryPrep.prepareNext(headers);
        assertNull(headers.get(Exchange.HTTP_QUERY));
        assertEquals("https://opendata.rijksoverheid.nl/v1/sources/rijksoverheid/documents?rows=100&lastmodifiedsince=20210924",
                headers.get(Exchange.HTTP_URI));

        pageQueryPrep.prepareNext(headers);
        assertEquals("https://opendata.rijksoverheid.nl/v1/sources/rijksoverheid/documents?rows=100&offset=100&lastmodifiedsince=20210924",
                headers.get(Exchange.HTTP_URI));
    }

    @ParameterizedTest
    @CsvSource({
            "http://mockserver:1080/repository.overheid.nl/frbr/officielepublicaties/kst/32545/kst-32545-143/1/pdf/kst-32545-143.pdf, https://repository.overheid.nl/frbr/officielepublicaties/kst/32545/kst-32545-143/1/pdf/kst-32545-143.pdf",
            "http://mockserver:1080/repository.overheid.nl/sru?operation=searchRetrieve&version=2.0&recordSchema=gzd&maximumRecords=10&query=c.product-area==%22officielepublicaties%22%20and%20(%20w.organisatietype==%22staten%20generaal%22%20and%20(%20w.documentstatus==%22Opgemaakt%22%20or%20w.documentstatus==%22Opgemaakt%20na%20onopgemaakt%22%20or%20dt.type==%22Bijlage%22%20))%20or%20(%20w.publicatienaam==%22Staatsblad%22%20)%20and%20dt.date%3E=%222021-07-01%22%20and%20dt.modified%3E=%222022-03-24%22, https://repository.overheid.nl/sru?operation=searchRetrieve&version=2.0&recordSchema=gzd&maximumRecords=10&query=c.product-area==%22officielepublicaties%22%20and%20(%20w.organisatietype==%22staten%20generaal%22%20and%20(%20w.documentstatus==%22Opgemaakt%22%20or%20w.documentstatus==%22Opgemaakt%20na%20onopgemaakt%22%20or%20dt.type==%22Bijlage%22%20))%20or%20(%20w.publicatienaam==%22Staatsblad%22%20)%20and%20dt.date%3E=%222021-07-01%22%20and%20dt.modified%3E=%222022-03-24%22",
            "http://mockserver:1080/zoekservice.overheid.nl/sru/Search?x-connection=oo&operation=searchRetrieve&version=2.0&query=type=(%22Ministerie%22%20OR%20%22Provincie%22%20OR%20%22Gemeente%22%20OR%20%22Waterschap%22)&, https://zoekservice.overheid.nl/sru/Search?x-connection=oo&operation=searchRetrieve&version=2.0&query=type=(%22Ministerie%22%20OR%20%22Provincie%22%20OR%20%22Gemeente%22%20OR%20%22Waterschap%22)&",
    })
    void mockUrl(String mocked, String toMock) {
        assertEquals(mocked, new PageQueryPrep().mockUrl(toMock, "mockserver:1080"));
    }

    private Map<String, Object> setupHeaders() {
        return new HashMap<>(Map.of(Prepping.DCN_SINCE_HEADER, LocalDateTime.of(2021, 10, 1, 10, 0)));
    }
}

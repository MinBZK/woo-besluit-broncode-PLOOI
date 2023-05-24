package nl.overheid.koop.plooi.dcn.roo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import nl.overheid.koop.plooi.dcn.route.prep.PageQueryPrep;
import nl.overheid.koop.plooi.dcn.route.prep.Prepping;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.Test;

class RooPageQueryPrepTest {

    @Test
    void rooSruQuery() {
        var pageQueryPrep = RooRoute.ROO_PAGING_QUERY;
        var headers = pageQueryPrep.prepare(
                new HashMap<>(Map.of(Prepping.DCN_SINCE_HEADER, LocalDateTime.of(2021, 10, 14, 10, 0))));
        assertNull(headers.get(Exchange.HTTP_QUERY));
        pageQueryPrep.prepareNext(headers);
        assertEquals(
                "https://zoekservice.overheid.nl/sru/Search?x-connection=oo&operation=searchRetrieve&version=2.0&"
                        + "query=type=(%22Ministerie%22%20"
                        + "OR%20%22Provincie%22%20"
                        + "OR%20%22Gemeente%22%20"
                        + "OR%20%22Waterschap%22)&maximumRecords=10",
                headers.get(Exchange.HTTP_URI));

        headers.put(PageQueryPrep.DCN_OFFSET_VALUE_HEADER, 11);
        pageQueryPrep.prepareNext(headers);
        assertEquals(
                "https://zoekservice.overheid.nl/sru/Search?x-connection=oo&operation=searchRetrieve&version=2.0&"
                        + "query=type=(%22Ministerie%22%20"
                        + "OR%20%22Provincie%22%20"
                        + "OR%20%22Gemeente%22%20"
                        + "OR%20%22Waterschap%22)&maximumRecords=10&startRecord=11",
                headers.get(Exchange.HTTP_URI));
    }
}

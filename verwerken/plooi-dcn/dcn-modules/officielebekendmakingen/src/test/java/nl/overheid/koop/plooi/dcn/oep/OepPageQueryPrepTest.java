package nl.overheid.koop.plooi.dcn.oep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import nl.overheid.koop.plooi.dcn.route.prep.PageQueryPrep;
import nl.overheid.koop.plooi.dcn.route.prep.Prepping;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.Test;

class OepPageQueryPrepTest {

    @Test
    void oepSruQuery() {
        var pageQueryPrep = OepSruRoute.OEP_PAGING_QUERY;
        var headers = pageQueryPrep.prepare(
                new HashMap<>(Map.of(Prepping.DCN_SINCE_HEADER, LocalDateTime.of(2021, 10, 14, 10, 0))));
        assertNull(headers.get(Exchange.HTTP_QUERY));
        pageQueryPrep.prepareNext(headers);
        assertEquals(
                "https://repository.overheid.nl/sru"
                + "?operation=searchRetrieve"
                + "&version=2.0"
                + "&recordSchema=gzd"
                + "&maximumRecords=10"
                + "&query=c.product-area==\"officielepublicaties\" "
                + "and ( "
                    + "w.organisatietype==\"staten generaal\" "
                    + "and ( "
                        + "w.documentstatus==\"Opgemaakt\" "
                        + "or w.documentstatus==\"Opgemaakt na onopgemaakt\" "
                        + "or dt.type==\"Bijlage\" "
                    + ")"
                + ") or ( "
                    + "w.publicatienaam==\"Staatsblad\" "
//                    + "or w.publicatienaam==\"Staatscourant\""
                + ") "
                + "and dt.date>=\"2021-07-01\" "
                + "and dt.modified>=\"2021-10-14\"",
                headers.get(Exchange.HTTP_URI));

        headers.put(PageQueryPrep.DCN_OFFSET_VALUE_HEADER, Integer.valueOf(11));
        pageQueryPrep.prepareNext(headers);
        assertEquals(
                "https://repository.overheid.nl/sru"
                + "?operation=searchRetrieve"
                + "&version=2.0"
                + "&recordSchema=gzd"
                + "&maximumRecords=10"
                + "&startRecord=11"
                + "&query=c.product-area==\"officielepublicaties\" "
                + "and ( "
                    + "w.organisatietype==\"staten generaal\" "
                    + "and ( "
                        + "w.documentstatus==\"Opgemaakt\" "
                        + "or w.documentstatus==\"Opgemaakt na onopgemaakt\" "
                        + "or dt.type==\"Bijlage\" "
                    + ")"
                + ") or ( "
                    + "w.publicatienaam==\"Staatsblad\" "
//                    + "or w.publicatienaam==\"Staatscourant\""
                + ") "
                + "and dt.date>=\"2021-07-01\" "
                + "and dt.modified>=\"2021-10-14\"",
                headers.get(Exchange.HTTP_URI));
    }
}

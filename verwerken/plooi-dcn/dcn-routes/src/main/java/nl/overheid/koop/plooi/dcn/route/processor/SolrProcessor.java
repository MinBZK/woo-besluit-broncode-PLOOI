package nl.overheid.koop.plooi.dcn.route.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.overheid.koop.plooi.dcn.solr.SolrDocumentService;
import nl.overheid.koop.plooi.dcn.solr.models.SolrSearchRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrProcessor implements Processor {

    private static final String DCN_ID = "dcn_id";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SolrDocumentService solrDocumentService;

    public SolrProcessor(SolrDocumentService solrDocumentService) {
        this.solrDocumentService = solrDocumentService;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        var timer = ProcessorMetrics.start();
        var cursorMark = exchange.getIn().getHeader("cursorMark", "*", String.class);
        var query = exchange.getIn().getHeader("query", SolrSearchRequest.class);
        var queryResponse = this.solrDocumentService.getPageableDocument(query, cursorMark, new String[] { DCN_ID });
        if (queryResponse.getNextCursorMark().equalsIgnoreCase(cursorMark)) {
            this.logger.debug("No more results");
            exchange.getIn().setBody(new ArrayList<>());
        } else {
            exchange.getIn().setHeader("cursorMark", queryResponse.getNextCursorMark());
            List<String> ids = queryResponse.getResults()
                    .stream()
                    .filter(sd -> sd.get(DCN_ID) != null)
                    .map(sd -> sd.get(DCN_ID))
                    .map(Object::toString)
                    .map(id -> id.replaceAll("_1$", "")) // Temporary hack so we can reindex legacy documents
                    .toList();
            this.logger.debug("page contains {} documents", ids.size());
            exchange.getIn().setBody(ids);
        }
        timer.stop(this, this.solrDocumentService, Optional.empty());
    }
}

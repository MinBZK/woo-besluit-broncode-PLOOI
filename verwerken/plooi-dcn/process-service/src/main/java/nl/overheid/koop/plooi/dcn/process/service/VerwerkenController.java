package nl.overheid.koop.plooi.dcn.process.service;

import java.util.List;
import nl.overheid.koop.plooi.dcn.process.data.VerwerkingActies;
import nl.overheid.koop.plooi.dcn.route.common.CommonRoute;
import nl.overheid.koop.plooi.dcn.route.common.Routing;
import nl.overheid.koop.plooi.dcn.route.common.SolrRoute;
import nl.overheid.koop.plooi.dcn.route.prep.IngressExecutionPrep;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class VerwerkenController implements VerwerkenApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ProducerTemplate producerTemplate;

    public VerwerkenController(CamelContext camelContext) {
        this.producerTemplate = camelContext.createProducerTemplate();
    }

    @Override
    public ResponseEntity<Void> process(VerwerkingActies actie, String procesId, List<String> dcnIds) {
        this.logger.info("Executing {} action on {} documents for process {}", actie, dcnIds.size(), procesId);
        dcnIds.forEach(dcnId -> this.producerTemplate.sendBodyAndHeader(Routing.external(
                switch (actie) {
                    case VERWERKING -> CommonRoute.DCN_PROCESS_DOCUMENT;
                    case INTREKKING -> CommonRoute.DCN_DELETE_DOCUMENT;
                    case HERPUBLICATIE -> CommonRoute.DCN_UNDELETE_DOCUMENT;
                    case INDEXERING -> SolrRoute.SOLR_INDEX_ROUTE;
                }),
                dcnId,
                IngressExecutionPrep.DCN_EXECUTION_HEADER,
                procesId));
        return ResponseEntity.ok(null);
    }
}

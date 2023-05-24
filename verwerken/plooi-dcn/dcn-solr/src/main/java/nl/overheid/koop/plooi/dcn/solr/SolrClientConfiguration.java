package nl.overheid.koop.plooi.dcn.solr;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SolrClientConfiguration {

    @Value("${dcn.solr.url:http://localhost:8983/solr}")
    private String url;

    @Bean
    ConcurrentUpdateSolrClient concurrentUpdateSolrClient() {
        /*
         * SolrJ introduced a stall prevention timeout to fix an issue with (core to core) replication, which however causes
         * unwanted exception in our pipeline. Therefore we set it to a high value preventing "Request processing has stalled
         * for 15041ms with 10 remaining elements in the queue." IOExceptions.
         */
        System.setProperty("solr.cloud.client.stallTime", String.valueOf(90000));
        return new ConcurrentUpdateSolrClient.Builder(this.url).build();
    }
}

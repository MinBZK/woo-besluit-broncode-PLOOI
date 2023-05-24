package nl.overheid.koop.plooi.search.service;

import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolrClientConfiguration {

    @Bean
    Http2SolrClient httpSolrClient(@Value("${solr.url}") String url) {
        return new Http2SolrClient.Builder(url).build();
    }

}

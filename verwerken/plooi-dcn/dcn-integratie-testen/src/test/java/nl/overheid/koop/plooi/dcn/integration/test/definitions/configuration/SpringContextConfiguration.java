package nl.overheid.koop.plooi.dcn.integration.test.definitions.configuration;

import io.cucumber.spring.CucumberContextConfiguration;
import nl.overheid.koop.plooi.dcn.integration.test.client.actuator.ActuatorClient;
import nl.overheid.koop.plooi.dcn.integration.test.client.dcnadmin.DcnAdminClient;
import nl.overheid.koop.plooi.dcn.integration.test.client.solr.SolrClient;
import nl.overheid.koop.plooi.dcn.integration.test.util.Retry;
import org.springframework.test.context.ContextConfiguration;

/*
 * This is where SpringContext is configured.
 *
 * Add all autowired candidates to the SpringBootTest classes parameter list
 * so that the autowired candidate implementations can be instantiated.
 *
 * BeanConfiguration class is where Spring singleton beans are configured.
 *
 */
@CucumberContextConfiguration
@ContextConfiguration(classes = { BeanConfiguration.class, DcnAdminClient.class, ActuatorClient.class, SolrClient.class, Retry.class })
public class SpringContextConfiguration {

}

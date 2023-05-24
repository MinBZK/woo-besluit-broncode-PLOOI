package nl.overheid.koop.plooi.search.integration.test.definitions.configuration;

import io.cucumber.spring.CucumberContextConfiguration;
import nl.overheid.koop.plooi.test.util.Retry;
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
@ContextConfiguration(classes = { BeanConfiguration.class, Retry.class })
public class SpringContextConfiguration {

}

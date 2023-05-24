package nl.overheid.koop.plooi.dcn.integration.test;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "nl.overheid.koop.plooi.dcn.integration.test.definitions"),
        @ConfigurationParameter(key = Constants.ANSI_COLORS_DISABLED_PROPERTY_NAME, value = "true"),
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,json:target/Cucumber/report.json,html:target/Cucumber/report.html,junit:target/Cucumber/report.xml"),
})
public class CucumberIT {}

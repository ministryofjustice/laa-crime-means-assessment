package uk.gov.justice.laa.crime.meansassessment.cucumber;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("uk/gov/justice/laa/crime/meansassessment/cucumber")
@IncludeEngines("cucumber")
@ConfigurationParameters({
    @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "uk.gov.justice.laa.crime.meansassessment.cucumber"),
    @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, junit:build/test-results/TEST-cucumber.xml")
})
public class StatelessCucumberTest {}

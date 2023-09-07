package uk.gov.justice.laa.crime.meansassessment.cucumber;

import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Junit cucumber test suite")
@SelectClasspathResource(value = "uk.gov.justice.laa.crime.meansassessment")
@SelectPackages("uk.gov.justice.laa.crime.meansassessment")
public class RunCucumberTest {
}

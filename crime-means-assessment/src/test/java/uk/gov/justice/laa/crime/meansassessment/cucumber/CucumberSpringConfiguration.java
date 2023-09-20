package uk.gov.justice.laa.crime.meansassessment.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.gov.justice.laa.crime.meansassessment.config.CrimeMeansAssessmentTestConfiguration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Import(CrimeMeansAssessmentTestConfiguration.class)
public class CucumberSpringConfiguration {
}

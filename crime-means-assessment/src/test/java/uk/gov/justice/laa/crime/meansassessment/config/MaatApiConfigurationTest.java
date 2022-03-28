package uk.gov.justice.laa.crime.meansassessment.config;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties(value = MaatApiConfiguration.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
public class MaatApiConfigurationTest {

    @Autowired
    @Qualifier("test_configuration")
    private MaatApiConfiguration configuration;

    @Configuration
    public static class MaatApiConfigurationFactory {
        @Bean(name = "test_configuration")
        public MaatApiConfiguration getDefaultConfiguration() {
            return new MaatApiConfiguration();
        }
    }

    @Test
    public void givenUserDefinedPOJO_whenBindingYMLConfigFile_thenAllFieldsAreSet() {
        assertThat("search-url").isEqualTo(configuration.getFinancialAssessmentEndpoints().getSearchUrl());
        assertThat("create-url").isEqualTo(configuration.getFinancialAssessmentEndpoints().getCreateUrl());
        assertThat("update-url").isEqualTo(configuration.getFinancialAssessmentEndpoints().getUpdateUrl());

        assertThat(false).isEqualTo(configuration.isOAuthEnabled());

        assertThat("role-action-url").isEqualTo(configuration.getValidationEndpoints().getRoleActionUrl());
        assertThat("new-work-reason-url").isEqualTo(configuration.getValidationEndpoints().getNewWorkReasonUrl());
        assertThat("reservation-url").isEqualTo(configuration.getValidationEndpoints().getReservationsUrl());
        assertThat("outstanding-assessments-url").isEqualTo(configuration.getValidationEndpoints().getOutstandingAssessmentsUrl());
    }

    @Test
    public void givenDefinedFinancialAssessmentEndpoints_whenGetByRequestTypeIsInvoked_thenCorrectEndpointIsReturned() {
        assertThat("create-url").isEqualTo(configuration.getFinancialAssessmentEndpoints().getByRequestType(AssessmentRequestType.CREATE));
    }
}

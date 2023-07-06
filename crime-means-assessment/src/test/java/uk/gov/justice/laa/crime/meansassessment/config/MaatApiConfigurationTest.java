package uk.gov.justice.laa.crime.meansassessment.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
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
        assertThat(buildUrl("search-url")).isEqualTo(configuration.getFinancialAssessmentEndpoints().getSearchUrl());
        assertThat(buildUrl("create-url")).isEqualTo(configuration.getFinancialAssessmentEndpoints().getCreateUrl());
        assertThat(buildUrl("update-url")).isEqualTo(configuration.getFinancialAssessmentEndpoints().getUpdateUrl());

        assertThat(false).isEqualTo(configuration.isOAuthEnabled());

        assertThat(buildUrl("role-action-url")).isEqualTo(configuration.getValidationEndpoints().getRoleActionUrl());
        assertThat(buildUrl("new-work-reason-url")).isEqualTo(configuration.getValidationEndpoints().getNewWorkReasonUrl());
        assertThat(buildUrl("reservation-url")).isEqualTo(configuration.getValidationEndpoints().getReservationsUrl());
        assertThat(buildUrl("outstanding-assessments-url")).isEqualTo(configuration.getValidationEndpoints().getOutstandingAssessmentsUrl());
    }

    @Test
    public void givenDefinedFinancialAssessmentEndpoints_whenGetByRequestTypeIsInvoked_thenCorrectEndpointIsReturned() {
        assertThat(buildUrl("create-url")).isEqualTo(configuration.getFinancialAssessmentEndpoints().getByRequestType(AssessmentRequestType.CREATE));
    }

    @Test
    public void givenDefinedPostProcessingUrl_whenGetPostProcessingUrl_thenCorrectEndpointIsReturned() {
        assertThat(buildUrl("post-processing/{repId}")).isEqualTo(configuration.getPostProcessingUrl());
    }

    private String buildUrl(String url) {
        return String.format("http://localhost:9999/api/internal/v1/assessment/%s", url);
    }
}

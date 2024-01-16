package uk.gov.justice.laa.crime.meansassessment.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.justice.laa.crime.enums.RequestType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = MaatApiConfiguration.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
class MaatApiConfigurationTest {

    @Autowired
    @Qualifier("test_configuration")
    private MaatApiConfiguration configuration;

    @Test
    void givenUserDefinedPOJO_whenBindingYMLConfigFile_thenAllFieldsAreSet() {
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
    void givenDefinedFinancialAssessmentEndpoints_whenGetByRequestTypeIsInvoked_thenCorrectEndpointIsReturned() {
        assertThat(buildUrl("create-url")).isEqualTo(configuration.getFinancialAssessmentEndpoints().getByRequestType(RequestType.CREATE));
    }

    @Test
    void givenDefinedPostProcessingUrl_whenGetPostProcessingUrl_thenCorrectEndpointIsReturned() {
        assertThat(buildUrl("post-processing/{repId}")).isEqualTo(configuration.getPostProcessingUrl());
    }

    private String buildUrl(String url) {
        return String.format("http://localhost:9999/api/internal/v1/assessment/%s", url);
    }

    @Configuration
    static class MaatApiConfigurationFactory {
        @Bean(name = "test_configuration")
        MaatApiConfiguration getDefaultConfiguration() {
            return new MaatApiConfiguration();
        }
    }
}

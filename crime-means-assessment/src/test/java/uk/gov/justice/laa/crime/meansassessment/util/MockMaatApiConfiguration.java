package uk.gov.justice.laa.crime.meansassessment.util;

import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;

public class MockMaatApiConfiguration {

    public static final String MOCK_BASE_URL = "http://localhost:9999";
    public static final String MOCK_ASSESSMENT_BASE_URL = MOCK_BASE_URL.concat("/financial-assessments");

    public static MaatApiConfiguration getConfiguration(int port) {

        MaatApiConfiguration configuration = new MaatApiConfiguration();

        MaatApiConfiguration.IOJAppealEndpoints iojEndpoints =
                new MaatApiConfiguration.IOJAppealEndpoints(MOCK_BASE_URL + "/ioj-appeal/{repId}");
        MaatApiConfiguration.HardshipReviewEndpoints hardshipEndpoints =
                new MaatApiConfiguration.HardshipReviewEndpoints(MOCK_BASE_URL + "/hardship/{repId}");
        MaatApiConfiguration.PassportAssessmentEndpoints passportEndpoints =
                new MaatApiConfiguration.PassportAssessmentEndpoints(MOCK_BASE_URL + "/passport-assessments/{repId}");
        MaatApiConfiguration.FinancialAssessmentEndpoints financialAssessmentEndpoints =
                new MaatApiConfiguration.FinancialAssessmentEndpoints(
                        MOCK_ASSESSMENT_BASE_URL + "/{financialAssessmentId}",
                        MOCK_ASSESSMENT_BASE_URL,
                        MOCK_ASSESSMENT_BASE_URL + "/{financialAssessmentId}",
                        MOCK_ASSESSMENT_BASE_URL + "/rollback/{financialAssessmentId}"
                );
        MaatApiConfiguration.RepOrderEndpoints repOrderEndpoints =
                new MaatApiConfiguration.RepOrderEndpoints(
                        MOCK_BASE_URL + "/rep-orders",
                        MOCK_BASE_URL + "/rep-orders/update-date-completed"
                );
        MaatApiConfiguration.ValidationEndpoints validationEndpoints = new MaatApiConfiguration.ValidationEndpoints(
                MOCK_BASE_URL + "/authorization/users/{username}/actions/{action}",
                MOCK_BASE_URL + "/authorization/users/{username}/work-reasons/{nworCode}",
                MOCK_BASE_URL + "/authorization/users/{username}/reservations/{reservationId}/sessions/{sessionId}",
                MOCK_BASE_URL + "/financial-assessments/check-outstanding/{repId}"
        );
        configuration.setBaseUrl(
                String.format("http://localhost:%s", port)
        );
        configuration.setOAuthEnabled(false);
        configuration.setPostProcessingUrl("/post-processing/{repId}");
        configuration.setIojAppealEndpoints(iojEndpoints);
        configuration.setHardshipReviewEndpoints(hardshipEndpoints);
        configuration.setPassportAssessmentEndpoints(passportEndpoints);
        configuration.setRepOrderEndpoints(repOrderEndpoints);
        configuration.setValidationEndpoints(validationEndpoints);
        configuration.setFinancialAssessmentEndpoints(financialAssessmentEndpoints);

        return configuration;
    }
}

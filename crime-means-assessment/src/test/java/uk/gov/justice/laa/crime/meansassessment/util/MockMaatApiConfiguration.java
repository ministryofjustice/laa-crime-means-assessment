package uk.gov.justice.laa.crime.meansassessment.util;

import uk.gov.justice.laa.crime.meansassessment.config.MaatApiConfiguration;

public class MockMaatApiConfiguration {

    public static MaatApiConfiguration getConfiguration(int port) {

        MaatApiConfiguration configuration = new MaatApiConfiguration();

        MaatApiConfiguration.IOJAppealEndpoints iojEndpoints =
                new MaatApiConfiguration.IOJAppealEndpoints("/ioj-appeal/{repId}");
        MaatApiConfiguration.HardshipReviewEndpoints hardshipEndpoints =
                new MaatApiConfiguration.HardshipReviewEndpoints("/hardship/{repId}");
        MaatApiConfiguration.PassportAssessmentEndpoints passportEndpoints =
                new MaatApiConfiguration.PassportAssessmentEndpoints("/passport-assessments/{repId}");
        MaatApiConfiguration.FinancialAssessmentEndpoints financialAssessmentEndpoints =
                new MaatApiConfiguration.FinancialAssessmentEndpoints(
                        "/financial-assessments/{financialAssessmentId}",
                        "/financial-assessments/",
                        "/financial-assessments/{financialAssessmentId}"
                );
        MaatApiConfiguration.RepOrderEndpoints repOrderEndpoints =
                new MaatApiConfiguration.RepOrderEndpoints(
                        "/rep-orders",
                        "/rep-orders/update-date-completed"
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
        configuration.setFinancialAssessmentEndpoints(financialAssessmentEndpoints);

        return configuration;
    }
}

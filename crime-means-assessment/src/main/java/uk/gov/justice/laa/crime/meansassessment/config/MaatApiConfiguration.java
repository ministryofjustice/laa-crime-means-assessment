package uk.gov.justice.laa.crime.meansassessment.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "maat-api")
public class MaatApiConfiguration {
    /**
     * The API's Base URL
     */
    @NotNull
    private String baseUrl;

    /**
     * Determines whether oAuth authentication is enabled
     */
    @NotNull
    private boolean oAuthEnabled;

    /**
     * Defines validation endpoint URLs
     */
    @NotNull
    private ValidationEndpoints validationEndpoints;

    @NotNull
    private FinancialAssessmentEndpoints financialAssessmentEndpoints;

    @NotNull
    private PassportAssessmentEndpoints passportAssessmentEndpoints;

    @Getter
    @AllArgsConstructor
    @Setter
    @NoArgsConstructor
    public static class ValidationEndpoints {
        /**
         * Validate Role Action Endpoint URL
         */
        @NotNull
        private String roleActionUrl;

        /**
         * Validate New Work Reason Endpoint URL
         */
        @NotNull
        private String newWorkReasonUrl;

        /**
         * Validate Reservation Endpoint URL
         */
        @NotNull
        private String reservationsUrl;

        /**
         * Check Outstanding Assessments Endpoint URL
         */
        @NotNull
        private String outstandingAssessmentsUrl;
    }

    @Getter
    @AllArgsConstructor
    @Setter
    @NoArgsConstructor
    public static class FinancialAssessmentEndpoints {
        /**
         * Find assesssment URL
         */
        @NotNull
        private String findUrl;

        /**
         * Create assessment URL
         */
        @NotNull
        private String createUrl;

        /**
         * Update assessment URL
         */
        @NotNull
        private String updateUrl;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PassportAssessmentEndpoints {

        @NotNull
        private String findUrl;
    }
}

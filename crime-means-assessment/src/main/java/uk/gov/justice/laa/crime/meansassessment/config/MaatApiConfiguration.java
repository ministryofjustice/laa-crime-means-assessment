package uk.gov.justice.laa.crime.meansassessment.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentRequestType;

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

    /**
     * Defines financial assessment endpoint URLs
     */
    @NotNull
    private FinancialAssessmentEndpoints financialAssessmentEndpoints;

    @NotNull
    private PassportAssessmentEndpoints passportAssessmentEndpoints;

    @NotNull
    private HardshipReviewEndpoints hardshipReviewEndpoints;

    @NotNull
    private IOJAppealEndpoints iojAppealEndpoints;

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
         * Find assessment URL
         */
        @NotNull
        private String searchUrl;

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

        @NotNull
        private String createHistoryUrl;

        public String getByRequestType(AssessmentRequestType requestType) {
            return (requestType.equals(AssessmentRequestType.CREATE)) ? createUrl : updateUrl;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PassportAssessmentEndpoints {

        @NotNull
        private String findUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HardshipReviewEndpoints {

        @NotNull
        private String findUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IOJAppealEndpoints {

        @NotNull
        private String findUrl;
    }

}

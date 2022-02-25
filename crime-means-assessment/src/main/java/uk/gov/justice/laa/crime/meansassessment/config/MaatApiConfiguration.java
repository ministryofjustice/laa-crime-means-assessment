package uk.gov.justice.laa.crime.meansassessment.config;

import lombok.Data;
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
     * The Financial Assessments Endpoint URL
     */
    @NotNull
    private String financialAssessmentUrl;
}

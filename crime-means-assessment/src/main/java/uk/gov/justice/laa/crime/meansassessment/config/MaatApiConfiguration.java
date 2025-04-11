package uk.gov.justice.laa.crime.meansassessment.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "maat-api")
public class MaatApiConfiguration {
    /**
     * The API's Base URL
     */
    @NotNull
    private String baseUrl;

    private String registrationId;

}

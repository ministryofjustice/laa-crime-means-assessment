package uk.gov.justice.laa.crime.meansassessment.config;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "services")
public class ServicesConfiguration {

    @NotNull
    private MaatApi maatApi;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaatApi {
        /**
         * The API's Base URL
         */
        @NotNull
        private String baseUrl;

        @NotNull
        private String registrationId;

    }

}

package uk.gov.justice.laa.crime.meansassessment.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "features")
public class FeaturesConfiguration {

    @NotNull
    private boolean dateCompletionEnabled;
}

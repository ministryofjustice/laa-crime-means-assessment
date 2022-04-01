package uk.gov.justice.laa.crime.meansassessment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ConfigurationProperties(prefix = "retry-config")
public class RetryConfiguration {
    @NotNull
    private Integer maxRetries;

    @NotNull
    private Integer minBackOffPeriod;
}

package uk.gov.justice.laa.crime.meansassessment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "aws.sqs")
public class SqsProperties {

    private String region;
    private String accesskey;
    private String secretkey;

}


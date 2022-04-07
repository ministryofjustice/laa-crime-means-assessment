package uk.gov.justice.laa.crime.meansassessment.config;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@EnableConfigurationProperties({ RetryConfiguration.class })
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
public class RetryConfigurationTest {

    @Autowired
    @Qualifier("test_retry_config")
    private RetryConfiguration retryConfiguration;

    @Configuration
    public static class ConfigurationFactory {
        @Bean("test_retry_config")
        public RetryConfiguration getDefaultConfiguration() {
            return new RetryConfiguration();
        }
    }

    @Test
    public void givenUserDefinedPOJO_whenBindingYMLConfigFile_thenAllFieldsAreSet() {
        SoftAssertions.assertSoftly(softly -> {
            assertEquals(2, retryConfiguration.getMaxRetries());
            assertEquals(3, retryConfiguration.getMinBackOffPeriod());
            assertEquals(0.5, retryConfiguration.getJitterValue());
        });
    }
}

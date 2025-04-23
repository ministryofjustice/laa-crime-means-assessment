package uk.gov.justice.laa.crime.meansassessment.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = ServicesConfiguration.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
class ServicesConfigurationTest {

    @Autowired
    @Qualifier("test_configuration")
    private ServicesConfiguration configuration;

    @Test
    void givenUserDefinedPOJO_whenBindingYMLConfigFile_thenAllFieldsAreSet() {

        assertThat("http://localhost:9999/api/internal/v1/assessment").isEqualTo(configuration.getBaseUrl());
        assertThat("maat-api").isEqualTo(configuration.getRegistrationId());
    }

    @Configuration
    static class MaatApiConfigurationFactory {
        @Bean(name = "test_configuration")
        ServicesConfiguration getDefaultConfiguration() {
            return new ServicesConfiguration();
        }
    }
}

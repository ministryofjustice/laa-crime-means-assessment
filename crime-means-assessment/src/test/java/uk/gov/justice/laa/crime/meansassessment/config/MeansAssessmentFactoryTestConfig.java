package uk.gov.justice.laa.crime.meansassessment.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.crime.meansassessment.factory.MeansAssessmentServiceFactory;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentCriteriaChildWeightingService;
import uk.gov.justice.laa.crime.meansassessment.service.FullMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.InitMeansAssessmentService;

@Configuration
@ExtendWith(MockitoExtension.class)
public class MeansAssessmentFactoryTestConfig {

    @MockitoBean
    private AssessmentCriteriaChildWeightingService childWeightingService;

    @Bean
    public InitMeansAssessmentService initService() {
        return new InitMeansAssessmentService(childWeightingService);
    }

    @Bean
    public FullMeansAssessmentService fullService() {
        return new FullMeansAssessmentService(childWeightingService);
    }

    @Bean
    public MeansAssessmentServiceFactory factory(ApplicationContext context) {
        return new MeansAssessmentServiceFactory(context);
    }
}

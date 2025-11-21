package uk.gov.justice.laa.crime.meansassessment.factory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.config.MeansAssessmentFactoryTestConfig;
import uk.gov.justice.laa.crime.meansassessment.service.FullMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.InitMeansAssessmentService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MeansAssessmentFactoryTestConfig.class})
class MeansAssessmentServiceFactoryTest {

    @Autowired
    private MeansAssessmentServiceFactory factory;

    @Test
    void givenInitAssessmentType_whenGetServiceIsInvoked_thenInitAssessmentServiceIsReturned() {
        assertThat(factory.getService(AssessmentType.INIT))
                .isInstanceOf(InitMeansAssessmentService.class);
    }

    @Test
    void givenFullAssessmentType_whenGetServiceIsInvoked_thenFullAssessmentServiceIsReturned() {
        assertThat(factory.getService(AssessmentType.FULL))
                .isInstanceOf(FullMeansAssessmentService.class);
    }
}

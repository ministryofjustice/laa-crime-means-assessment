package uk.gov.justice.laa.crime.meansassessment.factory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.config.CrimeMeansAssessmentTestConfiguration;
import uk.gov.justice.laa.crime.meansassessment.service.FullMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.InitMeansAssessmentService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@Import(CrimeMeansAssessmentTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureObservability
class MeansAssessmentServiceFactoryTest {

    @Autowired
    private MeansAssessmentServiceFactory meansAssessmentServiceFactory;

    @Test
    void givenInitAssessmentType_whenGetServiceIsInvoked_thenInitAssessmentServiceIsReturned() {
        assertThat(meansAssessmentServiceFactory.getService(AssessmentType.INIT))
                .isInstanceOf(InitMeansAssessmentService.class);
    }

    @Test
    void givenFullAssessmentType_whenGetServiceIsInvoked_thenFullAssessmentServiceIsReturned() {
        assertThat(meansAssessmentServiceFactory.getService(AssessmentType.FULL))
                .isInstanceOf(FullMeansAssessmentService.class);
    }
}

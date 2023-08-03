package uk.gov.justice.laa.crime.meansassessment.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.justice.laa.crime.meansassessment.service.FullMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.InitMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest()
@ExtendWith(SpringExtension.class)
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

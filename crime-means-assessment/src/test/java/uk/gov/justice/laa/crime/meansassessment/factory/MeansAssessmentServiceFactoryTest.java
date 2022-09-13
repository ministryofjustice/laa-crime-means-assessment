package uk.gov.justice.laa.crime.meansassessment.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.justice.laa.crime.meansassessment.service.FullMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.InitMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.AssessmentType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class MeansAssessmentServiceFactoryTest {

    @Autowired
    private MeansAssessmentServiceFactory meansAssessmentServiceFactory;

    @Test
    public void givenInitAssessmentType_whenGetServiceIsInvoked_thenInitAssessmentServiceIsReturned() {
        assertThat(meansAssessmentServiceFactory.getService(AssessmentType.INIT))
                .isInstanceOf(InitMeansAssessmentService.class);
    }

    @Test
    public void givenFullAssessmentType_whenGetServiceIsInvoked_thenFullAssessmentServiceIsReturned() {
        assertThat(meansAssessmentServiceFactory.getService(AssessmentType.FULL))
                .isInstanceOf(FullMeansAssessmentService.class);
    }
}

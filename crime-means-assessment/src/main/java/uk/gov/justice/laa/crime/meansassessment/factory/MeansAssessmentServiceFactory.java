package uk.gov.justice.laa.crime.meansassessment.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.meansassessment.service.AssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.FullMeansAssessmentService;
import uk.gov.justice.laa.crime.meansassessment.service.InitMeansAssessmentService;

@Component
@RequiredArgsConstructor
public class MeansAssessmentServiceFactory {

    private final ApplicationContext applicationContext;

    public AssessmentService getService(AssessmentType assessmentType) {
        if (AssessmentType.INIT.equals(assessmentType)) {
            return applicationContext.getBean(InitMeansAssessmentService.class);
        } else {
            return applicationContext.getBean(FullMeansAssessmentService.class);
        }
    }
}

package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.validation.service.MeansAssessmentValidationService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final MeansAssessmentValidationService meansAssessmentValidationService;

    public ApiCreateMeansAssessmentResponse createInitialAssessment(ApiCreateMeansAssessmentRequest meansAssessment) {
        log.info("Create initial means assessment - Start");
        meansAssessmentValidationService.validate(meansAssessment);

        log.info("Validation completed for Rep ID {}", meansAssessment.getRepId());
        // TODO check the type of assessment
        /*
        * if(INIT){
        * Call --> Initial Assessment service  (INIT Assessment validation + Calculation)
        * }else{
        * Call Full Assessment  service  -->   //TODO - This is where we will invoke services to do calcualtion upon successful validation
        * }
        * */





        // TODO - Few post processing needs to occur - Create a Post Assessment Service
                //TODO - process txn data - call MAAT API to perform CRUD on financial_assessments, fin_assessment_details,
                // fin_ass_child_weightings
                // process Old Assessment
                // full_assessment_available, post_assessment_processing,get_assessments_summary


        return null;
    }
}

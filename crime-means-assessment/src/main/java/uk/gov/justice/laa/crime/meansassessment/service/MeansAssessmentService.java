package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.client.AuthorisationMeansAssessmentClient;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.exception.MeansAssessmentValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.AuthorizationResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.*;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentResponse;
import uk.gov.justice.laa.crime.meansassessment.validation.service.MeansAssessmentValidationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final MeansAssessmentValidationService meansAssessmentValidationService;
    private final AuthorisationMeansAssessmentClient workReasonsClient;


    protected List<AssessmentCriteriaEntity> getAssessmentCriteria(LocalDateTime assessmentDate, boolean hasPartner, boolean contraryInterest) throws AssessmentCriteriaNotFoundException {
        List<AssessmentCriteriaEntity> assessmentCriteriaForDate = assessmentCriteriaRepository.findAssessmentCriteriaForDate(assessmentDate);
        if(!assessmentCriteriaForDate.isEmpty()){
            // If there is no partner or there is a partner with contrary interest, set partnerWeightingFactor to null
            if(!hasPartner ||  contraryInterest){
                assessmentCriteriaForDate.forEach(ac -> ac.setPartnerWeightingFactor(null));
            }
            return assessmentCriteriaForDate;
        } else {
            log.error("No Assessment Criteria found for date {}", assessmentDate);
            throw new AssessmentCriteriaNotFoundException(String.format("No Assessment Criteria found for date %s",assessmentDate));
        }
    }

    public void createInitialAssessment(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) throws AssessmentCriteriaNotFoundException, MeansAssessmentValidationException {
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

        if (apiCreateMeansAssessmentRequest.getAssessmentDate() == null
                || apiCreateMeansAssessmentRequest.getNewWorkReason() == null
                || apiCreateMeansAssessmentRequest.getNewWorkReason().getCode() == null) {

            throw new AssessmentCriteriaNotFoundException("-20245,'Null mandatory fields'");
        }

        AuthorizationResponse authorizationResponse = workReasonsClient.checkWorkReasonStatus(apiCreateMeansAssessmentRequest.getNewWorkReason().getCode());

        if (!authorizationResponse.isResult()
                || apiCreateMeansAssessmentRequest.getReviewType().getCode() == null
                || apiCreateMeansAssessmentRequest.getReviewType().getCode().isEmpty()) {

            log.info("-20246, 'Review Type - As the current Crown Court Rep Order Decision is Refused - Ineligible (applicants disposable income was assessed as ¿37,500 or more) you must select the appropriate review type - Eligibility Review, Miscalculation Review or New Application Following Ineligibility");

            throw new MeansAssessmentValidationException("-20246, 'Review Type - As the current Crown Court Rep Order Decision is Refused - Ineligible (applicants disposable income was assessed as ¿37,500 or more) you must select the appropriate review type - Eligibility Review, Miscalculation Review or New Application Following Ineligibility.");

        }




        // TODO - Few post processing needs to occur - Create a Post Assessment Service
                //TODO - process txn data - call MAAT API to perform CRUD on financial_assessments, fin_assessment_details,
                // fin_ass_child_weightings
                // process Old Assessment
                // full_assessment_available, post_assessment_processing,get_assessments_summary



    }
}

package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.client.WorkReasonsClient;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.exception.MeansAssessmentValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.AuthorizationResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentService {

    private final AssessmentCriteriaRepository assessmentCriteriaRepository;
    private final WorkReasonsClient workReasonsClient;

    protected List<AssessmentCriteriaEntity> getAssessmentCriteria(LocalDateTime assessmentDate, boolean hasPartner, boolean contraryInterest) throws AssessmentCriteriaNotFoundException {
        List<AssessmentCriteriaEntity> assessmentCriteriaForDate = assessmentCriteriaRepository.findAssessmentCriteriaForDate(assessmentDate);
        if (!assessmentCriteriaForDate.isEmpty()) {
            // If there is no partner or there is a partner with contrary interest, set partnerWeightingFactor to null
            if (!hasPartner || contraryInterest) {
                assessmentCriteriaForDate.forEach(ac -> ac.setPartnerWeightingFactor(null));
            }
            return assessmentCriteriaForDate;
        } else {
            log.error("No Assessment Criteria found for date {}", assessmentDate);
            throw new AssessmentCriteriaNotFoundException(String.format("No Assessment Criteria found for date %s", assessmentDate));
        }
    }

    public void createInitialAssessment(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) throws AssessmentCriteriaNotFoundException, MeansAssessmentValidationException {
        log.info("Create initial means assessment - Start");

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
    }


//    PROCEDURE check_init_assessment  (p_initial_object  IN  initial_assessmenttype) IS
//
//    BEGIN
//            IF p_initial_object.assessment_date IS NULL
//            OR p_initial_object.new_work_reason_object.code  IS NULL
//            then
//            raise_application_error(-20245,'Null mandatory fields');
//            END IF;
//
//            check_new_work_reason(p_user      => USER_ADMIN.GR_USER_SESSION.user_name
//            ,p_nwor_code => p_initial_object.new_work_reason_object.code);
//
//            IF p_application_object.crown_court_overview_object.crown_court_summary_object.cc_reporder_decision = 'Refused - Ineligible'
//            AND p_initial_object.review_type_object.code IS NULL  --placeholder just in case AND p_initial_object.result IS NULL
//            then
//            raise_application_error(-20246, 'Review Type - As the current Crown Court Rep Order Decision is Refused - Ineligible (applicants disposable income was assessed as ¿37,500 or more) you must select the appropriate review type - Eligibility Review, Miscalculation Review or New Application Following Ineligibility.');
//
//            END IF;
//
//            END check_init_assessment;


}

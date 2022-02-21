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

    private final AuthorisationMeansAssessmentClient workReasonsClient;

    private final InitialMeansAssessmentService initialMeansAssessmentService;
    private final FullMeansAssessmentService fullMeansAssessmentService;

    public void checkInitialAssessment(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) throws AssessmentCriteriaNotFoundException, MeansAssessmentValidationException {

        if (apiCreateMeansAssessmentRequest.getAssessmentDate() == null
                || apiCreateMeansAssessmentRequest.getNewWorkReason() == null
                || apiCreateMeansAssessmentRequest.getNewWorkReason().getCode() == null) {
            throw new MeansAssessmentValidationException("-20245,'Null mandatory fields'");
        }
        //todo: is the userId is same as session user name?
        AuthorizationResponse authorizationResponse = workReasonsClient.checkWorkReasonStatus(apiCreateMeansAssessmentRequest.getUserId(), apiCreateMeansAssessmentRequest.getNewWorkReason().getCode());
        //todo: missing condition - p_application_object.crown_court_overview_object.crown_court_summary_object.cc_reporder_decision = 'Refused - Ineligible'
        if (!authorizationResponse.isResult()
                || apiCreateMeansAssessmentRequest.getReviewType().getCode() == null
                || apiCreateMeansAssessmentRequest.getReviewType().getCode().isEmpty()) {

            initialMeansAssessmentService.createInitialAssessment(apiCreateMeansAssessmentRequest);
            log.info("-20246, 'Review Type - As the current Crown Court Rep Order Decision is Refused - Ineligible (applicants disposable income was assessed as ¿37,500 or more) you must select the appropriate review type - Eligibility Review, Miscalculation Review or New Application Following Ineligibility");
            //throw new MeansAssessmentValidationException("-20246, 'Review Type - As the current Crown Court Rep Order Decision is Refused - Ineligible (applicants disposable income was assessed as ¿37,500 or more) you must select the appropriate review type - Eligibility Review, Miscalculation Review or New Application Following Ineligibility.");

        }  else {
            fullMeansAssessmentService.createFullAssessment(apiCreateMeansAssessmentRequest);
        }
    }
}

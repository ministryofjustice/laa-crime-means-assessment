package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.client.AuthorisationMeansAssessmentClient;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.exception.MeansAssessmentValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.AuthorizationResponse;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;
import uk.gov.justice.laa.crime.meansassessment.validation.service.MeansAssessmentValidationService;
import uk.gov.justice.laa.crime.meansassessment.validation.validator.CreateAssessmentValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitialMeansAssessmentService {

    private final CreateAssessmentValidator createAssessmentValidator;
    private final AssessmentCriteriaRepository assessmentCriteriaRepository;

    public void createInitialAssessment(ApiCreateMeansAssessmentRequest apiCreateMeansAssessmentRequest) throws AssessmentCriteriaNotFoundException {

        log.info("Starting initial means assessment");
        createAssessmentValidator.validate(apiCreateMeansAssessmentRequest);
        log.info("Validation completed for Rep ID {}", apiCreateMeansAssessmentRequest.getRepId());
        getAssessmentCriteria(apiCreateMeansAssessmentRequest.getAssessmentDate(),
                apiCreateMeansAssessmentRequest.getHasPartner(),
                apiCreateMeansAssessmentRequest.getPartnerContraryInterest());

    }

    private List<AssessmentCriteriaEntity> getAssessmentCriteria(LocalDateTime assessmentDate, boolean hasPartner, boolean contraryInterest) throws AssessmentCriteriaNotFoundException {
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
}
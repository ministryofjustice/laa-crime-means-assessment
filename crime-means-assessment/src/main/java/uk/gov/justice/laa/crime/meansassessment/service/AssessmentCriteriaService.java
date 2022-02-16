package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentCriteriaService {

    private final AssessmentCriteriaRepository assessmentCriteriaRepository;

    protected AssessmentCriteriaEntity getAssessmentCriteria(LocalDateTime assessmentDate, boolean hasPartner, boolean contraryInterest) {
        AssessmentCriteriaEntity assessmentCriteriaForDate = assessmentCriteriaRepository.findAssessmentCriteriaForDate(assessmentDate);
        if (assessmentCriteriaForDate != null) {
            // If there is no partner or there is a partner with contrary interest, set partnerWeightingFactor to null
            if (!hasPartner || contraryInterest) {
                assessmentCriteriaForDate.setPartnerWeightingFactor(BigDecimal.ZERO);
            }
            return assessmentCriteriaForDate;
        } else {
            log.error("No Assessment Criteria found for date {}", assessmentDate);
            throw new AssessmentCriteriaNotFoundException(String.format("No Assessment Criteria found for date %s", assessmentDate));
        }
    }
}

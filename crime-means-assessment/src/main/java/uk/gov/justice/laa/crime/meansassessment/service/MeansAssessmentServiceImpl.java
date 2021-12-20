package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeansAssessmentServiceImpl implements MeansAssessmentService{

    @Autowired
    private AssessmentCriteriaRepository assessmentCriteriaRepository;

    @Override
    public List<AssessmentCriteriaEntity> getAssessmentCriteria(LocalDateTime assessmentDate, boolean hasPartner, boolean contraryInterest) throws AssessmentCriteriaNotFoundException {
        List<AssessmentCriteriaEntity> assessmentCriteriaForDate = assessmentCriteriaRepository.findAssessmentCriteriaForDate(assessmentDate);
        if(!assessmentCriteriaForDate.isEmpty()){
            // If there is no partner or there is a partner with contrary interest, set partnerWeightingFactor to null
            if(!hasPartner || (hasPartner && contraryInterest)){
                assessmentCriteriaForDate.forEach(ac -> ac.setPartnerWeightingFactor(null));
            }
            return assessmentCriteriaForDate;
        } else {
            log.error("No Assessment Criteria found for date {}", assessmentDate);
            throw new AssessmentCriteriaNotFoundException(String.format("No Assessment Criteria found for date %s",assessmentDate));
        }
    }
}

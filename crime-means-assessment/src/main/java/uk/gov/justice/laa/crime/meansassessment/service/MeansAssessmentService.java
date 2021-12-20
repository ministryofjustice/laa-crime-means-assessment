package uk.gov.justice.laa.crime.meansassessment.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.crime.meansassessment.exception.AssessmentCriteriaNotFoundException;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface MeansAssessmentService {

    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_COMMITTED, readOnly = true)
    List<AssessmentCriteriaEntity> getAssessmentCriteria(LocalDateTime assessmentDate, boolean hasPartner, boolean contraryInterest) throws AssessmentCriteriaNotFoundException;
}

package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.CaseTypeAssessmentCriteriaDetailValueEntity;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseTypeAssessmentCriteriaDetailValueRepository
        extends CrudRepository<CaseTypeAssessmentCriteriaDetailValueEntity, Integer> {
    Optional<CaseTypeAssessmentCriteriaDetailValueEntity> findByAssessmentCriteriaDetailAndCaseType(
            AssessmentCriteriaDetailEntity assessmentCriteriaDetail, CaseType caseType);
}

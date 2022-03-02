package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.CaseTypeAssessmentCriteriaDetailValueEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.CaseType;

import java.util.Optional;

@Repository
public interface CaseTypeAssessmentCriteriaDetailValueRepository extends CrudRepository<CaseTypeAssessmentCriteriaDetailValueEntity, Integer> {
    Optional<CaseTypeAssessmentCriteriaDetailValueEntity> findByAssessmentCriteriaDetailAndCaseType(AssessmentCriteriaDetailEntity assessmentCriteriaDetail, CaseType caseType);

}

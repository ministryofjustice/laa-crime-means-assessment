package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailFrequencyEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.enums.Frequency;

import java.util.Optional;

@Repository
public interface AssessmentCriteriaDetailFrequencyRepository extends CrudRepository<AssessmentCriteriaDetailFrequencyEntity, Integer> {
    Optional<AssessmentCriteriaDetailFrequencyEntity> findByAssessmentCriteriaDetailAndFrequency(AssessmentCriteriaDetailEntity criteriaDetail, Frequency frequency);
}

package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import uk.gov.justice.laa.crime.enums.Frequency;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailFrequencyEntity;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentCriteriaDetailFrequencyRepository
        extends CrudRepository<AssessmentCriteriaDetailFrequencyEntity, Integer> {
    Optional<AssessmentCriteriaDetailFrequencyEntity> findByAssessmentCriteriaDetailAndFrequency(
            AssessmentCriteriaDetailEntity criteriaDetail, Frequency frequency);
}

package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentCriteriaChildWeightingRepository
        extends CrudRepository<AssessmentCriteriaChildWeightingEntity, Integer> {}

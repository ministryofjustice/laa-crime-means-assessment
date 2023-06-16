package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;

@Repository
public interface AssessmentCriteriaChildWeightingRepository extends CrudRepository<AssessmentCriteriaChildWeightingEntity, Integer> {

}

package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;

@Repository
public interface AssessmentCriteriaDetailRepository extends CrudRepository<AssessmentCriteriaDetailEntity, Integer> {

}

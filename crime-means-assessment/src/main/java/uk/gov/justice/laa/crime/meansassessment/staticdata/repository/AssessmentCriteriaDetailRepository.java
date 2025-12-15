package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaDetailEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentCriteriaDetailRepository extends CrudRepository<AssessmentCriteriaDetailEntity, Integer> {}

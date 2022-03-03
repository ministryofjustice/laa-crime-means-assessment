package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AssessmentCriteriaChildWeightingRepository extends CrudRepository<AssessmentCriteriaChildWeightingEntity, Integer> {

    List<AssessmentCriteriaChildWeightingEntity> findByLowerAgeRangeGreaterThanEqual(Integer lowerAgeRangeFrom);
    List<AssessmentCriteriaChildWeightingEntity> findByUpperAgeRangeIsLessThanEqual(Integer upperAgeRangeTo);
    List<AssessmentCriteriaChildWeightingEntity> findByLowerAgeRangeGreaterThanEqualAndUpperAgeRangeIsLessThanEqual(Integer lowerAgeRangeFrom, Integer upperAgeRangeTo);

    List<AssessmentCriteriaChildWeightingEntity> findByWeightingFactor(BigDecimal weightFactor);
}

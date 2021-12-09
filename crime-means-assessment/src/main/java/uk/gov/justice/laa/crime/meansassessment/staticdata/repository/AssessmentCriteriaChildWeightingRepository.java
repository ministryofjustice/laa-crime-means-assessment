package uk.gov.justice.laa.crime.meansassessment.staticdata.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AssessmentCriteriaChildWeightingRepository extends CrudRepository<AssessmentCriteriaChildWeightingEntity, Long> {

    List<AssessmentCriteriaChildWeightingEntity> findByLowerAgeRangeGreaterThanEqual(BigDecimal lowerAgeRangeFrom);
    List<AssessmentCriteriaChildWeightingEntity> findByUpperAgeRangeIsLessThanEqual(BigDecimal upperAgeRangeTo);
    List<AssessmentCriteriaChildWeightingEntity> findByLowerAgeRangeGreaterThanEqualAndUpperAgeRangeIsLessThanEqual(BigDecimal lowerAgeRangeFrom,BigDecimal upperAgeRangeTo);

    List<AssessmentCriteriaChildWeightingEntity> findByWeightingFactor(BigDecimal weightFactor);
}

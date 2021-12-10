package uk.gov.justice.laa.crime.meansassessment.staticdata.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaChildWeightingRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class AssessmentCriteriaChildWeightingService {
    private final AssessmentCriteriaChildWeightingRepository assessmentCriteriaChildWeightingRepository;

    public AssessmentCriteriaChildWeightingEntity findById(Long assessmentCriteriaChildWeightingId){
        return assessmentCriteriaChildWeightingRepository.findById(assessmentCriteriaChildWeightingId).orElse(null);
    }


    public List<AssessmentCriteriaChildWeightingEntity> findAll() {
        List<AssessmentCriteriaChildWeightingEntity> results = new ArrayList<>();
        Iterable<AssessmentCriteriaChildWeightingEntity> queryResult = assessmentCriteriaChildWeightingRepository.findAll();
        if(queryResult.iterator().hasNext()) {
            results = StreamSupport.stream(queryResult.spliterator(), false).collect(Collectors.toList());
        }
        return results;
    }

    public List<AssessmentCriteriaChildWeightingEntity> findByLowerAgeRangeGreaterThanEqual(BigDecimal lowerAgeRangeFrom){
        return assessmentCriteriaChildWeightingRepository.findByLowerAgeRangeGreaterThanEqual(lowerAgeRangeFrom);
    }
    public List<AssessmentCriteriaChildWeightingEntity> findByUpperAgeRangeIsLessThanEqual(BigDecimal upperAgeRangeTo) {
        return assessmentCriteriaChildWeightingRepository.findByUpperAgeRangeIsLessThanEqual(upperAgeRangeTo);
    }
    public List<AssessmentCriteriaChildWeightingEntity> findByLowerAgeRangeGreaterThanEqualAndUpperAgeRangeIsLessThanEqual(BigDecimal lowerAgeRangeFrom,BigDecimal upperAgeRangeTo){
        return assessmentCriteriaChildWeightingRepository.findByLowerAgeRangeGreaterThanEqualAndUpperAgeRangeIsLessThanEqual(lowerAgeRangeFrom, upperAgeRangeTo);
    }

    public List<AssessmentCriteriaChildWeightingEntity> findByWeightingFactor(BigDecimal weightFactor){
        return assessmentCriteriaChildWeightingRepository.findByWeightingFactor(weightFactor);
    }

}

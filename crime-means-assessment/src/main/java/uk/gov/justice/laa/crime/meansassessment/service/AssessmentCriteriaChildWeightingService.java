package uk.gov.justice.laa.crime.meansassessment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.model.common.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AssessmentCriteriaChildWeightingService {

    protected BigDecimal getTotalChildWeighting(List<ApiAssessmentChildWeighting> childWeightings, AssessmentCriteriaEntity assessmentCriteria) {
        Set<AssessmentCriteriaChildWeightingEntity> criteriaChildWeightings = assessmentCriteria.getAssessmentCriteriaChildWeightings();

        if (criteriaChildWeightings.size() != childWeightings.size()) {
            throw new ValidationException(String.format("Child weightings missing for criteria: %d", assessmentCriteria.getId()));
        }

        BigDecimal totalChildWeighting = BigDecimal.ZERO;

        for (ApiAssessmentChildWeighting weighting : childWeightings) {
            AssessmentCriteriaChildWeightingEntity childWeighting = criteriaChildWeightings.stream().filter(
                    cw -> cw.getId().equals(weighting.getChildWeightingId())
            ).findFirst().orElseThrow(
                    () -> new ValidationException(String.format("Invalid child weighting id: %s", weighting.getChildWeightingId()))
            );

            totalChildWeighting = totalChildWeighting.add(
                    childWeighting.getWeightingFactor().multiply(
                            BigDecimal.valueOf(weighting.getNoOfChildren())
                    )
            );
        }
        return totalChildWeighting;
    }
}

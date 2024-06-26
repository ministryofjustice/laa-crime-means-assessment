package uk.gov.justice.laa.crime.meansassessment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiAssessmentChildWeighting;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.exception.ValidationException;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AssessmentCriteriaChildWeightingServiceTest {

    @InjectMocks
    private AssessmentCriteriaChildWeightingService criteriaChildWeightingService;

    private AssessmentCriteriaEntity assessmentCriteria;

    @BeforeEach
    void setUp() {
        assessmentCriteria = TestModelDataBuilder.getAssessmentCriteriaEntityWithChildWeightings(
                new BigDecimal[]{
                        BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.35)
                }
        );
        assessmentCriteria.setId(1000);
    }

    @Test
    void givenIncorrectNumberOfChildWeightings_whenGetTotalChildWeightingIsInvoked_thenThrowsException() {
        List<ApiAssessmentChildWeighting> childWeightings = new ArrayList<>();
        assertThatThrownBy(
                () -> criteriaChildWeightingService.getTotalChildWeighting(childWeightings, assessmentCriteria)
        ).isInstanceOf(ValidationException.class).hasMessageContaining(
                String.format("Child weightings missing for criteria: %d", assessmentCriteria.getId())
        );
    }

    @Test
    void givenMissingChildWeighting_whenGetTotalChildWeightingIsInvoked_thenThrowsException() {
        List<ApiAssessmentChildWeighting> childWeightings = TestModelDataBuilder.getAssessmentChildWeightings();
        childWeightings.get(0).setChildWeightingId(0);
        assertThatThrownBy(
                () -> criteriaChildWeightingService.getTotalChildWeighting(childWeightings, assessmentCriteria)
        ).isInstanceOf(ValidationException.class).hasMessageContaining("Invalid child weighting id:");
    }

    @Test
    void givenValidChildWeightings_whenGetTotalChildWeightingIsInvoked_thenReturnTotalWeighting() {
        BigDecimal expectedWeighting = BigDecimal.valueOf(0.85);
        List<ApiAssessmentChildWeighting> childWeightings = TestModelDataBuilder.getAssessmentChildWeightings();
        assertThat(criteriaChildWeightingService.getTotalChildWeighting(childWeightings, assessmentCriteria)).isEqualTo(expectedWeighting);
    }
}

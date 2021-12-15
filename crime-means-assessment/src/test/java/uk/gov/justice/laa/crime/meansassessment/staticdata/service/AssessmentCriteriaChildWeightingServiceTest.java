package uk.gov.justice.laa.crime.meansassessment.staticdata.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaChildWeightingEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaChildWeightingRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentCriteriaChildWeightingServiceTest {

    private static final long VALID_ASSESSMENT_CRITERIA_CHILD_WEIGHTING_ID = 10000l;
    private static final long INVALID_ASSESSMENT_CRITERIA_CHILD_WEIGHTING_ID = 20000l;
    private static final BigDecimal INVALID_LOWER_AGE_RANGE = BigDecimal.valueOf(99L);
    private static final BigDecimal INVALID_UPPER_AGE_RANGE = BigDecimal.ZERO;
    private static final BigDecimal INVALID_WEIGHTING_FACTOR = BigDecimal.valueOf(99L);


    @InjectMocks
    private AssessmentCriteriaChildWeightingService assessmentCriteriaChildWeightingService;

    @Mock
    private AssessmentCriteriaChildWeightingRepository assessmentCriteriaChildWeightingRepository;

    private AssessmentCriteriaChildWeightingEntity assessmentCriteriaChildWeightingEntity;

    @Before
    public void setUp() {
        //given
        assessmentCriteriaChildWeightingEntity = TestModelDataBuilder.getAssessmentCriteriaChildWeightingEntity();
        assessmentCriteriaChildWeightingEntity.setId(VALID_ASSESSMENT_CRITERIA_CHILD_WEIGHTING_ID);
    }
    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenAllRecordsAreRequestThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting is populated
        when(assessmentCriteriaChildWeightingRepository.findAll()).thenReturn(List.of(assessmentCriteriaChildWeightingEntity));
        // when all Assessment Criteria Child Weighting are requested
        var results = assessmentCriteriaChildWeightingService.findAll();
        // then expected number of results are returned
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsNotPopulatedWhenAllRecordsAreRequestThenAssessmentCriteriaChildWeightingIsNotReturned(){
        // given Assessment Criteria Child Weighting is not populated
        // when all Assessment Criteria Child Weighting are requested
        var results = assessmentCriteriaChildWeightingService.findAll();
        // then no results are returned
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenCorrectIdIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting is populated
        when(assessmentCriteriaChildWeightingRepository.findById(VALID_ASSESSMENT_CRITERIA_CHILD_WEIGHTING_ID)).thenReturn(Optional.of(assessmentCriteriaChildWeightingEntity));
        // when all Assessment Criteria Child Weighting are requested
        var result = assessmentCriteriaChildWeightingService.findById(VALID_ASSESSMENT_CRITERIA_CHILD_WEIGHTING_ID);
        // then expected Assessment Criteria Child Weighting is returned
        assertNotNull(result);
        assertEquals(assessmentCriteriaChildWeightingEntity, result);
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenIncorrectIdIsProvidedThenAssessmentCriteriaChildWeightingIsNotReturned(){
        // given Assessment Criteria Child Weighting is populated
        when(assessmentCriteriaChildWeightingRepository.findById(INVALID_ASSESSMENT_CRITERIA_CHILD_WEIGHTING_ID)).thenReturn(Optional.empty());
        // when all Assessment Criteria Child Weighting are requested
        var result = assessmentCriteriaChildWeightingService.findById(INVALID_ASSESSMENT_CRITERIA_CHILD_WEIGHTING_ID);
        // then expected Assessment Criteria Child Weighting is returned
        assertNull(result);
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenValidLowerAgeRangeIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting is populated and matching results are returned
        when(assessmentCriteriaChildWeightingRepository.findByLowerAgeRangeGreaterThanEqual(any(BigDecimal.class))).thenReturn(List.of(assessmentCriteriaChildWeightingEntity));
        // when Assessment Criteria Child Weighting valid after a certain time are requested
        var results = assessmentCriteriaChildWeightingService.findByLowerAgeRangeGreaterThanEqual(assessmentCriteriaChildWeightingEntity.getLowerAgeRange());
        // then expected Assessment Criteria Child Weighting are returned
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(assessmentCriteriaChildWeightingEntity, results.get(0));
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenInvalidLowerAgeRangeIsProvidedThenAssessmentCriteriaChildWeightingShouldNotBeReturned(){
        // given Assessment Criteria Child Weighting is populated and matching results are returned
        when(assessmentCriteriaChildWeightingRepository.findByLowerAgeRangeGreaterThanEqual(any(BigDecimal.class))).thenReturn(List.of());
        // when Assessment Criteria Child Weighting valid after a certain time are requested
        var results = assessmentCriteriaChildWeightingService.findByLowerAgeRangeGreaterThanEqual(INVALID_LOWER_AGE_RANGE);
        // then expected Assessment Criteria Child Weighting are returned
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenValidUpperAgeRangeIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting is populated and matching results are returned
        when(assessmentCriteriaChildWeightingRepository.findByUpperAgeRangeIsLessThanEqual(any(BigDecimal.class))).thenReturn(List.of(assessmentCriteriaChildWeightingEntity));
        // when Assessment Criteria Child Weighting valid after a certain time are requested
        var results = assessmentCriteriaChildWeightingService.findByUpperAgeRangeIsLessThanEqual(assessmentCriteriaChildWeightingEntity.getLowerAgeRange());
        // then expected Assessment Criteria Child Weighting are returned
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(assessmentCriteriaChildWeightingEntity, results.get(0));
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenInvalidUpperAgeRangeIsProvidedThenAssessmentCriteriaChildWeightingShouldNotBeReturned(){
        // given Assessment Criteria Child Weighting is populated and matching results are returned
        when(assessmentCriteriaChildWeightingRepository.findByUpperAgeRangeIsLessThanEqual(any(BigDecimal.class))).thenReturn(List.of());
        // when Assessment Criteria Child Weighting valid after a certain time are requested
        var results = assessmentCriteriaChildWeightingService.findByUpperAgeRangeIsLessThanEqual(INVALID_UPPER_AGE_RANGE);
        // then expected Assessment Criteria Child Weighting are returned
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenValidUpperAgeRangeAndLowerAgeRangeIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting is populated and matching results are returned
        when(assessmentCriteriaChildWeightingRepository.findByLowerAgeRangeGreaterThanEqualAndUpperAgeRangeIsLessThanEqual(any(BigDecimal.class),any(BigDecimal.class))).thenReturn(List.of(assessmentCriteriaChildWeightingEntity));
        // when Assessment Criteria Child Weighting valid after a certain time are requested
        var results = assessmentCriteriaChildWeightingService.findByLowerAgeRangeGreaterThanEqualAndUpperAgeRangeIsLessThanEqual(assessmentCriteriaChildWeightingEntity.getLowerAgeRange(),assessmentCriteriaChildWeightingEntity.getUpperAgeRange() );
        // then expected Assessment Criteria Child Weighting are returned
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(assessmentCriteriaChildWeightingEntity, results.get(0));
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenInvalidUpperAgeRangeAndInvalidLowerAgeRangeIsProvidedThenAssessmentCriteriaChildWeightingShouldNotBeReturned(){
        // given Assessment Criteria Child Weighting is populated and matching results are returned
        when(assessmentCriteriaChildWeightingRepository.findByLowerAgeRangeGreaterThanEqualAndUpperAgeRangeIsLessThanEqual(any(BigDecimal.class),any(BigDecimal.class))).thenReturn(List.of());
        // when Assessment Criteria Child Weighting valid after a certain time are requested
        var results = assessmentCriteriaChildWeightingService.findByLowerAgeRangeGreaterThanEqualAndUpperAgeRangeIsLessThanEqual(INVALID_LOWER_AGE_RANGE, INVALID_UPPER_AGE_RANGE);
        // then expected Assessment Criteria Child Weighting are returned
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenValidWeightingFactorIsProvidedThenAssessmentCriteriaChildWeightingShouldBeReturned(){
        // given Assessment Criteria Child Weighting is populated and matching results are returned
        when(assessmentCriteriaChildWeightingRepository.findByWeightingFactor(any(BigDecimal.class))).thenReturn(List.of(assessmentCriteriaChildWeightingEntity));
        // when Assessment Criteria Child Weighting valid after a certain time are requested
        var results = assessmentCriteriaChildWeightingService.findByWeightingFactor(assessmentCriteriaChildWeightingEntity.getWeightingFactor());
        // then expected Assessment Criteria Child Weighting are returned
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(assessmentCriteriaChildWeightingEntity, results.get(0));
    }

    @Test
    public void givenAssessmentCriteriaChildWeightingIsPopulatedWhenInvalidWeightingFactorIsProvidedThenAssessmentCriteriaChildWeightingShouldNotBeReturned(){
        // given Assessment Criteria Child Weighting is populated and matching results are returned
        when(assessmentCriteriaChildWeightingRepository.findByWeightingFactor(any(BigDecimal.class))).thenReturn(List.of());
        // when Assessment Criteria Child Weighting valid after a certain time are requested
        var results = assessmentCriteriaChildWeightingService.findByWeightingFactor(INVALID_WEIGHTING_FACTOR);
        // then expected Assessment Criteria Child Weighting are returned
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }
}
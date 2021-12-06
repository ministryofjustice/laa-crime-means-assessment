package uk.gov.justice.laa.crime.meansassessment.staticdata.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.justice.laa.crime.meansassessment.staticdata.entity.AssessmentCriteriaEntity;
import uk.gov.justice.laa.crime.meansassessment.staticdata.repository.AssessmentCriteriaRepository;
import uk.gov.justice.laa.crime.meansassessment.data.builder.TestModelDataBuilder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentCriteriaServiceTest {

    private static final long VALID_ASSESSMENT_CRITERIA_ID = 10000l;
    private static final long INVALID_ASSESSMENT_CRITERIA_ID = 20000l;

    @InjectMocks
    private AssessmentCriteriaService assessmentCriteriaService;

    @Mock
    private AssessmentCriteriaRepository assessmentCriteriaRepository;

    private AssessmentCriteriaEntity assessmentCriteriaEntity;

    @Before
    public void setUp() {
        //given
        assessmentCriteriaEntity = TestModelDataBuilder.getAssessmentCriteriaEntity();
        assessmentCriteriaEntity.setId(VALID_ASSESSMENT_CRITERIA_ID);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenAllRecordsAreRequestThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria is populated
        when(assessmentCriteriaRepository.findAll()).thenReturn(Arrays.asList(new AssessmentCriteriaEntity[]{assessmentCriteriaEntity}));
        // when all Assessment Criteria are requested
        List<AssessmentCriteriaEntity> results = assessmentCriteriaService.findAll();
        // then expected number of results are returned
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    public void givenAssessmentCriteriaIsNotPopulatedWhenAllRecordsAreRequestThenAssessmentCriteriaIsNotReturned(){
        // given Assessment Criteria is not populated
        // when all Assessment Criteria are requested
        List<AssessmentCriteriaEntity> results = assessmentCriteriaService.findAll();
        // then no results are returned
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenCorrectIdIsProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria is populated
        when(assessmentCriteriaRepository.findById(VALID_ASSESSMENT_CRITERIA_ID)).thenReturn(Optional.of(assessmentCriteriaEntity));
        // when all Assessment Criteria are requested
        AssessmentCriteriaEntity result = assessmentCriteriaService.findById(VALID_ASSESSMENT_CRITERIA_ID);
        // then expected Assessment Criteria is returned
        assertNotNull(result);
        assertEquals(assessmentCriteriaEntity, result);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenIncorrectIdIsProvidedThenAssessmentCriteriaIsNotReturned(){
        // given Assessment Criteria is populated
        when(assessmentCriteriaRepository.findById(INVALID_ASSESSMENT_CRITERIA_ID)).thenReturn(Optional.empty());
        // when all Assessment Criteria are requested
        AssessmentCriteriaEntity result = assessmentCriteriaService.findById(INVALID_ASSESSMENT_CRITERIA_ID);
        // then expected Assessment Criteria is returned
        assertNull(result);
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateFromIsProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findByDateFromAfter(any(LocalDateTime.class))).thenReturn(Arrays.asList(new AssessmentCriteriaEntity[]{assessmentCriteriaEntity}));
        // when Assessment Criteria valid after a certain time are requested
        List<AssessmentCriteriaEntity> results = assessmentCriteriaService.findAssessmentCriteriaApplicableAfter(assessmentCriteriaEntity.getDateFrom().minusHours(1));
        // then expected Assessment Criteria are returned
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(assessmentCriteriaEntity, results.get(0));
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenInvalidDateFromIsProvidedThenAssessmentCriteriaIsNotReturned(){
        // given Assessment Criteria is populated and no matching results are returned
        when(assessmentCriteriaRepository.findByDateFromAfter(any(LocalDateTime.class))).thenReturn(Arrays.asList(new AssessmentCriteriaEntity[]{}));
        // when Assessment Criteria valid after a certain time are requested
        List<AssessmentCriteriaEntity> results = assessmentCriteriaService.findAssessmentCriteriaApplicableAfter(assessmentCriteriaEntity.getDateFrom().plusHours(1));
        // then no results are returned
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateToIsProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findByDateToBefore(any(LocalDateTime.class))).thenReturn(Arrays.asList(new AssessmentCriteriaEntity[]{assessmentCriteriaEntity}));
        // when Assessment Criteria valid after a certain time are requested
        List<AssessmentCriteriaEntity> results = assessmentCriteriaService.findAssessmentCriteriaApplicableBefore(assessmentCriteriaEntity.getDateTo().plusHours(1));
        // then expected Assessment Criteria are returned
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(assessmentCriteriaEntity, results.get(0));
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenInvalidDateToIsProvidedThenAssessmentCriteriaIsNotReturned(){
        // given Assessment Criteria is populated and no matching results are returned
        when(assessmentCriteriaRepository.findByDateToBefore(any(LocalDateTime.class))).thenReturn(Arrays.asList(new AssessmentCriteriaEntity[]{}));
        // when Assessment Criteria valid before a certain time are requested
        List<AssessmentCriteriaEntity> results = assessmentCriteriaService.findAssessmentCriteriaApplicableBefore(assessmentCriteriaEntity.getDateTo().minusHours(1));
        // then no results are returned
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenValidDateFromAndDateToAreProvidedThenAssessmentCriteriaShouldBeReturned(){
        // given Assessment Criteria is populated and matching results are returned
        when(assessmentCriteriaRepository.findByDateFromAfterAndDateToBefore(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Arrays.asList(new AssessmentCriteriaEntity[]{assessmentCriteriaEntity}));
        // when Assessment Criteria valid between two instants are requested
        List<AssessmentCriteriaEntity> results = assessmentCriteriaService.findAssessmentCriteriaApplicableBetween(assessmentCriteriaEntity.getDateFrom().minusHours(1), assessmentCriteriaEntity.getDateTo().plusHours(1));
        // then expected Assessment Criteria are returned
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(assessmentCriteriaEntity, results.get(0));
    }

    @Test
    public void givenAssessmentCriteriaIsPopulatedWhenInvalidDateFromAndDateToAreProvidedThenAssessmentCriteriaIsNotReturned(){
        // given Assessment Criteria is populated and no matching results are returned
        when(assessmentCriteriaRepository.findByDateFromAfterAndDateToBefore(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Arrays.asList(new AssessmentCriteriaEntity[]{}));
        // when Assessment Criteria valid before a certain time are requested
        List<AssessmentCriteriaEntity> results = assessmentCriteriaService.findAssessmentCriteriaApplicableBetween(assessmentCriteriaEntity.getDateFrom().plusHours(1), assessmentCriteriaEntity.getDateTo().minusHours(1));
        // then no results are returned
        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

}